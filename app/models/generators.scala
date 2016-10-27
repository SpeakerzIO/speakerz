package models

import scala.util.Random

object Generators {

  private[this] val CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray.map(_.toString)
  private[this] val EXTENDED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789*$£%)([]!=+-_:/;.><&".toCharArray.map(_.toString)
  private[this] val INIT_STRING = for(i <- 0 to 15) yield Integer.toHexString(i)

  def uuid: String = (for {
    c <- 0 to 36
  } yield c match {
    case i if i == 9 || i == 14 || i == 19 || i == 24 => "-"
    case i if i == 15 => "4"
    case i if c == 20 => INIT_STRING((Random.nextDouble() * 4.0).toInt | 8)
    case i => INIT_STRING((Random.nextDouble() * 15.0).toInt | 0)
  }).mkString("")

  def token(characters: Array[String], size: Int): String = (for {
    i <- 0 until size
  } yield characters(Random.nextInt(characters.length))).mkString("")

  def token(size: Int): String = token(CHARACTERS, size)
  def token: String = token(64)
  def extendedToken(size: Int): String = token(EXTENDED_CHARACTERS, size)
  def extendedToken: String = token(EXTENDED_CHARACTERS, 64)
}
