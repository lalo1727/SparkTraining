package test

import java.math.BigInteger
import java.security.MessageDigest

object Hash {
  def main(args: Array[String]): Unit = {
    println("Scala, hash ...")

    println(hashString("hugo"))
    // 256 bits = 64 caracteres
    // 224, 256, 384, 512
    // MD-5 = 32 caracteres
    // SHA-1 = 40 caracteres
  }

  def hashString(s: String): String = {
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(s.getBytes)
    val bigInt = new BigInteger(1,digest)
    val hashedString = bigInt.toString(16)
    hashedString
  }
}
