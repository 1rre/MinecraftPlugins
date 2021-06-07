package es.tmoor.minecraft

import util.parsing.combinator.RegexParsers
import scala.util.matching.Regex
import es.tmoor.minecraft.CompileBook.Construct
import es.tmoor.minecraft.CompileBook.Construct
import es.tmoor.minecraft.CompileBook.Variable
import es.tmoor.minecraft.CompileBook.Construct
import es.tmoor.minecraft.CompileBook.Construct
import es.tmoor.minecraft.CompileBook.InitDeclaration
import es.tmoor.minecraft.CompileBook.IntValue
import es.tmoor.minecraft.CompileBook.Assignment
import es.tmoor.minecraft.CompileBook.IfCase
import es.tmoor.minecraft.CompileBook.Statement
import es.tmoor.minecraft.CompileBook.IfStatement
import collection.mutable.HashMap
import es.tmoor.minecraft.CompileBook.Construct
import es.tmoor.minecraft.CompileBook.Operation
import es.tmoor.minecraft.CompileBook.LoopDeclaration
import es.tmoor.minecraft.CompileBook.Construct
import es.tmoor.minecraft.CompileBook.ValueNoOp
import scala.annotation.varargs
import org.bukkit.scheduler.BukkitRunnable

object CompileBook extends RegexParsers {
  override def skipWhitespace = true
  trait Token
  trait Construct[T <: Token] extends Parser[T] {
    def parser: Parser[T]
    def apply(in: Input) = parser(in)
  }

  abstract trait ModeDeclaration extends Token

  trait ICRunner {
    def resolveValue(v: Value, vars: HashMap[String,Int]): Int = v match {
      case IntValue(i) => i
      case Variable(s) => vars.getOrElseUpdate(s, 0)
      case o: Operation => o(resolveValue(o.l,vars), resolveValue(o.r,vars))
    }
    def resolveStatementSeq(s: Seq[Statement], vars: HashMap[String,Int]): Int = {
      s.foldLeft(0)((_,st) => {
        resolveStatement(st, vars)
      })
    }
    def resolveStatement(st: Statement, vars: HashMap[String,Int]): Int = st match {
      case IfStatement(cases) => {
        println(cases)
        cases.collectFirst {
          case cs if resolveValue(cs.predicate, vars) != 0 => {
            println(s"resolving case of ${cs.predicate}")
            resolveStatementSeq(cs.statements, vars)
          }
        }.getOrElse(0)
      }
      case Assignment(Variable(name), value) => {vars += name -> resolveValue(value, vars); vars(name)}
      case Variable(name) => vars.getOrElseUpdate(name, 0)
      case IntValue(i) => i
      case o: Operation => o(resolveValue(o.l,vars),resolveValue(o.r,vars))
    }
    val runner: (HashMap[String,Int] => Int)
  }

  object AnalogueModeDeclaration extends ModeDeclaration with Construct[ModeDeclaration] {
    def parser = "-" ~ Mode ~ Analogue ~ "." ^^^ this
  }

  object DigitalModeDeclaration extends ModeDeclaration with Construct[ModeDeclaration] {
    def parser = "-" ~ Mode ~ Analogue ~ "." ^^^ this
  }

  case class InitDeclaration(statements: Seq[Statement]) extends Token with ICRunner {
    val runner = (init: HashMap[String,Int]) => resolveStatementSeq(statements, init)
  }
  object InitDeclaration extends Construct[InitDeclaration] {
    def parser = "-" ~> Init ~> statementSeq <~ "." ^^ (InitDeclaration(_))
  }

  case class OutputDeclaration(output: Variable) extends Token
  object OutputDeclaration extends Construct[OutputDeclaration] {
    def parser = "-" ~ Output ~> Variable <~ "." ^^ (OutputDeclaration(_))
  }

  case class InputDeclaration(input: Variable) extends Token
  object InputDeclaration extends Construct[InputDeclaration] {
    def parser = "-" ~ Input ~> Variable <~ "." ^^ (InputDeclaration(_))
  }

  case class LoopDeclaration(statements: Seq[Statement]) extends Token with ICRunner {
    val runner = (state: HashMap[String,Int]) => resolveStatementSeq(statements, state)
  }
  object LoopDeclaration extends Construct[LoopDeclaration] {
    def parser = "-" ~> Loop ~> statementSeq <~ "." ^^ (LoopDeclaration(_))
  }

  case class Program(input: Variable, output: Variable, init: Option[InitDeclaration], loop: Option[LoopDeclaration]) extends Token {
    val variables = HashMap[String,Int]()
    init.map(_.runner(variables))
    def run(i: Int) = {
      variables(input.name) = i
      loop.map(_.runner(variables)).getOrElse(0)
    }

  }
  object Program extends Construct[Program] {
    def parser = InputDeclaration ~ OutputDeclaration ~ (InitDeclaration.?) ~ (LoopDeclaration.?) ^^ {
      case InputDeclaration(a) ~ OutputDeclaration(b) ~ c ~ d => Program(a,b,c,d)
    }
  }

  sealed class Keyword(key: Parser[_]) extends Construct[Token] with Token {
    def parser = key ^^^ this
    def this(s: String) = this(accept(s.toList))
    def this(r: Regex) = this(regex(r))
  }

  object StandaloneKW extends Construct[Keyword] {
    def parser = Otherwise.asInstanceOf[Parser[Keyword]] ||| End.asInstanceOf[Parser[Keyword]]
  }

  object Mode extends Keyword("mode") {
    override def toString = "mode: "
  }
  object Digital extends Keyword("digital") {
    override def toString = "digital"
  }
  object Analogue extends Keyword("analog(ue)?".r) {
    override def toString = "analogue"
  }
  object Input extends Keyword("input") {
    override def toString = "input"
  }
  object Output extends Keyword("output") {
    override def toString = "output"
  }
  object Init extends Keyword("init") {
    override def toString = "init"
  }
  object Loop extends Keyword("loop") {
    override def toString = "loop"
  }
  object If extends Keyword("if") {
    override def toString = "if"
  }
  object Otherwise extends Keyword("otherwise") {
    override def toString = "otherwise"
  }
  object End extends Keyword("end") {
    override def toString = "end"
  }

  type Value = Variable | IntValue | Operation
  object ValueNoOp extends Construct[Value] {
    def parser = Variable.asInstanceOf[Parser[Value]] ||| IntValue ||| "(" ~> Value <~ ")"
  }
  object Value extends Construct[Value] {
    def parser = Operation | ValueNoOp
  }

  case class Variable(name: String) extends Token
  object Variable extends Construct[Variable] {
    def parser = "[_a-zA-Z]([_a-zA-Z0-9]*)".r ^^ (Variable(_))
  }

  case class IntValue(i: Int) extends Token {
    def parser = i.toString ^^^ this
  }

  object IntValue extends Construct[IntValue] {
    def parser = "[0-9]+".r ^^ (i => IntValue(i.toInt))
  }

  case class Assignment(to: Variable, value: Value) extends Token {
    override def toString = s"$to <- $value"
  }
  object Assignment extends Construct[Assignment] {
    def parser = Variable ~ ("<-" ~> Value) ^^ {case (v: Variable) ~ (n: Value) => Assignment(v,n)}
  }

  abstract trait Operation extends Token {
    val l: Value
    val r: Value
    def apply(i: Int, j: Int): Int
  }
  object Operation extends Construct[Operation] {
    def parser = Equals | Add | Subtract 
  }

  case class Subtract(l: Value, r: Value) extends Operation {
    def apply(i: Int, j: Int) = math.max(0, i-j)
  }
  object Subtract extends Construct[Subtract] {
    def parser = (ValueNoOp <~ "-") ~ ValueNoOp ^^ (a => Subtract(a._1,a._2))
  }
  case class Add(l: Value, r: Value) extends Operation {
    def apply(i: Int, j: Int) = math.min(15, i+j)
  }
  object Add extends Construct[Add] {
    def parser = (Subtract | ValueNoOp <~ "+") ~ (Subtract | ValueNoOp) ^^ (a => Add(a._1,a._2))
  }
  case class Equals(l: Value, r: Value) extends Operation {
    def apply(i: Int, j: Int) = if(i==j) 15 else 0
  }
  object Equals extends Construct[Equals] {
    def parser = (((Add | Subtract | ValueNoOp)  <~ "=") ~ (Add | Subtract | ValueNoOp)) ^^ (a => Equals(a._1,a._2))
  }

  case class IfCase(predicate: Value, statements: Seq[Statement]) extends Token
  object IfCase extends Construct[IfCase] {
    def parser = (Operation | ValueNoOp) ~ statementSeq ^^ {
      case (v: Value) ~ (s: Seq[Statement]) => IfCase(v,s)
    }
  }
  type Statement = IfStatement | Assignment | Value
  object Statement extends Construct[Statement] {
    def parser = Assignment.asInstanceOf[Parser[Statement]] | IfStatement | Value
  }

  def otherwise = Otherwise ~> statementSeq ^^ (IfCase(IntValue(15),_))
  def statementSeq: Parser[Seq[Statement]] = (Statement <~ ",").* ~ Statement ^^ {case (s: Seq[Statement]) ~ (st: Statement) => s :+ st}
  def ifCaseSeq: Parser[Seq[IfCase]] = (IfCase <~ ";").* ~ IfCase ^^ {
    case i ~ j => i :+ j
  }

  case class IfStatement(cases: Seq[IfCase]) extends Token
  object IfStatement extends Construct[IfStatement] {
    def parser = "-" ~> If ~> ifCaseSeq <~ "end" ^^ (IfStatement(_))
  }


}