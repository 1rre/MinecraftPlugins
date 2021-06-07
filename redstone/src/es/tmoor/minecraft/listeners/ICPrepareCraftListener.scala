package es.tmoor.minecraft
package listeners

import org.bukkit.event.{Listener, EventHandler}
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.inventory.{ShapelessRecipe, ItemStack}
import org.bukkit.Material.{COMPARATOR, WRITTEN_BOOK, WRITABLE_BOOK}
import org.bukkit.inventory.CraftingInventory
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.NamespacedKey

import data.ICData

import scala.jdk.CollectionConverters._
import scala.util.parsing.input.CharSequenceReader
import org.bukkit.plugin.Plugin

class ICPrepareCraftListener(plugin: Plugin) extends Listener {
  @EventHandler
  def onPrepareCraft(event: PrepareItemCraftEvent) = {
    def inventory = event.getInventory
    def players = event.getViewers.asScala
    val items = inventory.iterator.asScala.filterNot(_ == null).toList
    def bookCollector: PartialFunction[List[ItemStack], Unit] = {
      case (a: ItemStack) :: (b: ItemStack) :: Nil
          if Option(a.getType) == Some(COMPARATOR) && a.getAmount == 1
            && Option(b.getType) == Some(WRITTEN_BOOK) && b.getAmount == 1 => {
        val book = b.getItemMeta.asInstanceOf[BookMeta]
        val pages = book.getPages.asScala
        val input = CharSequenceReader(pages.mkString("\n"))
        val result = CompileBook.Program(input)
        players.foreach(_.sendMessage(s"$result"))
        result.map(prog => {
          val item = ItemStack(COMPARATOR, 1)
          val meta = item.getItemMeta
          inventory.setResult(item)
          val nsk = NamespacedKey(plugin, "program")
          val pdc = ICData()
          meta.getPersistentDataContainer.set(nsk, pdc, prog)
          meta.setDisplayName("Integrated Circuit")
          meta.setLore(List("program").asJava)
          item.setItemMeta(meta)
          inventory.setResult(item)
        })
      }
      case _ =>
    }
    bookCollector(items.sortBy(_.getType.ordinal))

  }
}
