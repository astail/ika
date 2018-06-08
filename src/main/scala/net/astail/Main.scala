package net.astail

import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._


object Main {
  def main(args: Array[String]): Unit = {
    ArgumentsParser.parse(args, Arguments()).foreach { a =>
      val time = a.time
      val rule = a.rule

      val api = s"https://spla2.yuu26.com/${rule}/${time}"

      val rule2: String = rule match {
        case "regular" => "レギュラー"
        case "gachi" => "ガチ"
        case "league" => "リーグ"
      }
      val time2: String = time match {
        case "now" => "今"
        case "next" => "次回"
      }

      println(s"ルール: ${rule2}, 時間: ${time2}")
      getMaps(api)
    }
  }

  def getMaps(api: String) = {
    val source = Source.fromURL(api, "utf-8")
    val str = source.getLines.mkString
    val jsonObj = parse(str)

    implicit val formats = DefaultFormats
    case class Maps_ex(id: Int, name: String, statink: String)
    case class Rule_ex(key: String, name: String, statink: String)
    case class Result(rule: String, rule_ex: Rule_ex, maps: List[String], maps_ex: List[Maps_ex],
      start: String, start_utc: String, start_t: Int, end: String, end_utc: String, end_t: Int)

    val listResult = (jsonObj \ "result").extract[List[Result]]
    val maps = listResult(0).maps.mkString(",")

    println(maps)
    source.close
  }
}
