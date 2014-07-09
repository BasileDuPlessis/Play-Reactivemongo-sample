package controllers

import play.api._
import play.api.mvc._

import utils.MongoConnection._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.api.gridfs.GridFS
import reactivemongo.api.gridfs.Implicits._
import models.Recipe
import reactivemongo.api.collections.default.BSONCollection
import play.modules.reactivemongo.MongoController



object Application extends Controller with MongoController {

  def collection: BSONCollection = db.collection[BSONCollection]("recipes")
  def gridFS = new GridFS(db)

  def index = Action.async {

    withMongoConnection {
      Recipe.readAll
    } map {
      listRecipes => Ok(views.html.index(listRecipes))
    } recover {
      case e => BadRequest(e.getMessage())
    }

  }

  /**
   * Handle file upload
   */
  def upload = Action.async(gridFSBodyParser(gridFS)) { request =>
    request.body.files.head.ref.map {
      file => Ok(file.id.toString)
    } recover {
      case e =>
        BadRequest(e.getMessage())
    }
  }

}