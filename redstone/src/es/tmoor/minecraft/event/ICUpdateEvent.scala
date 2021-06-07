package es.tmoor.minecraft
package event

import org.bukkit.event.Event
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.plugin.Plugin
import org.bukkit.event.HandlerList
import org.bukkit.block.Comparator as ComparatorState
import org.bukkit.block.data.`type`.Comparator as ComparatorData
import org.bukkit.block.BlockFace._
import org.bukkit.Material.REDSTONE_WIRE
import data.ICData
import org.bukkit.event.Cancellable
import es.tmoor.minecraft.CompileBook.Program


object ICUpdateEvent {
  var handlers: HandlerList = new HandlerList()
  def getHandlerList: HandlerList = handlers
}
class ICUpdateEvent(location: Location, p: Plugin) extends Event with Cancellable {
  import ICUpdateEvent._
  def getHandlers: HandlerList = handlers

  def getBlock = location.getBlock
  def isCancelled = isComparator
  def setCancelled(b: Boolean) = {}

  def isComparator = Option(getBlock.getState).collect {
    case b: ComparatorState => {
      b.update(true)
    }
  } == Some(true)

  def getState = getBlock.getState

  def hasProgram = isComparator && getState.asInstanceOf[ComparatorState].getPersistentDataContainer.has (
    NamespacedKey(p, "program"),
    ICData()
  )

  def getProgram = getState.asInstanceOf[ComparatorState].getPersistentDataContainer.get (
    NamespacedKey(p, "program"),
    ICData()
  )
  def updateProgram(prog: Program) = {
    getState.asInstanceOf[ComparatorState].getPersistentDataContainer.set (
      NamespacedKey(p, "program"),
      ICData(),
      prog
    )
    getState.update(true)
  }

  def getData = getBlock.getBlockData.asInstanceOf[ComparatorData]

  def getInputs = getData.getFacing match {
    case NORTH =>
      (getBlock.getBlockPower(NORTH),getBlock.getBlockPower(EAST),getBlock.getBlockPower(WEST))
    case EAST =>
      (getBlock.getBlockPower(EAST),getBlock.getBlockPower(SOUTH),getBlock.getBlockPower(NORTH))
    case SOUTH =>
      (getBlock.getBlockPower(SOUTH),getBlock.getBlockPower(WEST),getBlock.getBlockPower(EAST))
    case WEST =>
      (getBlock.getBlockPower(WEST),getBlock.getBlockPower(NORTH),getBlock.getBlockPower(SOUTH))
    case _ => (0,0,0)
  }

  def setPower(i: Int) = {
    val dat = getBlock.getBlockData.asInstanceOf[ComparatorData]
    dat.setPowered(i>0)
    getBlock.setBlockData(dat)
  }

}
