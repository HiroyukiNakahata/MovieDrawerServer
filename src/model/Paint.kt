package com.movie_drawer.model

data class Paint(
  val paint_id: Int,
  val movie_id: Int,
  val layer_id: Int,
  val picture: String,
  val in_frame: Double,
  val out_frame: Double,
  val upload_time: String
)

interface IPaintService {
  fun findAllPaint(): List<Paint>
  fun findPaint(movieId: Int): List<Paint>
  fun insertPaint(paint: Paint)
  fun insertPaints(paints: Array<Paint>)
}
