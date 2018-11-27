package net.astail

import com.typesafe.config.ConfigFactory
import javax.security.auth.login.LoginException
import net.dv8tion.jda.core.{AccountType, JDA, JDABuilder}
import net.dv8tion.jda.core.entities.Game
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

      event.getMessage.getContentDisplay match {
        case e if e contains "バイト一覧" => {
          val x: Option[String] = ika.ika("all_coop", "all")
          event.getTextChannel.sendMessage(x.getOrElse("エラー")).queue
        }
        case _ => {

          val checkTime: Option[String] = event.getMessage.getContentDisplay match {
            case e if e startsWith "今の" => Some("now")
            case e if e startsWith "次の" => Some("next")
            case _ => None
          }

          val dictionary: Seq[(String, String)] = Seq(
            "レギュラー" -> "regular",
            "ガチ" -> "gachi",
            "リーグ" -> "league",
            "バイト" -> "new_coop",
            "バイト確認" -> "coop_check",
            "バイト武器" -> "coop_weapons_images",
            "エリア" -> "area",
            "ヤグラ" -> "scaffold",
            "ホコ" -> "grampus",
            "アサリ" -> "clams"
          )

          val checkBattle: Option[String] = dictionary.collectFirst {
            case (keyword, result) if event.getMessage.getContentDisplay endsWith keyword => result
          }

          val strCheck: Option[(String, String)] = checkTime.flatMap(time => checkBattle.map(battle => (battle, time)))

          val kekka: Option[String] = strCheck match {
            case Some(x) => {
              val (battle, time) = strCheck.get
              ika.ika(battle, time)
            }
            case _ => None
          }

          kekka match {
            case Some(x) => event.getTextChannel.sendMessage(x).queue
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

  def sendMessage(message: String) = {
    import net.dv8tion.jda.webhook.WebhookClient
    import net.dv8tion.jda.webhook.WebhookClientBuilder

    val webhook: WebhookClientBuilder = new WebhookClientBuilder(discordWebhook)
    val webhookCli: WebhookClient = webhook.build()

    try {
      webhookCli.send(message)
    } catch {
      case _ => logger.error(s"sendMessage error: $message")
    } finally {
      webhookCli.close()
    }
  }

}
