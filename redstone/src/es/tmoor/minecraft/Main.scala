package es.tmoor.minecraft

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.inventory.{ItemStack,ShapelessRecipe}
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.NamespacedKey
import org.bukkit.Material.{WRITABLE_BOOK,REPEATER}
import listeners._

import scala.jdk.CollectionConverters._

class RedstoneICs extends JavaPlugin {
  override def onEnable = {
    getServer.getPluginManager.registerEvents(ICPrepareCraftListener(), this)
    //getServer.getPluginManager.registerEvents(ICCraftListener(), this)
    println("Loaded TJ's Redstone Plugin")
  }
}
