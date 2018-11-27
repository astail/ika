package net.astail

object model {

  def timeDisplay(time: String): String = {
    time.replace("-", "/").replace("T", "-")
  }
}
