package re._1r.spigot.persistentcircuits

import org.bukkit.persistence._
import scala.collection.Map

class ComparatorIc extends PersistentDataType[Array[Byte], (Int, Int, Map[String, Int]) => Int]{
  override def getPrimitiveType: Class[Array[Byte]] = {
    (Array[Byte]()).getClass.asInstanceOf[Class[Array[Byte]]]
  }
  override def getComplexType: Class[(Int, Int, Map[String, Int]) => Int] = {
    ((x:Int, y:Int, z:Map[String, Int]) => 0).getClass.asInstanceOf[Class[(Int, Int, Map[String, Int]) => Int]]
  }
  override def toPrimitive(
    complex: (Int, Int, Map[String, Int]) => Int,
    context: PersistentDataAdapterContext
  ): Array[Byte] = {
    import java.io.{ByteArrayOutputStream, ObjectOutputStream}
    val baos = new ByteArrayOutputStream
    val writer = new ObjectOutputStream(baos)
    writer.writeObject(complex)
    writer.close
    val rtn = baos.toByteArray
    baos.close
    rtn
  }
  override def fromPrimitive(
    primitive: Array[Byte],
    context: PersistentDataAdapterContext
  ): (Int, Int, Map[String, Int]) => Int = {
    import java.io.{ByteArrayInputStream, ObjectInputStream}
    val bais = new ByteArrayInputStream(primitive)
    val reader = new ObjectInputStream(bais)
    val rtn = reader.readObject.asInstanceOf[(Int,Int, Map[String, Int]) => Int]
    reader.close
    bais.close
    rtn
  }
}
