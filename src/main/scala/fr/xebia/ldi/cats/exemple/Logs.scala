package fr.xebia.ldi.cats.exemple

import cats.data.Writer

import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by loicmdivad.
  */
object Logs extends App {

  sealed trait LogLevel

  final case class Debug() extends LogLevel
  final case class Info() extends LogLevel
  final case class Warn() extends LogLevel
  final case class Error() extends LogLevel
  final case class Fatal() extends LogLevel

  sealed abstract class LogMessage[T <: LogLevel](val msg: String)

  final case class DebugMessage(message: String) extends LogMessage[Debug](msg = message)
  final case class InfoMessage(message: String) extends LogMessage[Info](msg = message)
  final case class WarnMessage(message: String) extends LogMessage[Warn](msg = message)
  final case class ErrorMessage(message: String) extends LogMessage[Error](msg = message)


  trait MessageLogger[A <: LogLevel]{
    def log(message: LogMessage[A])(implicit logger: Logger): Unit
  }

  object MessageInstances {

    implicit val debugLogger: MessageLogger[Debug] = new MessageLogger[Debug] {
      override def log(message: LogMessage[Debug])(implicit logger: Logger): Unit =
        logger.debug(message.msg)
    }

    implicit val infoLogger: MessageLogger[Info] = new MessageLogger[Info] {
      override def log(message: LogMessage[Info])(implicit logger: Logger): Unit =
        logger.info(message.msg)
    }

    implicit val warnLogger: MessageLogger[Warn] = new MessageLogger[Warn] {
      override def log(message: LogMessage[Warn])(implicit logger: Logger): Unit =
        logger.warn(message.msg)
    }

    implicit val errorLogger: MessageLogger[Error] = new MessageLogger[Error] {
      override def log(message: LogMessage[Error])(implicit logger: Logger): Unit =
        logger.error(message.msg)
    }
  }

  object MessageSyntax {

    implicit class MessageOps[A <: LogLevel](value: LogMessage[A]) {
      def log(implicit loggerOps: MessageLogger[A], logger: Logger): Unit =
        loggerOps.log(value)
    }
  }

  implicit val logger: Logger = LoggerFactory.getLogger(getClass)

  import MessageInstances._
  import MessageSyntax._

  DebugMessage("Oh! somthing not so awsome just append").log

  type Logged[T] = Writer[LogMessage[LogLevel], T]





}
