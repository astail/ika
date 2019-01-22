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

    discord.MessageListener


    val timer = new JavaTimer
    val twitterUserName = ConfigFactory.load.getString("twitter_name")

    def gachCheck(matchData: Option[String]): String = {
      matchData match {
        case Some(x) if (x contains "エリア") => "エ"
        case Some(x) if (x contains "ホコ") => "ホ"
        case Some(x) if (x contains "ヤグラ") => "ヤ"
        case Some(x) if (x contains "アサリ") => "ア"
        case _ => "error"
      }
    }

    def gameSet = {
      val setGameStartCoop = ika.ika("coop_check", "now")
      val gachCheckNow: Option[String] = ika.ika("gachi", "now")
      val gachCheckNext: Option[String] = ika.ika("gachi", "next")

      val gachNow: String = gachCheck(gachCheckNow)
      val gachNext: String = gachCheck(gachCheckNext)


      setGameStartCoop match {
        case Some(x) => discord.setGame(gachNow + " -> " + gachNext + " / " + x.toString)
        case _ => None
      }
    }

    // 起動時に設定する
    timer.schedule(Time.now + 2.seconds) {
      gameSet
      // テスト用
      // twitter.twitterRest(twitterUserName, 10)
    }

    // 1時間ごとに見にいって設定する
    timer.schedule(Time.now.ceil(1.hour), 1.hour) {
      gameSet
    }

    // 15分ごとに見にいく
    timer.schedule(Time.now.ceil(1.hour), 15.minute) {
      logger.info("15分")
      twitter.twitterRest(twitterUserName, 10)
      Thread.sleep(10000)
      twitter.twitterRest("astel4696", 10)
    }

  }
}
