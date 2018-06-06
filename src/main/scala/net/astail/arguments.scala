package net.astail

import scopt.OptionParser

case class Arguments(
    rule: String = "regular",
    time: String = "now")

object ArgumentsParser extends OptionParser[Arguments]("ika") {
  override val showUsageOnError = true

  opt[String]('r',"rule") valueName ("<rule>") action { (arg, obj) =>
    val os = parseRule(arg)
    if (os.isEmpty) throw new Exception("invalid arg 'rule'")
    obj.copy(rule = os.get)
  } text ("デフォルトはregularです。 regular | gachi | league")

  opt[String]('t', "time") valueName ("<time>") action { (arg, obj) =>
    val os = parseTime(arg)
    if (os.isEmpty) throw new Exception("invalid arg 'time'")
    obj.copy(time = os.get)
  } text ("デフォルト値はnowです。 now | next")

  help("help") text "このヘルプを表示します。"

  def parseRule(s: String): Option[String] = {
    s match {
      case "regular"|"gachi"|"league" => Some(s)
      case _ => None
    }
  }

  def parseTime(s: String): Option[String] = {
    s match {
      case "now"|"next" => Some(s)
      case _ => None
    }
  }
}
