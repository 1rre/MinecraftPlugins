import org.bukkit._
import persistence._
import command._
import plugin._
import org.bukkit.entity._

class PlayerName extends PersistentDataType[String, String]{
  def apply = this.asInstanceOf[PersistentDataType[String, String]]
  def getPrimitiveType: Class[String] = classOf[String]
  def getComplexType: Class[String] = classOf[String]
  def toPrimitive(x: String, context: PersistentDataAdapterContext): String = x
  def fromPrimitive(x: String, context: PersistentDataAdapterContext): String = x
  
}
class PlayerDataGet(plugin:Plugin) extends CommandExecutor{
  override def onCommand(
    sender: CommandSender,
    cmd: Command,
    label: String,
    args: Array[String]
  ): Boolean = {
    sender match {
      case player: Player => {
        val NSK = new NamespacedKey(plugin, player.getUniqueId.toString)
        println(player.getPersistentDataContainer.get(NSK, new PlayerName))
        player.chat(player.getPersistentDataContainer.get(NSK, new PlayerName))
      }
    }
    true
  }
}
class PlayerDataSet(plugin:Plugin) extends CommandExecutor{
  override def onCommand(
    sender: CommandSender,
    cmd: Command,
    label: String,
    args: Array[String]
  ): Boolean = {
    sender match {
      case player: Player => {
        val NSK = new NamespacedKey(plugin, player.getUniqueId.toString)
        player.getPersistentDataContainer.set(NSK, new PlayerName, args(0))
        println(player.getPersistentDataContainer.get(NSK, new PlayerName))
        player.chat(player.getPersistentDataContainer.get(NSK, new PlayerName))
        }
    }
    true
  }
}
