package com.romibuzi

import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.unsafeRun.runBlocking
import arrow.unsafe

object HelloArrow {
    private fun sayHello(): Unit =
            println("Hello World")

    private fun sayGoodBye(): Unit =
            println("Good bye World!")

    @JvmStatic
    fun main(args: Array<String>) {
        val program = IO.fx {
            effect { sayHello() }.bind()
            effect { sayGoodBye() }.bind()
        }

        unsafe { runBlocking { program } }
    }
}
