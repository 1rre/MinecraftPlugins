package es.tmoor.minecraft
package data

import org.bukkit.persistence._
import CompileBook.Program

class ICData
    extends PersistentDataType[Array[Byte], Program] {
  def toPrimitive(
      complex: Program,
      context: PersistentDataAdapterContext
  ) = {
    import java.io.{ByteArrayOutputStream, ObjectOutputStream}
    val baos = ByteArrayOutputStream()
    val writer = ObjectOutputStream(baos)
    writer.writeObject(complex)
    val primitive = baos.toByteArray
    writer.close
    baos.close
    primitive
  }
  def fromPrimitive(
      primitive: Array[Byte],
      context: PersistentDataAdapterContext
  ) = {
    import java.io.{ByteArrayInputStream, ObjectInputStream}
    val bais = ByteArrayInputStream(primitive)
    val writer = ObjectInputStream(bais)
    val complex = writer.readObject.asInstanceOf[Program]
    writer.close
    bais.close
    complex
  }
  def getComplexType = classOf[Program]
  def getPrimitiveType = classOf[Array[Byte]]
}
