import mill._, scalalib._
import coursier.maven.MavenRepository

object spigotscala extends ScalaModule{
	def scalaVersion = "2.13.2"
	def unmanagedClasspath = T {
    if (!os.exists(millSourcePath / "lib")) Agg()
  	else Agg.from(os.list(millSourcePath / "lib").map(PathRef(_)))
	}
}
object redstone extends ScalaModule{
	def scalaVersion = "2.13.2"
	def unmanagedClasspath = T {
    if (!os.exists(millSourcePath / "lib")) Agg()
  	else Agg.from(os.list(millSourcePath / "lib").map(PathRef(_)))
	}
}
object elevator extends ScalaModule{
	def scalaVersion = "2.13.2"
	def unmanagedClasspath = T {
    if (!os.exists(millSourcePath / "lib")) Agg()
  	else Agg.from(os.list(millSourcePath / "lib").map(PathRef(_)))
	}
}
/*object mcMMO extends JavaModule{
	def ivyDeps = Agg(
		ivy"org.apache.commons:commons-lang3:3.10",
		ivy"com.google.guava:guava:29.0-jre"
	)
	def unmanagedClasspath = T {
    if (!os.exists(millSourcePath / "lib")) Agg()
  	else Agg.from(os.list(millSourcePath / "lib").map(PathRef(_)))
	}
}*/