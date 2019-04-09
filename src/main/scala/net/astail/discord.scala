package net.astail

import com.typesafe.config.ConfigFactory
import javax.security.auth.login.LoginException
import net.dv8tion.jda.core.{AccountType, JDA, JDABuilder}
import net.dv8tion.jda.core.entities.{Game, Message}
import net.dv8tion.jda.core.entities.Game.GameType
import net.dv8tion.jda.core.events.{Event, ReadyEvent}
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.exceptions.RateLimitedException
import net.dv8tion.jda.core.hooks.{EventListener, ListenerAdapter}
import org.slf4j.{Logger, LoggerFactory}

object discord {
  val token = ConfigFactory.load.getString("discord_token")
  val discordWebhook = ConfigFactory.load.getString("discord_webhook")

  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def MessageListener {
    @throws[LoginException]
    @throws[RateLimitedException]
    @throws[InterruptedException]
    val jda: JDA = new JDABuilder(AccountType.BOT).setToken(token).buildBlocking
    jda.addEventListener(new MessageListener)
  }

  class MessageListener extends ListenerAdapter {
    override def onMessageReceived(event: MessageReceivedEvent): Unit = {
      // 他botや自分自身のコメントに反応してしまうのを防ぐ
      if (!event.getAuthor.isBot) {
        def sendMessage(x: String) = event.getTextChannel.sendMessage(x).queue

        def gachiToDiscord(buttle: String) = {
          sendMessage("確認中")
          ika.gachiSchedule(buttle).foreach(x => sendMessage(s"${x.rule}: ${x.time}, マップ: ${x.map}\n${x.url}"))
        }

        val message = event.getMessage.getContentDisplay
        message match {
          case "バイト" =>
            sendMessage("確認中")
            val now: Option[String] = ika.ika("coop", "now")
            val next: Option[String] = ika.ika("coop", "next")

            sendMessage(now.getOrElse("エラー"))
            Thread.sleep(1000)
            sendMessage(next.getOrElse("エラー"))

          case "ガチ" | "ガチ一覧" | "ガチマ" =>
            sendMessage("確認中")
            val allGachiResult = ika.allSchedule("gachi")
            allGachiResult.foreach(x => sendMessage(s"${x.rule}: ${x.time}, マップ: ${x.map}\n${x.url}"))

          case "ガチエリア" | "エリア" | "エリア一覧" => gachiToDiscord("エリア")
          case "ガチヤグラ" | "ヤグラ" | "ヤグラ一覧" => gachiToDiscord("ヤグラ")
          case "ガチホコ" | "ホコ" | "ガチホコバトル" | "ホコ一覧" => gachiToDiscord("ホコ")
          case "ガチアサリ" | "アサリ" | "アサリ一覧" => gachiToDiscord("アサリ")

          case "レギュラー" | "レギュラー一覧" =>
            sendMessage("確認中")
            val allGachiResult = ika.allSchedule("regular")

            allGachiResult.foreach(x => sendMessage(s"${x.rule}: ${x.time}, マップ: ${x.map}\n${x.url}"))

          case "武器" => sendMessage(randomWeapon.shuffleWeapon)
          case "武器武器" => randomWeapon.shuffleWeapons(4).foreach(x => sendMessage(x._1 + ", " + x._2))

          case _ =>

            val checkTime: Option[String] = message match {
              case e if e startsWith "今の" => Some("now")
              case e if e startsWith "次の" => Some("next")
              case _ => None
            }

            val dictionary: Seq[(String, String)] = Seq(
              "レギュラー" -> "regular",
              "ガチ" -> "gachi",
              "ガチマ" -> "gachi",
              "リーグ" -> "league",
              "リグマ" -> "league",
              "バイト" -> "coop",
              "バイト確認" -> "coop_check"
            )

            val checkBattle: Option[String] = dictionary.collectFirst {
              case (keyword, result) if message endsWith keyword => result
            }

            val strCheck: Option[(String, String)] = checkTime.flatMap(time => checkBattle.map(battle => (battle, time)))

            val kekka: Option[String] = strCheck match {
              case Some(x) =>
                val (battle, time) = strCheck.get
                ika.ika(battle, time)
              case _ => None
            }

            kekka match {
              case Some(x) => sendMessage(x)
              case _ => None
            }
        }
      }
    }
  }

  def setGame(setName: String) = {
    @throws[LoginException]
    @throws[RateLimitedException]
    @throws[InterruptedException]
    val jda: JDA = new JDABuilder(AccountType.BOT).setToken(token).addEventListener(new setGame(setName)).buildBlocking
  }

  class setGame(setName: String) extends EventListener {
    def onEvent(event: Event): Unit = {
      if (event.isInstanceOf[ReadyEvent]) {
        logger.info("=== set Game start ===")
        event.getJDA.getPresence.setGame(Game.of(GameType.DEFAULT, setName))
        logger.info(s"$setName")
        logger.info("=== set Game end ===")
      }
    }
  }

  def webhookSendMessage(message: String, roomWebhook: String = discordWebhook) = {
    import net.dv8tion.jda.webhook.WebhookClient
    import net.dv8tion.jda.webhook.WebhookClientBuilder

    val webhook: WebhookClientBuilder = new WebhookClientBuilder(roomWebhook)
    val webhookCli: WebhookClient = webhook.build()

    try {
      webhookCli.send(message)
    } catch {
      case e: Throwable => logger.error(s"sendMessage error: $message")
    } finally {
      webhookCli.close()
    }
  }
}
