package net.astail

import net.astail.ika.resultD
import net.astail.model.timeDisplay
import org.joda.time.DateTime
import org.json4s.DefaultFormats

object coop {
  def checkTime(s: String, e: String, now: DateTime): Boolean = {
    val sTime = new DateTime(s)
    val eTime = new DateTime(e)
    now.isAfter(sTime) && now.isBefore(eTime)
  }

  def coop(api: String, time: String): String = {
    val timestamp: DateTime = DateTime.now()
    val resultDataCoop1 = resultDataCoop(api, time)
    val stage: String = resultDataCoop1.stage.name
    val weapons = resultDataCoop1.weapons.map(_.name).mkString(",")
    val sTime = resultDataCoop1.start
    val eTime = resultDataCoop1.end
    val kuma = if (checkTime(sTime, eTime, timestamp))
      "バイト募集中"
    else
      "シフトを確認してくれたまえ"

    (s"${kuma}\n時間: ${timeDisplay(sTime)} ~ ${timeDisplay(eTime)}\nステージ: ${stage}\n武器: ${weapons}")
  }

  def coop_weapons_images(api: String, time: String): String = {
    val resultDataCoop1 = resultDataCoop(api, time)

    val image = resultDataCoop1.weapons.map(_.image).mkString("\n")

    (s"${image}")
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

}
