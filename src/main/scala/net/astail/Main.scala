package net.astail

import com.twitter.util.Time
import com.twitter.util.JavaTimer
import com.twitter.conversions.time._
import org.slf4j.{Logger, LoggerFactory}

object Main {
  def main(args: Array[String]): Unit = {

    val logger: Logger = LoggerFactory.getLogger(this.getClass)

    logger.info("================================= start ika =================================")

    discord.MessageListener


    val timer = new JavaTimer

    def gameSet = {
      val setGameStart = ika.ika("coop", "now")

      setGameStart match {
        case Some(x) => discord.setGame(x.toString)
        case _ => None
      }
    }

    // 起動時に設定する
    timer.schedule(Time.now + 2.seconds) {
      gameSet
    }

    // 1時間ごとに見にいって設定する
    timer.schedule(Time.now.ceil(1.hour), 1.hour) {
      gameSet
    }
  }
}
