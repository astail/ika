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

      val ggetMaps = getMaps(api)
      val ggetRule = getRule(api)

      println(s"バトル: ${battle2}, 時間: ${time2}, ルール: ${ggetRule}, マップ: ${ggetMaps}")

    }
  }

  def getMaps(api: String): String = {
    val source = Source.fromURL(api, "utf-8")
    val str = source.getLines.mkString
    val jsonObj = parse(str)
    try {
      implicit val formats = DefaultFormats
      case class Maps_ex(id: Int, name: String, statink: String)
      case class Rule_ex(key: String, name: String, statink: String)
      case class Result(rule: String, rule_ex: Rule_ex, maps: List[String], maps_ex: List[Maps_ex],
        start: String, start_utc: String, start_t: Int, end: String, end_utc: String, end_t: Int)

      val listResult = (jsonObj \ "result").extract[List[Result]]
      listResult(0).maps.mkString(",")
    } finally {
      source.close
    }
  }

  def getRule(api: String): String = {
    val source = Source.fromURL(api, "utf-8")
    val str = source.getLines.mkString
    val jsonObj = parse(str)

    try {
      implicit val formats = DefaultFormats
      case class Maps_ex(id: Int, name: String, statink: String)
      case class Rule_ex(key: String, name: String, statink: String)
      case class Result(rule: String, rule_ex: Rule_ex, maps: List[String], maps_ex: List[Maps_ex],
        start: String, start_utc: String, start_t: Int, end: String, end_utc: String, end_t: Int)

      val listResult = (jsonObj \ "result").extract[List[Result]]
      listResult(0).rule_ex.name
    } finally {
      source.close
    }
  }
}
