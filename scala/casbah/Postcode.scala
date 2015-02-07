import com.mongodb.casbah.Imports._

object PostCodeStructure {

  private[this] val labels = Seq(
    "jis-code",         //全国地方公共団体コード(JIS X0401、X0402)
    "post-code-old",    //(旧)郵便番号(5桁)
    "post-code",        //郵便番号(7桁)
    "pref-kana",        //都道府県名(カタカナ)
    "city-kana",        //市区町村名(カタカナ)
    "town-kana",        //町域名(カタカナ)
    "pref-kanji",       //都道府県名(漢字)
    "city-kanji",       //市区町村名(漢字)
    "town-kanji",       //町域名(漢字)
    "multiple-code",    //一町域が二以上の郵便番号で表される場合の表示(1=該当,0=該当せず)
    "each-numbering",   //小字毎に番地が起番されている町域の表示(1=該当,0=該当せず)
    "has-block-number", //丁目を有する町域の場合の表示(1=該当,0=該当せず)
    "multiple-town",    //一つの郵便番号で二以上の町域を表す場合の表示(1=該当,0=該当せず)
    "update-status",    //更新の表示(0=変更なし,1=変更あり,2=廃止)
    "update-reason"     //変更理由(0=変更なし,1=市政・区政・町政・分区・政令指定都市施行,2=住居表示の実施,3=区画整理,4=郵便区調整等,5=訂正,6=廃止)
  )

  private[this] val quoted_column = """^"(.+)"$""".r

  def stripQuot(csvRow: String) = csvRow.split(",").map {
    case quoted_column(v) => v
    case non_quoted       => non_quoted
  }

  def toMongo(csvRow: String) = {
    val builder = MongoDBObject.newBuilder
    for (col <- labels.zip(stripQuot(csvRow))) builder += col
    builder.result
  }

}
