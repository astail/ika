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
    val kuma = if (checkTime(sTime, eTime, timestamp)) {
      val endHour: Int = Hours.hoursBetween(timestamp, eTime.toDateTime).getHours()
      s"バイト募集中 @${endHour}時間"
    }
    else {
      val startHour: Int = Hours.hoursBetween(timestamp, sTime.toDateTime).getHours()
      s"シフトを確認してくれたまえ @${startHour}時間"
    }

    val newStageImage: String = mergeWeaponsAndMaps(stageImage, weaponsImage)

    val newStageImageHttp: String = model.dirToHttp(newStageImage)

    s"""${kuma}
       |時間: ${timeDisplay(sTime)} ~ ${timeDisplay(eTime)}
       |ステージ: ${stage}
       |武器: ${weapons}
       |${newStageImageHttp}""".stripMargin
  }

  def setCoop(api: String, time: String): String = {
    val timestamp: DateTime = DateTime.now()
    val resultDataCoop1 = resultDataCoop(api, time)
    val sTime = resultDataCoop1.start
    val eTime = resultDataCoop1.end

    if (checkTime(sTime, eTime, timestamp)) {
      val endHour: Int = Hours.hoursBetween(timestamp, eTime.toDateTime).getHours()
      s"バイト募 @${endHour}時間"
    }
    else {
      val startHour: Int = Hours.hoursBetween(timestamp, sTime.toDateTime).getHours()
      s"シフト確 @${startHour}時間"
    }
  }

  def mergeWeaponsAndMaps(map: String, weapons: List[String]): String = {
    val mapData = sizeCheck(map)

    // ?だけ画像サイズが違うため揃える
    val questionUrl: String = "https://app.splatoon2.nintendo.net/images/coop_weapons/746f7e90bc151334f0bf0d2a1f0987e311b03736.png"
    val urlList = weapons.map(x =>
      if (x == questionUrl) resize(x, 256, Width) else x)

    val weaponsImage: String = imageAppend(urlList, Width)
    val resizeWeaponsImage: String = resize(weaponsImage, mapData.width, Width)
    val merge = imageAppend(List(map, resizeWeaponsImage), Height)
    delImage(resizeWeaponsImage)
    delImage(questionUrl.split('/').last, true)

    merge
  }
}
