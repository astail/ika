package net.astail

import org.joda.time.DateTime
import scalikejdbc._
import skinny.orm._

case class Tweet(
  id: Long,
  tweetId: Long,
  userName: String,
  tweet: String,
  tweetUrl: String,
  tweetAt: DateTime,
  createdAt: DateTime
)

object TweetRdb extends SkinnyCRUDMapper[Tweet] {
  override lazy val tableName = "tweet"
  override val defaultAlias: Alias[Tweet] = createAlias("tw")

  override def extract(rs: WrappedResultSet, n: ResultName[Tweet]): Tweet = autoConstruct(rs, n)

  def createTweet(tweetId: Long, userName: String, tweet: String, tweetUrl: String, tweetAt: DateTime)(implicit session: DBSession = autoSession): Long = {
    TweetRdb.createWithNamedValues(
      column.tweetId -> tweetId,
      column.userName -> userName,
      column.tweet -> tweet,
      column.tweetUrl -> tweetUrl,
      column.tweetAt -> tweetAt,
      column.createdAt -> DateTime.now
    )
  }

  def getTweetByTweetname(tweetId: Long)(implicit session: DBSession = autoSession) = {
    TweetRdb.findBy(sqls.eq(column.tweetId, tweetId))
  }

}
