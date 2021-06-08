package es.tmoor.minecraft

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.inventory.{ItemStack,ShapelessRecipe}
import listeners._
import scala.jdk.CollectionConverters._
import org.bukkit.Location
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.block.Comparator
import org.bukkit.event.Event
import org.bukkit.NamespacedKey
import data.ICData
import event.ICUpdateEvent
import java.io.File
import java.io.FileOutputStream
import collection.mutable.HashMap
import es.tmoor.minecraft.CompileBook.Program
import org.bukkit.Material

class RedstoneICs extends JavaPlugin {
  val programs = HashMap[Location,Program]()
  val configFile = File(this.getDataFolder.getAbsolutePath ++ "/ic.bin")
  if (!configFile.exists) {
    configFile.getParentFile.mkdirs
    configFile.createNewFile
  }

  def setLocations(l: Seq[(Int,Int,Int,String)]) = {
    import java.io.ObjectOutputStream
    import java.io.FileOutputStream
    val writer = new FileOutputStream(configFile)
    val stream = new ObjectOutputStream(writer)
    stream.writeObject(l)
    stream.close
    writer.close
  }

  def getLocations = {
    import java.io.ObjectInputStream
    import java.io.FileInputStream
    util.Try {
      val reader = new FileInputStream(configFile)
      val stream = new ObjectInputStream(reader)
      val locations = stream.readObject.asInstanceOf[Seq[(Int,Int,Int,String)]]
      stream.close
      reader.close
      locations
    } getOrElse {
      setLocations(Seq())
      Seq()
    }

  }

  override def onEnable = {
    getLocations.foreach(loc => {
      println(s"loading $loc")
      val lcn = Location(getServer.getWorld(loc._4),loc._1,loc._2,loc._3)
      val block = lcn.getBlock
      val plugin = this
      if (block.getType == Material.COMPARATOR) {
        programs += lcn -> block.getState.asInstanceOf[Comparator].getPersistentDataContainer.get(NamespacedKey(plugin, "program"),ICData())
      }
      val updater = new BukkitRunnable {
        val upEvent = new ICUpdateEvent(Location(getServer.getWorld(loc._4),loc._1,loc._2,loc._3), plugin)
        def run = {
            plugin.getServer.getPluginManager.callEvent(upEvent)
            if (!upEvent.isComparator) cancel
          }
      }
      updater.runTaskTimer(this,0,0)
    })
    getServer.getPluginManager.registerEvents(ICPrepareCraftListener(this), this)
    getServer.getPluginManager.registerEvents(ICPlaceBlockListener(this), this)
    getServer.getPluginManager.registerEvents(ICRedstoneListener(this), this)
    println("Loaded TJ's Redstone Plugin")
  }
}