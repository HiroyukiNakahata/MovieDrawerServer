package com.movie_drawer.model

data class History(
  val movie_id: Int,
  val movie_file_name: String,
  val movie_upload_time: String,
  val paints: MutableList<HistoryPaint>
)

data class HistoryPaint(
  val layer_id: Int,
  val picture: String,
  val in_frame: Double,
  val out_frame: Double
)

interface IHistoryService {
  fun findAllHistory(): List<History>
  fun findPartialHistory(limit: Int, offset: Int): List<History>
  fun findHistoryByMovieId(movieId: Int): History
  fun countHistory(): Int
}
