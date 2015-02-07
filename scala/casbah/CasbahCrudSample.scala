import com.mongodb.casbah.Imports._

object CasbahCrudSample extends App {

  //------------------------------------------------------------------
  // connect
  val host = SampleDatabase.host
  val port = SampleDatabase.port
  val user = SampleDatabase.username
  val pswd = SampleDatabase.password
  val dbName = SampleDatabase.dbName

  val server = new ServerAddress(host, port)
  val auth = MongoCredential.createCredential(user, dbName, pswd.toCharArray)

  val mongo = MongoClient(server, List(auth))
  val sample = mongo(dbName)        // sample db
  val postcode = sample("postcode") // postcode collection


  //------------------------------------------------------------------
  def insert = {
    val builder = MongoDBObject.newBuilder

    builder += "jis-code" -> "01101"
    builder += "post-code" -> "0600015"
    builder += "pref-kanji" -> "北海道"
    builder += "city-kanji" -> "札幌市中央区"
    builder += "town-kanji" -> "北十五条西"

    postcode.insert(builder.result)
  }

  //------------------------------------------------------------------
  def find = {
    // postcodeコレクションの全ドキュメント検索
    postcode.find().foreach(println)

    println("\n------------------------------------------------------------------\n")

    // post-code=0600015のドキュメントを検索
    postcode.find(MongoDBObject("post-code" -> "0600015")).foreach(println)

    // これはコンパイルエラー。。。マジかよ。。。
    // postcode.find("post-code" -> "0600015").foreach(println)

    println("\n------------------------------------------------------------------\n")

    // findに渡した関数がtrueを返すドキュメントを検索
    postcode.find((obj: DBObject) => obj("town-kanji") == "北五条東").foreach(println)

    // これもコンパイルエラー。。。もうすこし優しさを見せてくれても。。。
    // postcode.find(obj => obj("town-kanji") == "北五条東").foreach(println)
  }

  //------------------------------------------------------------------
  def update = {
    val query = MongoDBObject("post-code" -> "0600000")
    val replaceDoc = {
      val b = MongoDBObject.newBuilder
      b += "post-code" -> "0600000"
      b += "other-data" -> null
      b.result
    }
    // queryに該当するドキュメント「全体」を置き換える
    postcode.update(query, replaceDoc)

    val queryN15W = MongoDBObject("city-kanji" -> "札幌市中央区")
    val updateTownKanji = $set("city-kanji" -> "サッポロ市シ中央区")
    // queryに該当する「1つの」ドキュメントの指定フィールドを更新する
    postcode.update(queryN15W, updateTownKanji)

    val queryHokkaido = MongoDBObject("pref-kanji" -> "北海道")
    val updatePrefKanji = $set("pref-kanji" -> "蝦夷")
    // queryに該当する「全」ドキュメントの指定フィールドを更新する
    postcode.update(queryHokkaido, updatePrefKanji, multi=true)

    val queryHoge = MongoDBObject("hogehoge" -> "fugafuga")
    // 該当ドキュメントがないときはinsertする
    postcode.update(queryHoge, queryHoge, upsert=true)
  }

  //------------------------------------------------------------------
  def remove = {
    val query = MongoDBObject("post-code" -> "0600015")
    val result = postcode.remove(query)
    println(result)
  }

  insert
  find
  update
  remove

  mongo.close

}
