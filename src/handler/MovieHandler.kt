package com.movie_drawer.handler

import io.ktor.routing.*
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.response.respondFile
import java.io.File

fun Route.movieHandler(path: String) {
    route(path) {
        get("/{name}") {
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
            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    name = part.originalFileName ?: "user.mp4"
                    val file = File("upload/$name")
                    part.streamProvider().use { its ->
                        file.outputStream().buffered().use {
                            its.copyTo(it)
                        }
                    }
                }
                part.dispose()
            }
            call.respond(HttpStatusCode.OK, mapOf("path" to name))
        }
    }
}
