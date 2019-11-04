package com.movie_drawer.service

import com.movie_drawer.model.History
import com.movie_drawer.model.HistoryPaint
import com.movie_drawer.model.IHistoryService
import java.sql.ResultSet

class HistoryService : IHistoryService {
  override fun findAllHistory(): List<History> {
    PgService.getConnection().use { con ->
      con.prepareStatement(
        """
          SELECT movie.movie_id,
            file_name,
            movie.upload_time,
            layer_id,
            picture,
            in_frame,
            out_frame
          FROM paint
          INNER JOIN movie ON paint.movie_id = movie.movie_id
        """
      ).use { ps ->
        ps.executeQuery().use { rows ->
          return mappingHistories(rows)
        }
      }
    }
  }

  override fun findPartialHistory(limit: Int, offset: Int): List<History> {
    PgService.getConnection().use { con ->
      con.prepareStatement(
        """
          SELECT pm.movie_id,
            pm.file_name,
            pm.upload_time,
            pm.layer_id,
            pm.picture,
            pm.in_frame,
            pm.out_frame
          FROM (SELECT movie.movie_id,
                  file_name,
                  movie.upload_time,
                  layer_id,
                  picture,
                  in_frame,
                  out_frame,
                  dense_rank() OVER (ORDER BY movie.movie_id) as rank
                FROM paint
                  INNER JOIN movie ON paint.movie_id = movie.movie_id) as pm
          WHERE pm.rank >= ?
            AND pm.rank <= ?
        """
      ).use { ps ->
        ps.setInt(1, offset + 1)
        ps.setInt(2, offset + limit)
        ps.executeQuery().use { rows ->
          return mappingHistories(rows)
        }
      }
    }
  }

  override fun findHistoryByMovieId(movieId: Int): History {
    PgService.getConnection().use { con ->
      con.prepareStatement(
        """
          SELECT movie.movie_id,
            file_name,
            movie.upload_time,
            layer_id,
            picture,
            in_frame,
            out_frame
          FROM paint
          INNER JOIN movie ON paint.movie_id = movie.movie_id
          WHERE movie.movie_id = ?
        """
      ).use { ps ->
        ps.setInt(1, movieId)
        ps.executeQuery().use { rows ->
          val paints = mutableListOf<HistoryPaint>()
          var idx = 0
          var fileName = ""
          var uploadTime = ""
          while (rows.next()) {
            if (idx == 0) {
              fileName = rows.getString(2)
              uploadTime = rows.getString(3)
            }
            paints.add(
              HistoryPaint(
                layer_id = rows.getInt(4),
                picture = rows.getString(5),
                in_frame = rows.getDouble(6),
                out_frame = rows.getDouble(7)
              )
            )
            idx++
          }
          return History(
            movie_id = movieId,
            movie_file_name = fileName,
            movie_upload_time = uploadTime,
            paints = paints
          )
        }
      }
    }
  }

  private fun mappingHistories(rows: ResultSet): MutableList<History> {
    val histories = mutableListOf<History>()
    var h: History? = null
    var idx = 0
    while (rows.next()) {
      val i = rows.getInt(1)
      if (i != idx) {
        h?.also { histories.add(it) }
        h = History(
          movie_id = i,
          movie_file_name = rows.getString(2),
          movie_upload_time = rows.getString(3),
          paints = mutableListOf()
        )
        idx = i
      }
      h?.paints?.add(
        HistoryPaint(
          layer_id = rows.getInt(4),
          picture = rows.getString(5),
          in_frame = rows.getDouble(6),
          out_frame = rows.getDouble(7)
        )
      )
    }
    h?.also { histories.add(it) }
    return histories
  }

  override fun countHistory(): Int {
    PgService.getConnection().use { con ->
      con.prepareStatement(
        """
          SELECT COUNT(*)
          FROM (
            SELECT COUNT(movie_id)
            FROM paint
            GROUP BY movie_id
          ) as p 
        """
      ).use { ps ->
        ps.executeQuery().use { row ->
          row.next()
          return row.getInt(1)
        }
      }
    }
  }
}
