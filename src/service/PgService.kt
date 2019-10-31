package com.movie_drawer.service

import com.zaxxer.hikari.*
import java.sql.Connection

object PgService {
    private val config = HikariConfig()
    private val ds: HikariDataSource

    init {
        config.jdbcUrl = "jdbc:postgresql://localhost/postgres"
        config.username = "postgres"
        config.password = "1203"
        ds = HikariDataSource(config)
    }

    fun getConnection(): Connection {
        return ds.connection
    }
}
