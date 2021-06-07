package es.tmoor.minecraft
package listeners

import org.bukkit.plugin.Plugin
import org.bukkit.event.{Listener, EventHandler}
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.Material.COMPARATOR
import org.bukkit.NamespacedKey
import org.bukkit.Location
import org.bukkit.block.Comparator
import scala.jdk.CollectionConverters._
import org.bukkit.scheduler.BukkitRunnable

import data.ICData
import event.ICUpdateEvent
import org.bukkit.inventory.meta.BlockDataMeta
import org.bukkit.World

class ICPlaceBlockListener(plugin: RedstoneICs) extends Listener {
  def broadcast(s: String) = plugin.getServer.broadcastMessage(s)
  @EventHandler
  def onBlockPlace(event: BlockPlaceEvent) =
    if (
      Option(event.getItemInHand).collect {
        case item if Option(item.getType) == Some(COMPARATOR) => item
      }.isDefined
    ) {
      val item = event.getItemInHand
      val meta = item.getItemMeta
      val lore = Option(meta.getLore).map(_.asScala.headOption)
      if (lore.isDefined && lore.get.isDefined) {
        val player = event.getPlayer
        val nsk = NamespacedKey(plugin, "program")
        val program = meta.getPersistentDataContainer.get(nsk, ICData())
        val block = event.getBlockPlaced
        val state = block.getState.asInstanceOf[Comparator]
        state.getPersistentDataContainer.set(nsk, ICData(), program)
        state.update
        val (x,y,z,name) = (block.getLocation.getBlockX, block.getLocation.getBlockY, block.getLocation.getBlockZ, block.getLocation.getWorld.getName)
        plugin.setLocations((x,y,z,name) +: plugin.getLocations)
        val updater = new BukkitRunnable {
          val location = Location(plugin.getServer.getWorld(name),x,y,z)
          val event = new ICUpdateEvent(Location(plugin.getServer.getWorld(name),x,y,z), plugin)
          def run = {
            plugin.getServer.getPluginManager.callEvent(event)
            if (!event.isComparator) cancel
          }
        }
        updater.runTaskTimer(plugin,0,0)
      }
    }
}
