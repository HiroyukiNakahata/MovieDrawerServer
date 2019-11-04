package com.movie_drawer.handler

import com.movie_drawer.injector.Injector
import com.movie_drawer.model.IHistoryService
import io.ktor.routing.*
import io.ktor.application.call
import io.ktor.response.respond

fun Route.historyHandler(path: String) {
  val historyService: IHistoryService = Injector.getHistoryService()

  route(path) {
    get {
      val histories = historyService.findAllHistory()
      call.respond(histories)
    }

    get("/{movie_id}") {
      val movieId = call.parameters["movie_id"]?.toInt() ?: 1
      val history = historyService.findHistoryByMovieId(movieId)
      call.respond(history)
    }

    get("/partial") {
      val page = call.request.queryParameters["page"]?.toInt() ?: 1
      val limit = call.request.queryParameters["limit"]?.toInt() ?: 4
      val offset = (page - 1) * limit
      val histories = historyService.findPartialHistory(limit, offset)
      call.respond(histories)
    }

    get("/count") {
      val count = historyService.countHistory()
      call.respond(mapOf("count" to count))
    }
  }
}
