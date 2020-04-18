package com.romibuzi

import zio.ZIO
import zio.console.putStrLn

object Main extends zio.App {
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    putStrLn("Hello world!") *> ZIO.succeed(0)
  }
}
