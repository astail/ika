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
    val values = jsonObj.asInstanceOf[JObject].values

    val result = values("result")
    val resultTypes = result.asInstanceOf[List[Map[String,Any]]]
    val maps_ex = resultTypes(0)("maps_ex")
    val map = maps_ex.asInstanceOf[List[Map[String,Any]]]

    for(xx <- map) println(xx("name"))

    source.close
  }
}