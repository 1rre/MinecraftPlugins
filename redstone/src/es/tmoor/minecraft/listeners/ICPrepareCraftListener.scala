package es.tmoor.minecraft.listeners

import org.bukkit.event.{Listener, EventHandler}
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.inventory.{ShapelessRecipe, ItemStack}
import org.bukkit.Material.{REPEATER, WRITTEN_BOOK}
import org.bukkit.inventory.CraftingInventory
import org.bukkit.inventory.meta.BookMeta

import es.tmoor.minecraft.CompileBook

import scala.jdk.CollectionConverters._
import scala.util.parsing.input.CharSequenceReader

class ICPrepareCraftListener extends Listener {

  def compileBook(inv: CraftingInventory) = {
    val players = inv.getViewers.asScala
    players.foreach(_.sendMessage("Compiling book..."))
    inv.iterator.asScala.collect {
      case book if Option(book.getType) == Some(WRITTEN_BOOK) => {
        val meta = book.getItemMeta.asInstanceOf[BookMeta]
      }
    }
  }

  @EventHandler
  def onPrepareCraft(event: PrepareItemCraftEvent) = {
    val players = event.getViewers.asScala
    def items = event.getInventory.iterator.asScala.filterNot(_ == null).toList
    def bookCollector: PartialFunction[List[ItemStack], Unit] = {
      case (a: ItemStack) :: (b: ItemStack) :: Nil
          if Option(a.getType) == Some(REPEATER) && Option(b.getType) == Some(WRITTEN_BOOK) => {
        val meta = b.getItemMeta.asInstanceOf[BookMeta]
        val pages = meta.getPages.asScala
        val input = CharSequenceReader(pages.mkString("\n"))
        val result = CompileBook.Program(input)
        players.foreach(_.sendMessage(s"$result"))
        println(result)
        import collection.mutable.HashMap
        for (i <- 0 to 15 if result.successful) {
          val in = result.get.input.name
          val out = result.get.output.name
          val vars = HashMap(in->i)
          players.foreach(_.sendMessage(s"When $in = $i:\n$out = ${result.get.init.get.runner(vars);vars.getOrElseUpdate(out,0)}"))
        }
      }
      case _ =>
    }
    bookCollector(items.sortBy(_.getType.ordinal))

  }
}
