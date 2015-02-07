import com.typesafe.config.ConfigFactory
import com.mongodb.casbah.Imports._
import Implicits._

object SampleDatabase extends {
  val dbName = "sample"
} with MongoDatabase

sealed abstract class MongoDatabase {

  val dbName: String

  private[this] val mongoConf = ConfigFactory.load("application.conf").getConfig("mongo")

  private[this] val dbConf = mongoConf.getConfig(dbName)

  val host = mongoConf.getString("host")

  val port = mongoConf.getInt("port")

  val username = dbConf.getString("username")

  val password = dbConf.getString("password")

  private[this] val server = new ServerAddress(host, port)

  private[this] val auth = MongoCredential.createCredential(username, dbName, password.toCharArray)

  def scope[A](f: MongoDB => A) = MongoClient(server, List(auth)).use(client => f(client(dbName)) )

}
