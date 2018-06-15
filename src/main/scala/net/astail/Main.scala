package net.astail

import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._


object Main {
  def main(args: Array[String]): Unit = {
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


    def coop(api: String, time: String) = {
      val resultDataCoop1 = resultDataCoop(api, time)

      def stage: String = resultDataCoop1.stage.name

      def weapons = resultDataCoop1.weapons.map(_.name).mkString(",")
      def sTime = resultDataCoop1.start
      def eTime = resultDataCoop1.end



      println(s"${sTime} ~ ${eTime}")
      println(s"ステージ: ${stage}, 武器: ${weapons}")
    }

    def nomal(api: String, battle2: String, time2: String) = {
      val resultData1 = resultData(api)

      def map: String = resultData1.maps.mkString(",")

      def rule: String = resultData1.rule_ex.name

      println(s"バトル: ${battle2}, 時間: ${time2}, ルール: ${rule}, マップ: ${map}")
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
}
