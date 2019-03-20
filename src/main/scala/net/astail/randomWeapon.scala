package net.astail

import org.jsoup.Jsoup
import scala.collection.JavaConverters._
import scala.util.Random
import com.typesafe.config.ConfigFactory

object randomWeapon {
  val url = ConfigFactory.load.getString("splatoon_weapon_url")
  val r = new Random
  val result = Jsoup.connect(url).get
  val resultWeaponList = result.getElementsByClass("a-img").eachAttr("alt").asScala.toList
  val weaponList = resultWeaponList.filterNot(_ contains "画像")

  def shuffleWepon: String = {
    r.shuffle(weaponList).head
  }
}
