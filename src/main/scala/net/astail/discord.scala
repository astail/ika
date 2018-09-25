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
  val ikahelp = "https://github.com/astail/ika/wiki#readme"

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

      val checkHelp: Option[String] = event.getMessage.getContentDisplay match {
        case e if e contains "ikahelp" => Some("ikahelp")
        case _ => None
      }

      val checkTime: Option[String] = event.getMessage.getContentDisplay match {
        case e if e contains "今の" => Some("now")
        case e if e contains "次の" => Some("next")
        case _ => None
      }

      val checkBattle: Option[String] = event.getMessage.getContentDisplay match {
        case e if e contains "レギュラー" => Some("regular")
        case e if e contains "ガチ" => Some("gachi")
        case e if e contains "リーグ" => Some("league")
        case e if e contains "バイト武器" => Some("coop_weapons_images")
        case e if e contains "バイト" => Some("coop")
        case _ => None
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

      checkHelp match {
        case Some("ikahelp") => event.getTextChannel.sendMessage(ikahelp).queue
        case _ => None
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

}
