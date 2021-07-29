package org.knoldus.config

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.knoldus.dao.DAO
import org.knoldus.db.MysqlDB
import slick.jdbc.MySQLProfile
import slick.util.AsyncExecutor
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.ExecutionContext

class DatabaseConnection(implicit ec:ExecutionContext) extends ConfigData {

  private def createConnection: MySQLProfile.backend.Database ={
    val maxConnections = dbMaxConnection
    val hikariConfig = new HikariConfig()
    hikariConfig.setJdbcUrl(dbUrl)
    hikariConfig.setUsername(dbUser)
    hikariConfig.setPassword(dbPassword)
    hikariConfig.setDriverClassName(dbDriver)
    hikariConfig.setMaximumPoolSize(maxConnections)
    hikariConfig.setConnectionTimeout(5000)

    val dataSource = new HikariDataSource(hikariConfig)
    val numberOfThreads = maxConnections

    Database.forDataSource(
      dataSource,
      Some(maxConnections),
      AsyncExecutor.apply("slick-async-executor", numberOfThreads, numberOfThreads, 1000, maxConnections)
    )

  }

  private val db = createConnection
  val userDb:DAO = new MysqlDB(db)

}