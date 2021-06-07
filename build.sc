import mill._, scalalib._
import coursier.maven.MavenRepository

object redstone extends ScalaModule{
	def scalaVersion = "3.0.0"
  def ivyDeps = Agg (ivy"org.scala-lang.modules::scala-parser-combinators::2.0.0")
	def unmanagedClasspath = T {
    if (!os.exists(millSourcePath / "lib")) Agg()
  	else Agg.from(os.list(millSourcePath / "lib").map(PathRef(_)))
	}
}