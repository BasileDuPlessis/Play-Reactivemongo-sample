package com.github.athieriot

import controllers.Application._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.mutable.Specification
import reactivemongo.api._
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.BSONDocument
import scala.concurrent.ExecutionContext.Implicits.global

@RunWith(classOf[JUnitRunner])
class EmbedConnectionSpec extends Specification with EmbedConnection {

  "Mongo" should {
    "should be ok" in {

      val driver = new MongoDriver
      val connection = driver.connection("localhost:12345" :: Nil)
      val db = connection("testItsmyfood")
      val collection = db.collection[BSONCollection]("recipes")

      println(collection)
/*
      driver.close()
      connection.close()
*/
      true must beTrue

    }
  }

}
