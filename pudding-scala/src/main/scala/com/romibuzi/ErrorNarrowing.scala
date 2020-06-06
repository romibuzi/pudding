package com.romibuzi

import java.io.IOException

import zio.{ExitCode, ZIO}
import zio.console.putStrLn

import scala.io.StdIn.readLine

object ErrorNarrowing extends zio.App {
  val myReadLine: ZIO[Any, IOException, String] = ZIO.effect(readLine()).refineToOrDie[IOException]

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] =
    (for {
      _ <- putStrLn("What is your name?")
      name <- myReadLine
      _ <- putStrLn(s"Good to meet you, $name")
    } yield ExitCode.success) orElse ZIO.succeed(ExitCode.failure)
}
