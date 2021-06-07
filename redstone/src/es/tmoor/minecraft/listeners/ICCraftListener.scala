package es.tmoor.minecraft.listeners

import org.bukkit.event
import event.Listener
import event.EventHandler
import org.bukkit.entity.Player
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.inventory.ItemStack
import scala.jdk.CollectionConverters._
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.Material
import org.bukkit.inventory.meta.ItemMeta

class ICCraftListener extends Listener {
  @EventHandler
  def onCraft(event: CraftItemEvent) = {
    val item = ItemStack(Material.REPEATER)
    val im = item.getItemMeta
    im.setDisplayName("ADS")
    im.setLocalizedName("ADS")
    im.setLore(List("ADS").asJava)
    item.setItemMeta(im)
    event.setCurrentItem(item)
    event.getWhoClicked.sendMessage(s"${item.getItemMeta}")
  }
}
