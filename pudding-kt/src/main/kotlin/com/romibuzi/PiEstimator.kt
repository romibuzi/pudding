package com.romibuzi

import arrow.Kind
import arrow.fx.*
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.io.monad.map
import arrow.fx.extensions.io.monad.monad
import arrow.fx.extensions.io.monadDefer.monadDefer
import arrow.fx.extensions.io.unsafeRun.runBlocking
import arrow.fx.typeclasses.seconds
import arrow.unsafe
import kotlin.math.sqrt
import kotlin.random.Random.Default.nextDouble

object PiEstimator {
    data class PiState(val inside: Long, val total: Long)

    private fun estimatePi(state: PiState): Double =
            (state.inside.toDouble() / state.total.toDouble()) * 4.0

    private fun insideCircle(x: Double, y: Double): Boolean =
            sqrt(x * x + y * y) <= 1.0

    private fun updatePiOnce(ref: Ref<ForIO, PiState>): Kind<ForIO, Unit> = run {
        ref.update {
            val point = Pair(nextDouble(), nextDouble())
            val inside = if (insideCircle(point.first, point.second)) 1 else 0

            it.copy(inside = it.inside + inside, total = it.total + 1)
        }
    }

    private fun displayEstimate(ref: Ref<ForIO, PiState>): IO<Unit> = run {
        ref.get().map {
            println("Current Pi estimate is: ${estimatePi(it)}")
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        /**
         * Continuously estimate PI using a dedicated fiber and a concurrent atomic ref
         * and display the ongoing estimation each second using another fiber
         */
        val program = IO.fx {
            val ref = Ref(IO.monadDefer(), PiState(0, 0)).bind()

            val fiberCompute = updatePiOnce(ref)
                    .repeat(IO.concurrent(), Schedule.forever(IO.monad()))
                    .fork()
                    .bind()

            val fiberDisplay = displayEstimate(ref)
                    .repeat(IO.concurrent(), Schedule.spaced(IO.monad(), 1.seconds))
                    .fork()
                    .bind()

            fiberDisplay.join().bind()
        }

        unsafe { runBlocking { program } }
    }
}
