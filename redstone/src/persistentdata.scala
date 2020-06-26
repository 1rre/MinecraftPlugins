package re._1r.spigot.persistentcircuits

import org.bukkit.persistence._
import scala.collection.Map

class ComparatorIc extends PersistentDataType[(Int, Int) => Int, (Int, Int) => Int]{
  override def getPrimitiveType: Class[(Int, Int) => Int] = {
    ((x:Int, y:Int) => 0).getClass.asInstanceOf[Class[(Int, Int) => Int]]
  }
  override def getComplexType: Class[(Int, Int) => Int] = {
    ((x:Int, y:Int) => 0).getClass.asInstanceOf[Class[(Int, Int) => Int]]
  }
  override def toPrimitive(
    complex: (Int, Int) => Int,
    context: PersistentDataAdapterContext
  ): (Int, Int) => Int = complex
  override def fromPrimitive(
    primitive: (Int, Int) => Int,
    context: PersistentDataAdapterContext
  ): (Int, Int) => Int =  primitive
}