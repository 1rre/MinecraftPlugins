package re._1r.spigot
import org.bukkit._
import plugin.java.JavaPlugin
import event.{Listener, EventHandler}
import event.block.{SignChangeEvent, Action}
import event.player.PlayerInteractEvent
import block.Sign
import metadata.FixedMetadataValue
import plugin.Plugin
import scala.util.control.{BreakControl, Breaks}

class SignavatorPlugin extends JavaPlugin{
  override def onEnable = {
    println("Loaded TJ's Elevator Signs")
    getServer.getPluginManager.registerEvents(
      new SignavatorListener, this
    )
    getServer.getPluginManager.registerEvents(
      new SignavatorTeleporter, this
    )
  }
}
class SignavatorListener extends Listener{
  @EventHandler
  def signavatorCreated(event: SignChangeEvent) = {
    if(event.getLine(0) == "[Elevator]"){
      def player = event.getPlayer
      def signavator = event.getBlock
      event.getLine(1).toLowerCase match{
        case "up" => {
          player.sendMessage("Signavator Placed!")
          event.setLine(0,(ChatColor.BLUE + "[Elevator]"))
          event.setLine(1, "Up")
        }
        case "down" => {
          player.sendMessage("Signavator Placed!")
          event.setLine(1, "Down")
        }
        case _ => player.sendMessage("Incorrect format for Signavator")
      }
    }
  }
}
class SignavatorTeleporter extends Listener {
  @EventHandler
  def signavatorClicked(event: PlayerInteractEvent) = {
    if(event.getAction == Action.RIGHT_CLICK_BLOCK){
      def player = event.getPlayer
      event.getClickedBlock.getState match {
        case sign: Sign => if(sign.getLine(0) == ChatColor.BLUE + "[Elevator]"){ //Change Colour
          sign.getLine(1).last match{
            case 'p' => {
              val findBlock = new Breaks
              findBlock.breakable{
                for(height <- 1 to 255 - sign.getLocation.getBlockY){
                  val CheckBlock = sign.getLocation.add(0d, height, 0d).getBlock
                  if(!(CheckBlock.isPassable)
                  && CheckBlock.getRelative(0,1,0).isPassable
                  && CheckBlock.getRelative(0,2,0).isPassable
                  ){
                    player.teleport(
                      sign.getLocation.setDirection(player.getLocation.getDirection)
                        .add(0.5d, height + 1d,0.5d)
                    )
                    player.sendMessage(ChatColor.GRAY + "Woosh!")
                    player.playSound(player.getLocation, Sound.ENTITY_PLAYER_BREATH, 3f, 1f)
                    findBlock.break
                  }
                }
              }
            }
            case 'n' => 
              val findBlock = new Breaks
              findBlock.breakable{
                for(height <- 3 to sign.getLocation.getBlockY){
                  val CheckBlock = sign.getLocation.subtract(0d, height, 0d).getBlock
                  if(!(CheckBlock.isPassable)
                  && CheckBlock.getRelative(0,1,0).isPassable
                  && CheckBlock.getRelative(0,2,0).isPassable
                  ){
                    player.teleport(
                      sign.getLocation.setDirection(player.getLocation.getDirection)
                        .subtract(-0.5d,height - 1d,-0.5d)
                    )
                    player.sendMessage(ChatColor.GRAY + "Woosh!")
                    player.playSound(player.getLocation, Sound.ENTITY_PLAYER_BREATH, 3f, 1f)
                    findBlock.break
                  }
                }
              }
            case _ =>
          }
          
        }
        case _ =>
      }
    }
  }
}