package net.astail

import java.net.URL

import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.parse

import scala.io.Source

object ika {
  def ika(battle: String, time: String): Option[String] = {

    val api = battle match {
      case "coop" | "coop_weapons_images" => "https://spla2.yuu26.com/coop/schedule"
      case _ => s"https://spla2.yuu26.com/${battle}/${time}"
    }

    val battle2 = battle match {
      case "regular" => "レギュラー"
      case "gachi" => "ガチ"
      case "league" => "リーグ"
      case "coop" => "バイト"
      case "coop_weapons_images" => "バイト武器"
      case _ => "error"
    }

    val time2 = time match {
      case "now" => "今"
      case "next" => "次"
      case _ => "error"
    }

    battle match {
      case "coop" => Some(coop.coop(api, time))
      case "coop_weapons_images" => Some(coop.coop_weapons_images(api, time))
      case _ => Some(normal(api, battle2, time2))
    }
  }



  def normal(api: String, battle2: String, time2: String): String = {
    val resultData1 = resultData(api)
    val map: String = resultData1.maps.mkString(",")
    val rule: String = resultData1.rule_ex.name
    val sTime = resultData1.start
    val eTime = resultData1.end

    (s"バトル: ${battle2}\n時間: ${time2}, ${sTime} ~ ${eTime}\nルール: ${rule}\nマップ: ${map}")
  }


  def resultData(api: String): Result = {
    val jsonObj = resultD(api)

    implicit val formats = DefaultFormats
    val listResult = (jsonObj \ "result").extract[List[Result]]
    listResult(0)
  }


  def resultD(api: String) = {
    val stackOverflowURL = api
    val requestProperties = Map(
      "User-Agent" -> "@astel4696"
    )
    val connection = new URL(stackOverflowURL).openConnection
    requestProperties.foreach({
      case (name, value) => connection.setRequestProperty(name, value)
    })
    val str = Source.fromInputStream(connection.getInputStream).mkString
    parse(str)
  }

}
