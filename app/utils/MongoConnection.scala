package utils

import libraries.Di.Reader
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.api.{DefaultDB, MongoDriver}

/**
 * Provide Connection layer to execute Reader
 */
object MongoConnection  {

  def connection: DefaultDB = ReactiveMongoPlugin.db

  def testConnection: DefaultDB = {
    val driver = new MongoDriver
    val connection = driver.connection("localhost:12345" :: Nil)
    connection("testDB")
  }

  def withMongoConnection[To](f: Reader[DefaultDB, To]): To = f(connection)

}
