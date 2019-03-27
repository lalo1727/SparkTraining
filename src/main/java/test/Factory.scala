import com.typesafe.config.{Config, ConfigFactory}

object Factory {
  def main(args: Array[String]): Unit = {

    val mongoDB1 = ConfigFactory.load("ConfigFactory.conf")

    val mongoDB = mongoDB1.getConfig("mongoDB")
    lazy val host = mongoDB.getString("host")
    lazy val port = mongoDB.getString("port")
    lazy val db = mongoDB.getString("db")
    lazy val collection = mongoDB.getString("collection")

    println(host)

  }
}