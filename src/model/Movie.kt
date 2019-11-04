package com.movie_drawer.model

data class Movie(
  val movie_id: Int,
  val file_name: String,
  val upload_time: String
)

interface IMovieService {
  fun findAllMovie(): List<Movie>
  fun insertMovie(name: String): Int
}
