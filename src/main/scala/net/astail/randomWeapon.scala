package net.astail

import org.jsoup.Jsoup
import scala.collection.JavaConverters._
import scala.util.Random
import com.typesafe.config.ConfigFactory

object randomWeapon {
  val url = ConfigFactory.load.getString("splatoon_weapon_url")
  val r = new Random
  val result = Jsoup.connect(url).get
  val resultWeaponsNameList = result.getElementsByClass("a-img").eachAttr("alt").asScala.toList
  val resultWeaponsImageList = result.getElementsByClass("a-img").eachAttr("src").asScala.toList
  val resultWeaponsList = resultWeaponsNameList zip resultWeaponsImageList
  val weaponsList = resultWeaponsList.filterNot(_._1 contains "画像").filterNot(_._1 contains "レプリカ")

  def shuffleWeapon(n: Int): List[(String, String)] = {
    r.shuffle(weaponsList).take(n)
  }
}
