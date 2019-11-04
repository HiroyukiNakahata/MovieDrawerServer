package com.movie_drawer

import com.movie_drawer.handler.historyHandler
import com.movie_drawer.handler.movieHandler
import com.movie_drawer.handler.paintHandler
import com.movie_drawer.handler.rootHandler
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.routing.routing

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
  install(ContentNegotiation) {
    gson {
      setPrettyPrinting()
    }
  }

  routing {
    rootHandler("/")
    movieHandler("/api/movie")
    paintHandler("/api/paint")
    historyHandler("/api/history")
  }
}
