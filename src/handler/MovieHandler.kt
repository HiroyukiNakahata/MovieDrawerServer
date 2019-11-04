package com.movie_drawer.handler

import com.movie_drawer.injector.Injector
import com.movie_drawer.model.IMovieService
import com.movie_drawer.model.Movie
import io.ktor.routing.*
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.response.respondFile
import io.ktor.features.*
import java.io.File

fun Route.movieHandler(path: String) {
  install(PartialContent)
  val movieService: IMovieService = Injector.getMovieService()

  route(path) {
    get {
      val movies = movieService.findAllMovie()
      call.respond(movies)
    }

    get("/file/{name}") {
      val fileName = call.parameters["name"]
      val file = File("upload/$fileName")
      if (file.exists()) {
        call.respondFile(file)
      } else {
        call.respond(HttpStatusCode.NotFound)
      }
    }

    post {
      val multipart = call.receiveMultipart()
      var name = ""
      var id = 0
      multipart.forEachPart { part ->
        if (part is PartData.FileItem) {
          name = part.originalFileName ?: "user.mp4"
          val file = File("upload/$name")
          part.streamProvider().use { its ->
            file.outputStream().buffered().use {
              its.copyTo(it)
            }
          }
          id = movieService.insertMovie(name)
        }
        part.dispose()
      }
      call.respond(HttpStatusCode.OK, Movie(movie_id = id, file_name = name, upload_time = ""))
    }
  }
}
