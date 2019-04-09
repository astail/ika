package net.astail

import com.danielasfregola.twitter4s.TwitterRestClient
import org.joda.time.DateTime
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
            if (checkTweetUser(screenName, tweet.text)) {

              val tweetAt: DateTime = new DateTime(tweet.created_at)
              val userName: String = screenName
              val tweetId: Long = tweet.id
              val tweetUrl: String = "https://twitter.com/" + userName + "/status/" + tweetId

              toRdbDiscord(tweetId, userName, tweet.text, tweetUrl, tweetAt)
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


  def toRdbDiscord(tweetId: Long, userName: String, tweet: String, tweetUrl: String, tweetAt: DateTime) = {
    TweetRdb.getTweetByTweetname(tweetId) match {
      case Some(s) => false
      case None =>
        TweetRdb.createTweet(tweetId, userName, tweet, tweetUrl, tweetAt)
        Thread.sleep(1000)
        discord.webhookSendMessage(tweetUrl)
    }
  }

  def checkTweetUser(screenName: String, tweet: String): Boolean = {
    screenName match {
      case "SplatoonJP" => (!tweet.contains("甲子園") && !tweet.contains("代表")) || tweet.contains("グッズ")
      case "astel4696" => tweet.contains("#Splatoon2 #スプラトゥーン2 #NintendoSwitch")
    }
  }
}
