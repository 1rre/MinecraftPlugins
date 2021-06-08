package es.tmoor.minecraft
package listeners

import org.bukkit.plugin.Plugin
import org.bukkit.event.{Listener,EventHandler}
import org.bukkit.event.block.BlockRedstoneEvent
import org.bukkit.block.data.`type`.Comparator as ComparatorData
import org.bukkit.block.Comparator as ComparatorState
import org.bukkit.NamespacedKey
import data.ICData
import event.ICUpdateEvent
import CompileBook._

class ICRedstoneListener(plugin: Plugin) extends Listener {
  @EventHandler
  def setComparatorLevel(event: ICUpdateEvent) = {
    if (event.isComparator && event.hasProgram) {
      val program = event.getProgram
      val Program(Variable(input),Variable(output),init,loop) = program
      val (i0,i1,i2) = event.getInputs
      println(s"input: $i0")
      val result = program.run(i0)
      println(s"result: $result")
      event.setPower(result)
    } else {
      println((event.getBlock,event.hasProgram))
    }
  }
}

/*
 Option(block.getState).collect {
      case state: ComparatorState => {
        val container = state.getPersistentDataContainer
        val data = block.getBlockData.asInstanceOf[ComparatorData]
        val nsk = NamespacedKey(plugin, "program")
        val program = container.get(nsk, ComparatorIC())
        val (in0,in1,in2) = data.getFacing match {
          case NORTH =>
            (block.getBlockPower(NORTH),block.getBlockPower(EAST),block.getBlockPower(WEST))
          case EAST =>
            (block.getBlockPower(EAST),block.getBlockPower(SOUTH),block.getBlockPower(NORTH))
          case SOUTH =>
            (block.getBlockPower(SOUTH),block.getBlockPower(WEST),block.getBlockPower(EAST))
          case WEST =>
            (block.getBlockPower(WEST),block.getBlockPower(NORTH),block.getBlockPower(SOUTH))
          case _ => (0,0,0)
        }
        plugin.getServer.broadcastMessage(s"in0: $in0, in1: $in1, in2: $in2")
      }
      case _ => say(block)
    }
*/