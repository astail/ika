package net.astail

import org.joda.time.DateTime

import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._
import java.net.URL


object Main {
  def main(args: Array[String]): Unit = {
    val timestamp: DateTime = DateTime.now()
    ArgumentsParser.parse(args, Arguments()).foreach { a =>
      val time = a.time
      val battle = a.battle

      val api = battle match {
        case "coop" | "coop_weapons_images" => "https://spla2.yuu26.com/coop/schedule"
        case _ => s"https://spla2.yuu26.com/${battle}/${time}"
      }

      val battle2: String = battle match {
        case "regular" => "レギュラー"
        case "gachi" => "ガチ"
        case "league" => "リーグ"
        case "coop" => "バイト"
        case "coop_weapons_images" => "バイト武器"
        case _ => "error"
      }

      val time2: String = time match {
        case "now" => "今"
        case "next" => "次回"
        case _ => "error"
      }

      val kekka = battle match {
        case "coop" => coop(api, time)
        case "coop_weapons_images" => coop_weapons_images(api, time)
        case _ => normal(api, battle2, time2)
      }

      println(kekka)
    }


    def checkTime(s: String, e: String, now: DateTime): Boolean = {
      val sTime = new DateTime(s)
      val eTime = new DateTime(e)
      now.isAfter(sTime) && now.isBefore(eTime)
    }

    def normal(api: String, battle2: String, time2: String): String = {
      val resultData1 = resultData(api)

      def map: String = resultData1.maps.mkString(",")

      def rule: String = resultData1.rule_ex.name

      def sTime = resultData1.start

      def eTime = resultData1.end

      (s"バトル: ${battle2}\n時間: ${time2}, ${sTime} ~ ${eTime}\nルール: ${rule}\nマップ: ${map}")
    }

    def coop(api: String, time: String): String = {
      val resultDataCoop1 = resultDataCoop(api, time)

      def stage: String = resultDataCoop1.stage.name

      def weapons = resultDataCoop1.weapons.map(_.name).mkString(",")

      def sTime = resultDataCoop1.start

      def eTime = resultDataCoop1.end

      val kuma = if (checkTime(sTime, eTime, timestamp))
        "バイト募集中"
      else
        "シフトを確認してくれたまえ"

      (s"${kuma}\n時間: ${sTime} ~ ${eTime}\nステージ: ${stage}\n武器: ${weapons}")
    }

    def coop_weapons_images(api: String, time: String) = {
      val resultDataCoop1 = resultDataCoop(api, time)

      def image = resultDataCoop1.weapons.map(_.image).mkString("\n")

      println(s"${image}")
    }


    def resultData(api: String): Result = {
      val jsonObj = resultD(api)

      implicit val formats = DefaultFormats
      val listResult = (jsonObj \ "result").extract[List[Result]]
      listResult(0)
    }

    def resultDataCoop(api: String, time: String): ResultCoop = {
      val jsonObj = resultD(api)

      implicit val formats = DefaultFormats
      val listResult = (jsonObj \ "result").extract[List[ResultCoop]]

      time match {
        case "now" => listResult(0)
        case "next" => listResult(1)
        case _ => listResult(0)
      }
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
}
