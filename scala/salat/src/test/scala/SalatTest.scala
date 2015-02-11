import scala.language.postfixOps

import org.scalatest._

import com.typesafe.config.ConfigFactory

import com.mongodb.casbah.Imports._

import com.novus.salat._
import com.novus.salat.global._

class SalatTest extends FunSuite with GivenWhenThen with Matchers with BeforeAndAfterAll {

  val (mongo, coll) = {
    val conf = ConfigFactory.load.getConfig("mongo")

    val host = conf.getString("host")
    val port = conf.getInt("port")
    val user = conf.getString("username")
    val pswd = conf.getString("password")
    val name = conf.getString("database")

    val server = new ServerAddress(host, port)
    val auth = MongoCredential.createCredential(user, name, pswd.toCharArray)

    val mongo = MongoClient(server, List(auth))
    val coll = mongo(name)("salat") // salatはコレクション名

    coll.drop // 空にしておく

    (mongo, coll)
  }

  override def afterAll {
    mongo.close
  }

  test("salat grater") {
    val pochi = User(1, "pochi", "pochi@example.zoo")
    Given(s"a User instance pochi=$pochi")

    val convertedpochi: DBObject = grater[User].asDBObject(pochi)
    coll.insert(convertedpochi)
    When("insert pochi into salat-collection")

    And("find `pochi` data")
    val result = coll.find(DBObject("id" -> pochi.id, "name" -> pochi.name, "email" -> pochi.email))

    Then("should find one elements")
    result.count shouldBe 1

    val pochiClone: User = grater[User].asObject(result.toList.head)

    And("should that is equal original pochi")
    pochiClone shouldEqual pochi
  }

}
