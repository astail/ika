package net.astail

import java.net.URL

import net.astail.ImageMagickWrapper._
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.parse
import org.slf4j.{Logger, LoggerFactory}

import scala.io.Source
import net.astail.model.timeDisplay

object ika {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)
  val gachiSchedule = "https://spla2.yuu26.com/gachi/schedule"
  val coopSchedule = "https://spla2.yuu26.com/coop/schedule"
  val regularSchedule = "https://spla2.yuu26.com/regular/schedule"

  def ika(battle: String, time: String): Option[String] = {
    val api: String = battle match {
      case "coop_check" | "coop" => coopSchedule
      case _ => s"https://spla2.yuu26.com/${battle}/${time}"
    }

    val battle2 = battle match {
      case "regular" => "レギュラー"
      case "gachi" => "ガチ"
      case "league" => "リーグ"
      case "coop" => "バイト"
      case "coop_check" => "バイト確認"
      case _ => "error"
    }

    val time2 = time match {
      case "now" => "今"
      case "next" => "次"
      case _ => "error"
    }

    battle match {
      case "coop" => Some(coop.coopEndImage(api, time))
      case "coop_check" => Some(coop.setCoop(api, time))
      case _ => Some(normal(api, battle2, time2))
    }
  }


  def normal(api: String, battle2: String, time2: String): String = {
    val resultData1 = resultData(api)
    val map: String = resultData1.maps.mkString(",")
    val rule: String = resultData1.rule_ex.name
    val mapImage = resultData1.maps_ex.map(_.image).mkString("\n")
    val sTime = timeDisplay(resultData1.start)
    val eTime = timeDisplay(resultData1.end)

    s"バトル: ${battle2}\n時間: ${time2}, ${sTime} ~ ${eTime}\nルール: ${rule}\nマップ: ${map}\n${mapImage}"
  }

  def gachiSchedule(battle: String): List[BattleData] = {
    val resultData1 = resultData2(gachiSchedule)

    val gachiBattle2: String = battle match {
      case "ホコ" => "ガチ" + battle + "バトル"
      case _ => "ガチ" + battle
    }

    resultData1.flatMap(x =>
      x.rule match {
        case `gachiBattle2` => {
          val rule = x.rule
          val map: String = x.maps.mkString(",")
          val mapImage: List[String] = x.maps_ex.map(_.image)
          val sTime = timeDisplay(x.start)
          val eTime = timeDisplay(x.end)
          val resizeMaps: List[String] = mapImage.map(x => resize(x, 640, Width))
          val marge = imageAppend(resizeMaps, Width)
          resizeMaps.map(x => delImage(x))
          val margeHttp = model.dirToHttp(marge)
          Some(BattleData(rule, s"${sTime} ~ ${eTime}", map, margeHttp))
        }
        case _ => None
      }
    )
  }

  def allSchedule(r: String): List[BattleData] = {

    val api = if(r == "gachi") gachiSchedule
    else regularSchedule
    val resultData1 = resultData2(api)

    resultData1.flatMap(x => {
      val rule = x.rule
      val map: String = x.maps.mkString(",")
      val mapImage: List[String] = x.maps_ex.map(_.image)
      val sTime = timeDisplay(x.start)
      val eTime = timeDisplay(x.end)
      val resizeMaps: List[String] = mapImage.map(x => resize(x, 640, Width))
      val marge = imageAppend(resizeMaps, Width)
      resizeMaps.map(x => delImage(x))
      val margeHttp = model.dirToHttp(marge)

      Some(BattleData(rule, s"${sTime} ~ ${eTime}", map, margeHttp))
    })
  }


  def resultData2(api: String) = {
    val jsonObj = retry(resultD(api))

    implicit val formats = DefaultFormats
    (jsonObj \ "result").extract[List[Result]]
  }


  def resultData(api: String): Result = {
    val jsonObj = retry(resultD(api))

    implicit val formats = DefaultFormats
    val listResult = (jsonObj \ "result").extract[List[Result]]
    listResult(0)
  }


  def resultD(api: String) = {
    val url = api
    val requestProperties = Map(
      "User-Agent" -> "twitter @astel4696"
    )

    val connection = new URL(url).openConnection
    requestProperties.foreach {
      case (name, value) => connection.setRequestProperty(name, value)
    }
    val str = Source.fromInputStream(connection.getInputStream).mkString
    parse(str)
  }

  def retry[R](f: => R, time: Int = 6): R = {
    try {
      f
    } catch {
      case _: Throwable if time > 0 => {
        logger.info(s"retry time: $time, f: $f")
        // 30秒
        Thread.sleep(30000)
        retry(f, time - 1)
      }
    }
  }
}
