package net.astail

import com.twitter.util.Time
import com.twitter.util.JavaTimer
import com.twitter.conversions.time._
import com.typesafe.config.ConfigFactory
import org.slf4j.{Logger, LoggerFactory}

object Main {
  def main(args: Array[String]): Unit = {

    val logger: Logger = LoggerFactory.getLogger(this.getClass)

    logger.info("================================= start ika =================================")

    skinny.DBSettings.initialize()
    discord.MessageListener


    val timer = new JavaTimer
    val twitterUserNameIka = ConfigFactory.load.getString("twitter_name_ika")
    val twitterUserNameASTEL = ConfigFactory.load.getString("twitter_name_astel")


    def gachCheck(matchData: String) = {
      val r = List("エリア", "ホコ", "ヤグラ", "アサリ")
      r.collectFirst { case i if matchData contains i => i }
    }

    def gameSet = {
      val gachCheckNow: Option[String] = ika.ika("gachi", "now")
      val gachCheckNext: Option[String] = ika.ika("gachi", "next")
      val setGameStartCoop = ika.ika("coop_check", "now")

      (gachCheckNow, gachCheckNext, setGameStartCoop) match {
        case (Some(x), Some(y), Some(z)) =>
          val gachNow: Option[String] = gachCheck(x)
          val gachNext: Option[String] = gachCheck(y)
          (gachNow, gachNext) match {
            case (Some(now: String), Some(next: String)) => discord.setGame(now.head.toString + " -> " + next.head.toString + " / " + z.toString)
            case _ => discord.setGame("setup error")
          }
        case _ => discord.setGame("setup error")
      }
    }

    def coopCheckSend = {
      val nowCoop = ika.ika("coop", "now")
      val nextCoop = ika.ika("coop", "next")
      val discordWebhookCoop = ConfigFactory.load.getString("discord_webhook_coop")

      val getKey = redis.getKey("ika").get
      val resultKey = nowCoop.get.split("_").last

      if (getKey != resultKey) {
        redis.setKey("ika", resultKey)
        discord.webhookSendMessage(nowCoop.get + "\n" + nextCoop.get, discordWebhookCoop)
      }
    }

    // 起動時に設定する
    timer.schedule(Time.now + 2.seconds) {
      gameSet
      coopCheckSend
      // テスト用
      //twitter.twitterRest(twitterUserNameIka, 10)
    }


    // 1時間ごとに見にいって設定する
    timer.schedule(Time.now.ceil(1.hour), 1.hour) {
      Thread.sleep(30000)
      gameSet
      coopCheckSend
    }

    // 15分ごとに見にいく
    timer.schedule(Time.now.ceil(1.hour), 15.minute) {
      logger.info("15分")
      twitter.twitterRest(twitterUserNameIka, 10)
      Thread.sleep(10000)
      twitter.twitterRest(twitterUserNameASTEL, 10)
    }
  }
}
