package re._1r.spigot
package compilation

import org.bukkit._
import inventory.meta.BookMeta
import block.Comparator
import scala.collection.Map
import plugin.Plugin

object CompileBook{
  import McIcRegex._
  import Error._
  def getAllIfs(allText: String): List[Any] = {
    /*if(IF_STATEMENT.matches(allText)) println("allText")
    else println(allText)*/
    List()
  }
  def compileComparator(book: BookMeta, comparator: Comparator, plugin: Plugin):String = {
    if(book.getTitle == "++CODE--"){
      val lines = book.getPages.toArray.foldLeft(Array[String]())((acc, v) => {
        acc :++ (v.asInstanceOf[String].split('\n').map(_.trim))
      }).filterNot(line => line.take(2) == "//" || line == "")

      if(lines.length == 0){
        return errors(ERROR_EMPTY_BOOK)
      }
      val analogue = lines(0) match {
        case ANALOGUE_DECLARATION(_*) => true
        case DIGITAL_DECLARATION(_*) => false
        case _ => return errors(ERROR_UNDEFINED_MODE)
      }
      val allText = lines.tail.tail.mkString("\n")
      val splitAtRun = allText.split("\n#run\n")
      val initLines = if(lines(1)=="#init") splitAtRun(0) else ""
      val runLines = {
        if(splitAtRun.length == 2){
          splitAtRun(1)
        }
        else if(lines.exists(_ == "#run")){
          //RUN CODE HERE
        }
        else {
          return errors(ERROR_NO_RUN)
        }
      }
      if(initLines != ""){
        val exitCode = comparatorInit(initLines.split('\n'), analogue, comparator, plugin)
        if(exitCode != 0){
          return errors(exitCode)
        }
      }
      val functionAndExitCode = comparatorFunction(lines)
      def exitCode = functionAndExitCode._1
      def  function = functionAndExitCode._2
      if(exitCode >= 0){
        Error.errors(exitCode)
      }
      else{
        //No errors in function
        comparator.update
        ""
      }
    } else "" //Book isn't code
  }

  def comparatorInit(initLines: Array[String], analogue: Boolean, comparator:Comparator, plugin: Plugin): Int = {
    if(initLines.forall(line => {
      VAL_INIT_DECLARATION.matches(line) || VAL_LITERAL_DECLARATION.matches(line)
    })){
      println("0")
      val variables = initLines.map(line => line match{
        case VAL_LITERAL_DECLARATION(_*) => {
          val declaration = line.drop(4).split(" = ")
          def valName = declaration(0)
          def valString = declaration(1)
          if(!valString.forall(_.isDigit)) return ERROR_INIT_INT
          val valInt = if(analogue){
            if(valString.toInt >= 15) "15"
            else if(valString.toInt > 0) valString
            else "0"
          }
          else{
            if(valString.toInt == 0) "0" else "1"
          }
          (valName, valInt)
        }
        case VAL_INIT_DECLARATION(_*) => {
          println("1")
          def valName = line.drop(4)
          (valName, "0")
        }
      })
      import persistentcircuits._
      import persistence.PersistentDataType
      variables.foreach(variable => {
        val NSK = new NamespacedKey(plugin, variable._1)
        comparator.getPersistentDataContainer.set(NSK, PersistentDataType.STRING,variable._2)
        println("stored " + variable._2 + " as " + variable._1)
        println("Check: " + comparator.getPersistentDataContainer.get(NSK, PersistentDataType.STRING))
        println(variable._1)
        println(variable._2)
      })
      val NSK = new NamespacedKey(plugin, "val")
      val vals = variables.map(_._1).mkString("\n")
      comparator.getPersistentDataContainer.set(NSK, PersistentDataType.STRING, vals)
      0
    }
    else{
      ERROR_INIT_DECLARATION
    }
  }

  def comparatorFunction(lines: Array[String]): (Int, (Int, Int, Map[String, Int]) => Int) = {
    

    return(-1, (a: Int, b: Int, variables: Map[String, Int]) => 0)
  }
}

object McIcRegex {
  //Mode Declarations
  val DIGITAL_DECLARATION = "using digital".r
  val ANALOGUE_DECLARATION = "using ((analogue)|(analog))".r
  //If Statement
  val IF_STATEMENT = "if[(](-?[0-9]+)|([a-zA-z]+) == (-?[0-9]+)|([a-zA-z]+)[)]\\{\n.*\n\\}".r.unanchored
  //Declarations & Operators
  val VAL_INIT_DECLARATION = "val [a-zA-Z]+".r
  val VAL_ADD_OR_DECLARATION = "val [a-zA-Z]+ = (-?[0-9]+)|([a-zA-z]+) + (-?[0-9]+)|([a-zA-z]+)".r
  val VAL_MUL_AND_DECLARATION = "val [a-zA-Z]+ = (-?[0-9]+)|([a-zA-z]+) * (-?[0-9]+)|([a-zA-z]+)".r
  val VAL_SUB_XOR_DECLARATION = "val [a-zA-Z]+ = (-?[0-9]+)|([a-zA-z]+) - (-?[0-9]+)|([a-zA-z]+)".r
  val VAL_DIV_NAND_DECLARATION = "val [a-zA-Z]+ = (-?[0-9]+)|([a-zA-z]+) / (-?[0-9]+)|([a-zA-z]+)".r
  val VAL_NOT_DECLARATION = "val [a-zA-Z]+ = ![(](-?[0-9]+)|([a-zA-z]+)[)]".r
  val VAL_LITERAL_DECLARATION = "val [a-zA-Z]+ = (-?[0-9]+)|([a-zA-z]+)".r
}

object Error {
  val errors = Array(
    "Error: No digital/analogue declaration",
    "Error: Book is empty",
    "Error: #run not found",
    "Error: Non integer format found in #init",
    "Error: Only literal declarations are allowed in #init"
  )
  def ERROR_UNDEFINED_MODE = 0
  def ERROR_EMPTY_BOOK = 1
  def ERROR_NO_RUN = 2
  def ERROR_INIT_INT = 3
  def ERROR_INIT_DECLARATION = 4
}