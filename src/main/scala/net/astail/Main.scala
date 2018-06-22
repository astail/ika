package net.astail

import org.joda.time.DateTime

import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._


object Main {
  def main(args: Array[String]): Unit = {
    val timestamp: DateTime = DateTime.now()
    ArgumentsParser.parse(args, Arguments()).foreach { a =>
      val time = a.time
      val battle = a.battle

      val api = battle match {
        case "coop" => "https://spla2.yuu26.com/coop/schedule"
        case _ => s"https://spla2.yuu26.com/${battle}/${time}"
      }

      val battle2: String = battle match {
        case "regular" => "レギュラー"
        case "gachi" => "ガチ"
        case "league" => "リーグ"
        case "coop" => "バイト"
      }
      val time2: String = time match {
        case "now" => "今"
        case "next" => "次回"
      }


      battle match {
        case "coop" => coop(api, time)
        case _ => nomal(api, battle2, time2)
      }
    }

    def checkTime(s: String, e: String, now: DateTime): Boolean = {
      val sTime = new DateTime(s)
      val eTime = new DateTime(e)
      now.isAfter(sTime) && now.isBefore(eTime)
    }

    def nomal(api: String, battle2: String, time2: String) = {
      val resultData1 = resultData(api)

      def map: String = resultData1.maps.mkString(",")
      def rule: String = resultData1.rule_ex.name
      def sTime = resultData1.start
      def eTime = resultData1.end

      println(s"バトル: ${battle2}\n時間: ${time2}, ${sTime} ~ ${eTime}\nルール: ${rule}\nマップ: ${map}")
    }

    def coop(api: String, time: String) = {
      val resultDataCoop1 = resultDataCoop(api, time)

      def stage: String = resultDataCoop1.stage.name
      def weapons = resultDataCoop1.weapons.map(_.name).mkString(",")
      def image = resultDataCoop1.weapons.map(_.image).mkString("\n")
      def sTime = resultDataCoop1.start
      def eTime = resultDataCoop1.end

      val kuma = if (checkTime(sTime,eTime,timestamp))
        "バイト募集中"
      else
        "シフトを確認してくれたまえ"

      println(s"${kuma}\n時間: ${sTime} ~ ${eTime}\nステージ: ${stage}\n武器: ${weapons}\n${image}")
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

    def resultDataCoop(api: String, time: String): ResultCoop = {
      val source = Source.fromURL(api, "utf-8")
      val str = source.getLines.mkString
      val jsonObj = parse(str)

      try {
        implicit val formats = DefaultFormats
        val listResult = (jsonObj \ "result").extract[List[ResultCoop]]

        time match {
          case "now" => listResult(0)
          case "next" => listResult(1)
          case _ => listResult(0)
        }
      } finally {
        source.close
      }
    }
  }
}
