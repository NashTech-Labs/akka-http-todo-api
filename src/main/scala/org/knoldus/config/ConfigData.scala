package org.knoldus.config

import com.typesafe.config.ConfigFactory

trait ConfigData {
  private val config = ConfigFactory.load()

  private val databaseConfig = config.getConfig("database")

  val dbDriver: String = databaseConfig.getString("driver")
  val dbUrl: String = databaseConfig.getString("url")
  val dbSchema: String = databaseConfig.getString("schema-name")
  val dbMaxConnection: Int = databaseConfig.getInt("max-connections")
  val dbUser: String = databaseConfig.getString("user")
  val dbPassword: String = databaseConfig.getString("password")
}
