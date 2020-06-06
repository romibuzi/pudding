package com.romibuzi

import zio.{ExitCode, ZIO}
import zio.console.putStrLn

object Main extends zio.App {
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] = {
    putStrLn("Hello world!") *> ZIO.succeed(ExitCode.success)
  }
}
