package fr.xebia.ldi.cats.exemple

import cats.data.Writer

import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by loicmdivad.
  */
object Logs extends App {

  sealed trait Level

  final case object Debug extends Level
  final case object Info extends Level
  final case object Warn extends Level
  final case object Error extends Level

  final case class LogMessage(level: Level, msg: String){
    def log(implicit logger: Logger) = level match {
      case Debug => logger.debug(msg)
      case Info => logger.info(msg)
      case Warn => logger.warn(msg)
      case Error => logger.error(msg)
    }
  }

  implicit val logger: Logger = LoggerFactory.getLogger(getClass)

  //LogMessage(Debug, "Oh! something not so awsome just append").log
  //LogMessage(Error, "Oh! something really important just append").log

  //type Logged[T] = Writer[Vector[Message[_]], T]

  type Logged[T] = Writer[Vector[LogMessage], T]


  import cats.syntax.applicative._
  import cats.instances.vector._
  import cats.syntax.semigroup._
  import cats.syntax.option._
  import cats.syntax.writer._
  import cats.syntax.option._


  val mapping = Map("a" -> 'alpha, "b" -> 'beta, "d" -> 'delta)

  def doRun(payload: Option[String]): Logged[Option[String]] = {

    for {
      a <- payload.pure[Logged]
      _ <- a match {
        case Some(key) => Vector(LogMessage(Debug, s"looking for the key $key")).tell
        case None => Vector(LogMessage(Info, "the key was empty")).tell
      }
      b <- a.flatMap(mapping.get).pure[Logged]
      _ <- b match {
        case None => Vector(LogMessage(Warn, s"the map does not contain the key $a")).tell
        case _ => Option.empty[String].pure[Logged]
      }


    } yield a

  }

  def doRunAgain(payload: Option[String]): Logged[Option[String]] = {
    payload.pure[Logged].mapBoth { (messages, mayBeKey) =>

      mayBeKey match {
        case None => (messages ++ Vector(LogMessage(Debug, "the key was empty")), None)
        case Some(key) => (messages, mapping.get(key))
      }

    }.mapBoth {(messages, mayBeSymb) =>

      mayBeSymb match {
        case None => (messages ++ Vector(LogMessage(Info, "the symbol was not defined")), None)
        case Some(symbol) => (messages, Some(symbol.toString()))
      }

    }
  }

  println("EXEMPLE WITH None " + "---" * 30)
  doRun(None).run._1.foreach(_.log)
  println("\n\n")

  println("EXEMPLE WITH C " + "---" * 30)
  doRun(Some("c")).run._1.foreach(_.log)
  println("\n\n")

  println("EXEMPLE WITH A " + "---" * 30)
  doRun(Some("a")).run._1.foreach(_.log)
  println("\n\n")

}
