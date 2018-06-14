package net.astail

import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._


object Main {
  def main(args: Array[String]): Unit = {
    ArgumentsParser.parse(args, Arguments()).foreach { a =>
      val time = a.time
      val battle = a.battle

      val api = s"https://spla2.yuu26.com/${battle}/${time}"

      val battle2: String = battle match {
        case "regular" => "レギュラー"
        case "gachi" => "ガチ"
        case "league" => "リーグ"
      }
      val time2: String = time match {
        case "now" => "今"
        case "next" => "次回"
      }

      val resultData1 = resultData(api)

      def map: String = resultData1.maps.mkString(",")
      def rule: String = resultData1.rule_ex.name

      println(s"バトル: ${battle2}, 時間: ${time2}, ルール: ${rule}, マップ: ${map}")

    }
  }

  def resultData(api: String): Result = {
    val source = Source.fromURL(api, "utf-8")
    val str = source.getLines.mkString
    val jsonObj = parse(str)

    try {
      implicit val formats = DefaultFormats
      val listResult = (jsonObj \ "result").extract[List[Result]]
      listResult(0)
    } finally {
      source.close
    }
  }
}
