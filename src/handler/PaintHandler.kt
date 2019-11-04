package com.movie_drawer.handler

import com.movie_drawer.injector.Injector
import com.movie_drawer.model.IPaintService
import com.movie_drawer.model.Paint
import io.ktor.routing.*
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond

fun Route.paintHandler(path: String) {
  val paintService: IPaintService = Injector.getPaintService()

  route(path) {
    get {
      val paints = paintService.findAllPaint()
      call.respond(HttpStatusCode.OK, paints)
    }

    get("/{movie_id}") {
      val movieId = call.parameters["movie_id"]?.toInt() ?: 0
      val paints = paintService.findPaint(movieId)
      call.respond(HttpStatusCode.OK, paints)
    }

    post {
      val paints = call.receive<Array<Paint>>()
      paintService.insertPaints(paints)
      call.respond(paints)
    }

    put {

    }

    delete("/{id}") {

    }
  }
}
