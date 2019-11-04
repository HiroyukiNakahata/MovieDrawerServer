package com.movie_drawer.injector

import com.movie_drawer.model.IHistoryService
import com.movie_drawer.model.IMovieService
import com.movie_drawer.model.IPaintService
import com.movie_drawer.service.HistoryService
import com.movie_drawer.service.MovieService
import com.movie_drawer.service.PaintService

object Injector {
  fun getMovieService(): IMovieService {
    return MovieService()
  }

  fun getPaintService(): IPaintService {
    return PaintService()
  }

  fun getHistoryService(): IHistoryService {
    return HistoryService()
  }
}
