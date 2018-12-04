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

    def gameSet = {
      val setGameStart = ika.ika("coop_check", "now")

      setGameStart match {
        case Some(x) => discord.setGame(x.toString)
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
