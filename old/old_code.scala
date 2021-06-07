import org.bukkit._
import plugin.java.JavaPlugin
import event.{Listener, EventHandler}
import event.player.PlayerInteractEvent
import event.block.{Action, BlockRedstoneEvent}
import entity.Player
import Material.WRITTEN_BOOK
import plugin.Plugin
import inventory.meta.ItemMeta



class BookClickListener(plugin: Plugin) extends Listener{
  @EventHandler
  def ComparatorBookClick(event: PlayerInteractEvent) = {
    if(event.getAction == Action.RIGHT_CLICK_BLOCK) {
      import inventory.meta.BookMeta
      import block.Comparator
      val msg = event.getClickedBlock.getState match {
        case comparator: Comparator => {
          event.getItem.getItemMeta match {
            case book: BookMeta => {

              val NSK = new NamespacedKey(plugin, "val")
              import persistence.PersistentDataType
              comparator.getPersistentDataContainer.get(NSK, PersistentDataType.STRING).toString
            }
            //case item: ItemMeta => debug.Debug.getVariables(comparator, plugin)
            case _ => "Not a book or item"
          }
        }
        case block: block.Block => "Not a Comparator: " + block.toString
        case _ => ""
      }
      if(msg != "") event.getPlayer.sendMessage(msg)
    }
  }
}

class RedstoneIcListener extends Listener{
  @EventHandler
  def ComparatorRedstoneLightup(event: BlockRedstoneEvent) = {
    
  }
}