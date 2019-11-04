package com.movie_drawer.service

import com.movie_drawer.model.IMovieService
import com.movie_drawer.model.Movie

class MovieService : IMovieService {
  override fun findAllMovie(): List<Movie> {
    PgService.getConnection().use { con ->
      con.prepareStatement(
        """
          SELECT movie_id, file_name, upload_time
          FROM movie
          ORDER BY movie_id
          ;
        """
      ).use { ps ->
        ps.executeQuery().use { rows ->
          val movies = mutableListOf<Movie>()
          while (rows.next()) {
            val m = Movie(
              movie_id = rows.getInt(1),
              file_name = rows.getString(2),
              upload_time = rows.getString(3)
            )
            movies.add(m)
          }
          return movies
        }
      }
    }
  }

  override fun insertMovie(name: String): Int {
    PgService.getConnection().use { con ->
      con.prepareStatement(
        """
          INSERT INTO movie(file_name, upload_time)
          VALUES (?, current_timestamp)
          RETURNING movie_id
          ;
        """
      ).use { ps ->
        ps.setString(1, name)
        ps.executeQuery().use { res ->
          res.next()
          return res.getInt(1)
        }
      }
    }
  }
}
