package net.astail

import com.typesafe.config.ConfigFactory

object model {

  def timeDisplay(time: String): String = {
    time.replace("-", "/").replace("T", "-")
  }

  def dirToHttp(name: String): String = {
    val domain = ConfigFactory.load.getString("domain")
    domain + "/" + name.split('/').last
  }
}
