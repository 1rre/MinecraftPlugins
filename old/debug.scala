package re._1r.spigot
package debug

import org.bukkit._
import persistence.PersistentDataType
import block.Comparator
import scala.collection.Map
import plugin.Plugin

object Debug {
  def getVariables(comparator: Comparator, plugin: Plugin): String = {
    val NSK = new NamespacedKey(plugin, "val")
    var rtn = ""
    comparator.getPersistentDataContainer.get(NSK, PersistentDataType.STRING).split('\n').foreach(variable => {
      val NSKval = new NamespacedKey(plugin, variable)
      rtn += (variable + " = " + comparator.getPersistentDataContainer.get(NSKval, PersistentDataType.STRING))
    })
    rtn
  }
}