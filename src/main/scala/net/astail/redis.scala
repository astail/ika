package net.astail

import com.redis.RedisClient
import com.typesafe.config.ConfigFactory


object redis {
  val host = ConfigFactory.load.getString("redis.host")
  val port = ConfigFactory.load.getInt("redis.port")

  val r = new RedisClient(host, port)


  def setKey(key: String, value: String) = {
    r.set(key, value)
  }

  def getKey(key: String): Option[String] = {
    r.get(key)
  }

  def delKey(key: String): Option[Long] = {
    r.del(key)
  }
}
