# MinecraftPlugins
A spigot plugin for Minecraft which allows for 'comparators' and 'repeators' to be programmed using books.

## Development State
The functionality which changes the behaviours of the redstone components is fully implemented, however currently the compiler can only understand very basic instructions.

## Files
The syntax for programming the redstone components can be found in [syntax.mcic](/redstone/src/syntax.mcic)
The (unfinished) compiler can be found in [compiler.scala](redstone/src/compiler.scala)
The functions which allow the functionality of the programmed redstone component to be saved as block data can be found in [persistentdata.scala](/redstone/src/persistentdata.scala) and [debug.scala](/redstone/src/debug.scala)
The function which allows the user to program a redstone component using a signed book can be found in [main.scala](/redstone/src/main.scala)
The functions which allow a programmed redstone component to keep its functionality in the inventory can be found in [player.scala](redstone/src/player.scala)
