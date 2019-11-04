package com.movie_drawer.service

import com.movie_drawer.model.IPaintService
import com.movie_drawer.model.Paint
import java.sql.ResultSet
import java.sql.SQLException

class PaintService : IPaintService {
  override fun findAllPaint(): List<Paint> {
    PgService.getConnection().use { con ->
      con.prepareStatement(
        """
          SELECT paint_id, movie_id, layer_id, picture, in_frame, out_frame, upload_time
          FROM paint
          ORDER BY paint_id
        """
      ).use { ps ->
        ps.executeQuery().use { rows ->
          return mappingPaintList(rows)
        }
      }
    }
  }

  override fun findPaint(movieId: Int): List<Paint> {
    PgService.getConnection().use { con ->
      con.prepareStatement(
        """
          SELECT paint_id, movie_id, layer_id, picture, in_frame, out_frame, upload_time
          FROM paint
          WHERE movie_id = ?
          ORDER BY paint_id
        """
      ).use { ps ->
        ps.setInt(1, movieId)
        ps.executeQuery().use { rows ->
          return mappingPaintList(rows)
        }
      }
    }
  }

  private fun mappingPaintList(rows: ResultSet): MutableList<Paint> {
    val paints = mutableListOf<Paint>()
    while (rows.next()) {
      val p = Paint(
        paint_id = rows.getInt(1),
        movie_id = rows.getInt(2),
        layer_id = rows.getInt(3),
        picture = rows.getString(4),
        in_frame = rows.getDouble(5),
        out_frame = rows.getDouble(6),
        upload_time = rows.getString(7)
      )
      paints.add(p)
    }
    return paints
  }

  override fun insertPaint(paint: Paint) {
    PgService.getConnection().use { con ->
      con.prepareStatement(
        """
          INSERT INTO paint(movie_id, layer_id, picture, in_frame, out_frame, upload_time)
          VALUES (?, ?, ?, ?, ?, current_timestamp)
          ON CONFLICT ON CONSTRAINT paint_pk
          DO UPDATE SET picture = ?, in_frame = ?, out_frame = ?, upload_time = current_timestamp
        """
      ).use { ps ->
        ps.setInt(1, paint.movie_id)
        ps.setInt(2, paint.layer_id)
        ps.setString(3, paint.picture)
        ps.setDouble(4, paint.in_frame)
        ps.setDouble(5, paint.out_frame)
        ps.setString(6, paint.picture)
        ps.setDouble(7, paint.in_frame)
        ps.setDouble(8, paint.out_frame)
        ps.execute()
      }
    }
  }

  override fun insertPaints(paints: Array<Paint>) {
    PgService.getConnection().use { con ->
      try {
        con.autoCommit = false
        con.prepareStatement(
          """
            INSERT INTO paint(movie_id, layer_id, picture, in_frame, out_frame, upload_time)
            VALUES (?, ?, ?, ?, ?, current_timestamp)
            ON CONFLICT ON CONSTRAINT paint_pk
            DO UPDATE SET picture = ?, in_frame = ?, out_frame = ?, upload_time = current_timestamp
          """
        ).use { ps ->
          paints.forEach { paint ->
            ps.setInt(1, paint.movie_id)
            ps.setInt(2, paint.layer_id)
            ps.setString(3, paint.picture)
            ps.setDouble(4, paint.in_frame)
            ps.setDouble(5, paint.out_frame)
            ps.setString(6, paint.picture)
            ps.setDouble(7, paint.in_frame)
            ps.setDouble(8, paint.out_frame)
            ps.addBatch()
          }
          ps.executeBatch()
          con.commit()
          println("commit")
        }
      } catch (e: SQLException) {
        con.rollback()
        println("rollback")
        throw e
      }
    }
  }
}
