package net.astail

import org.joda.time.DateTime

import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._
import java.net.URL

import com.typesafe.config.ConfigFactory
import javax.security.auth.login.LoginException
import net.dv8tion.jda.core.events.{Event, ReadyEvent}
import net.dv8tion.jda.core.{AccountType, JDA, JDABuilder}
import net.dv8tion.jda.core.exceptions.RateLimitedException
import net.dv8tion.jda.core.hooks.EventListener
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import net.dv8tion.jda.core.OnlineStatus
import net.dv8tion.jda.core.entities.Game
import net.dv8tion.jda.core.entities.Game.GameType


object Main {
  def main(args: Array[String]): Unit = {

    val token = ConfigFactory.load.getString("discord_token")

    def ReadyListener = {
      @throws[LoginException]
      @throws[RateLimitedException]
      @throws[InterruptedException]
      val jda: JDA = new JDABuilder(AccountType.BOT).setToken(token).addEventListener(new ReadyListener).buildBlocking
    }

    class ReadyListener extends EventListener {
      def onEvent(event: Event): Unit = {
        if (event.isInstanceOf[ReadyEvent]) println("API is ready!")
      }
    }


    def MessageListener {
      @throws[LoginException]
      @throws[RateLimitedException]
      @throws[InterruptedException]
      val jda: JDA = new JDABuilder(AccountType.BOT).setToken(token).buildBlocking
      jda.addEventListener(new MessageListener)
    }

    class MessageListener extends ListenerAdapter {
      override def onMessageReceived(event: MessageReceivedEvent): Unit = {

        val checkTime: Option[String] = event.getMessage.getContentDisplay match {
          case e if e contains "今" => Some("now")
          case e if e contains "次" => Some("next")
          case _ => None
        }

        val checkBattle: Option[String] = event.getMessage.getContentDisplay match {
          case e if e contains "レギュラー" => Some("regular")
          case e if e contains "ガチ" => Some("gachi")
          case e if e contains "リーグ" => Some("league")
          case e if e contains "バイト" => Some("coop")
          case e if e contains "バイト武器" => Some("coop_weapons_images")
          case _ => None
        }

        val strCheck = checkTime.flatMap(time => checkBattle.map(battle => (battle, time)) )

        val kekka: Option[String] = strCheck match {
          case Some(x) => val (battle, time) = strCheck.get
            ika(battle, time)
          case _ => None
        }

        kekka match {
          case Some(x) => event.getTextChannel.sendMessage(x).queue
          case _ => None
        }
      }
    }

    def setGame = {
      @throws[LoginException]
      @throws[RateLimitedException]
      @throws[InterruptedException]
      val jda: JDA = new JDABuilder(AccountType.BOT).setToken(token).addEventListener(new setGame).buildBlocking
    }

    class setGame extends EventListener {
      def onEvent(event: Event): Unit = {
        if (event.isInstanceOf[ReadyEvent])
          event.getJDA.getPresence.setGame(Game.of(GameType.DEFAULT,"aaaa"))
      }
    }

    ReadyListener
    MessageListener
    setGame
  }


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

    val kekka = battle match {
      case "coop" => coop(api, time)
      case "coop_weapons_images" => coop_weapons_images(api, time)
      case _ => normal(api, battle2, time2)
    }

    Some(kekka)
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
    val timestamp: DateTime = DateTime.now()

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

  def coop_weapons_images(api: String, time: String): String = {
    val resultDataCoop1 = resultDataCoop(api, time)

    def image = resultDataCoop1.weapons.map(_.image).mkString("\n")

    (s"${image}")
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
