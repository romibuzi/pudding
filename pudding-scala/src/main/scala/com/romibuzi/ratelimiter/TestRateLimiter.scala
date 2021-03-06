package com.romibuzi.ratelimiter

import zio.{ExitCode, ZIO}
import zio.clock.Clock
import zio.console.{Console, putStrLn}

object Program {
  def apply(): ZIO[Console with Clock, Nothing, ExitCode] = {
    for {
      limiter <- RateLimiter.make(perSecond = 1, buffer = 990)
      _ <- ZIO.foreach(1 to 1000) { i =>
        limiter.rateLimit(putStrLn(i.toString))
      }
    } yield ExitCode.success
  }
}

// https://medium.com/wix-engineering/building-a-super-easy-rate-limiter-with-zio-88f1ccb49776
object TestRateLimiter extends zio.App {
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] =
    Program.apply()
}

object TestRateLimiterOutsideZIO {
  def main(args: Array[String]): Unit = {
    val runtime = zio.Runtime.default
    runtime.unsafeRun(Program.apply())
  }
}
