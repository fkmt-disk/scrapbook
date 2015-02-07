import scala.sys.process._
import scala.language.postfixOps
import java.io.File
import Implicits._

object PreparePostcodeCollection extends App {

  new File("01hokkai.zip") match {
    case f if f.exists => pass
    case f =>
      Process("wget -ncq http://www.post.japanpost.jp/zipcode/dl/kogaki/zip/01hokkai.zip") !
  }

  new File("01HOKKAI.CSV") match {
    case f if f.exists => pass
    case f =>
      Process("unzip 01hokkai.zip") !
  }

  new File("hokkai.csv") match {
    case f if f.exists => pass
    case f =>
      Process("head -30 01HOKKAI.CSV") #> f ! // 全部入れると時間かかるので30行だけにしておく
  }

  val csvRows = new File("hokkai.csv").newReader("Shift_JIS").use { r =>
    Iterator.continually(r.readLine).takeWhile(_ != null).toList
  }

  val datas = csvRows.map(row => PostCodeStructure.toMongo(row))

  SampleDatabase.scope { db =>
    val postcode = db("postcode")
    postcode.drop
    for (data <- datas) postcode.insert(data)
  }

}
