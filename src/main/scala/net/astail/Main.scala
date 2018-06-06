package net.astail

import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._


object Main {
  def main(args: Array[String]): Unit = {
    //val api = "https://spla2.yuu26.com/regular/now"
    val api = "https://astail.net/ika.html"
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