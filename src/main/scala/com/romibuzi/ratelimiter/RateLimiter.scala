package com.romibuzi.ratelimiter

import zio._
import zio.clock.Clock
import zio.duration.Duration

import scala.concurrent.duration._

class RateLimiter private(queue: Queue[Unit]) {
  def rateLimit[R, E, A](effect: => ZIO[R, E, A]): ZIO[R, E, A] =
    queue.offer(()) *> effect
}

object RateLimiter {
  def make(perSecond: Int, buffer: Int): ZIO[Clock, Nothing, RateLimiter] = {
    require(perSecond > 0 && buffer > 0)
    val period: Duration = periodFrom(perSecond)

    for {
      queue <- Queue.bounded[Unit](buffer)
      // keeps draining the queue at the given rate
      _ <- queue.take.repeat(Schedule.fixed(period)).fork
    } yield {
      new RateLimiter(queue)
    }
  }

  private def periodFrom(perSecond: Int): Duration =
    Duration.fromNanos(1.second.toNanos / perSecond)
}
