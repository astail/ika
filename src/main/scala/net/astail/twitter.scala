package net.astail

import com.danielasfregola.twitter4s.TwitterRestClient
import net.astail.redis._
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

object twitter {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def twitterRest(screenName: String, count: Int): Future[Unit] = {
    val client = TwitterRestClient()
    val timeLine = client.userTimelineForUser(screenName, count = count)

    timeLine.onComplete {
      case Success(msg) => for (tweet <- msg.data.reverse) {
        tweet.retweeted_status match {
          case None => {
            if (checkTweetUser(screenName, tweet.text)) {
              val tweetUrl: String = "https://twitter.com/" + screenName + "/status/" + tweet.id
              toRedis(tweetUrl)
              Thread.sleep(1000)
              redisToDiscord(tweetUrl)
            }
          }
          case Some(s) =>
        }
      }
      case Failure(t) => println(t.getMessage())
    }

    Await.ready(timeLine, Duration.Inf)
    client.shutdown()
  }


  def toRedis(url: String): Boolean = {
    getKey(url) match {
      case Some(s) => false
      case None => setKey(url, "0")
    }
  }


  def redisToDiscord(url: String) = {
    getKey(url) match {
      case Some(s) => {
        if (s == "0") {
          discord.sendMessage(url)
          setKey(url, "1")
        }
      }
      case None =>
    }
  }

  def checkTweetUser(screenName: String, tweet: String): Boolean = {
    screenName match {
      case "SplatoonJP" => ((!tweet.contains("甲子園") && !tweet.contains("代表")) || (tweet.contains("グッズ")))
      case "astel4696" => tweet.contains("#Splatoon2 #スプラトゥーン2 #NintendoSwitch")
      case _ => true
    }
  }
}
