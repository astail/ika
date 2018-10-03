package net.astail

object model {

  def timeDisplay(time: String) = {
    time.replace("-", "/").replace("T", "-")
  }
}
