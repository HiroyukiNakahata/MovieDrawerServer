package com.movie_drawer.handler

import io.ktor.routing.*
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

fun Route.paintHandler(path: String) {
    route(path) {
        get {
            call.respond(HttpStatusCode.OK, "")
        }
        get("/{id}") {

        }
        post {

        }
        put {

        }
        delete("/{id}") {

        }
    }
}
