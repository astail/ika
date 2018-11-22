package net.astail

import net.astail.ika.resultD
import net.astail.model.timeDisplay
import com.github.nscala_time.time.Imports._
import com.typesafe.config.ConfigFactory
import org.joda.time.DateTime
import org.joda.time.Hours
import org.json4s.DefaultFormats
import net.astail.ImageMagickWrapper._

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
    val stageImage: String = resultDataCoop1.stage.image
    val weapons = resultDataCoop1.weapons.map(_.name).mkString(",")
    val sTime = resultDataCoop1.start
    val eTime = resultDataCoop1.end
    val kuma = if (checkTime(sTime, eTime, timestamp))
      "バイト募集中"
    else
      "シフトを確認してくれたまえ"

    (s"${kuma}\n時間: ${timeDisplay(sTime)} ~ ${timeDisplay(eTime)}\nステージ: ${stage}\n武器: ${weapons}\n${stageImage}")
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

  def coopEndImage(api: String, time: String): String = {
    val resultDataCoop1 = resultDataCoop(api, time)
    val stageImage: String = resultDataCoop1.stage.image
    val weaponsImage: List[String] = resultDataCoop1.weapons.map(_.image)

    val timestamp: DateTime = DateTime.now()
    val stage: String = resultDataCoop1.stage.name
    val weapons = resultDataCoop1.weapons.map(_.name).mkString(",")
    val sTime = resultDataCoop1.start
    val eTime = resultDataCoop1.end
    val kuma = if (checkTime(sTime, eTime, timestamp))
      "バイト募集中"
    else
      "シフトを確認してくれたまえ"

    val newStageImage: String = mergeWeaponsAndMaps(stageImage, weaponsImage)

    val newStageImageHttp: String = dirToHttp(newStageImage)

    (s"${kuma}\n時間: ${timeDisplay(sTime)} ~ ${timeDisplay(eTime)}\nステージ: ${stage}\n武器: ${weapons}\n${newStageImageHttp}")
  }

  def setCoop(api: String, time: String): String = {
    val timestamp: DateTime = DateTime.now()
    val resultDataCoop1 = resultDataCoop(api, time)
    val sTime = resultDataCoop1.start
    val eTime = resultDataCoop1.end

    if (checkTime(sTime, eTime, timestamp)) {
      val endHour: Int = Hours.hoursBetween(timestamp, eTime.toDateTime).getHours()
      s"@${endHour}時間 バイト募集中"
    }
    else {
      val startHour: Int = Hours.hoursBetween(timestamp, sTime.toDateTime).getHours()
      s"@${startHour}時間 シフトを確認してくれたまえ"
    }
  }

  def mergeWeaponsAndMaps(map: String, weapons: List[String]): String = {
    val mapData = sizeCheck(map)
    val weaponsImage: String = imageAppend(weapons, Width)
    val resizeWeaponsImage: String = resize(weaponsImage, mapData.width, Width)
    val merge = imageAppend(List(map, resizeWeaponsImage), Height)
    delImage(resizeWeaponsImage)

    merge
  }

  def dirToHttp(name: String): String = {
    val domain = ConfigFactory.load.getString("domain")
    domain + "/" + name.split('/').last
  }
}
