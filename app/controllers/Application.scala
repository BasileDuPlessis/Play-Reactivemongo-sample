package controllers

import play.api._
import play.api.mvc._


import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.bson.BSONDocument
import models.Recipe
import models.Recipe._
import reactivemongo.api.collections.default.BSONCollection
import play.modules.reactivemongo.MongoController



object Application extends Controller with MongoController {

  def collection: BSONCollection = db.collection[BSONCollection]("recipes")

  def index = Action.async {

    val query = BSONDocument()

    collection.find(query).cursor[Recipe].collect[List]().map( l => {
      Ok(
        views.html.index(
          title = "Mes recettes",
          recipes = l
        )
      )
      }
    ).recover {
      case e =>
        BadRequest(e.getMessage())
    }

  }

}