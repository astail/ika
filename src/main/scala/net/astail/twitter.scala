package net.astail

import com.danielasfregola.twitter4s.TwitterRestClient
import net.astail.redis._
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

object twitter {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def twitterRest(screenName: String, count: Int) = {
    val client = TwitterRestClient()
    val timeLine = client.userTimelineForUser(screenName, count = count)

    timeLine.onComplete {
      case Success(msg) => for (tweet <- msg.data.reverse) {
        tweet.retweeted_status match {
          case None => {
            val tweetUrl: String = "https://twitter.com/" + screenName + "/status/" + tweet.id
            toRedis(tweetUrl)
            Thread.sleep(1000)
            redisToDiscord(tweetUrl)
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
}
