package controllers

import play.api._
import play.api.mvc._
import reactivemongo.bson.{BSONValue, BSONObjectID, BSONDocument}

import utils.MongoConnection._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.api.gridfs.{ReadFile, GridFS}
import reactivemongo.api.gridfs.Implicits._

import models.Recipe
import play.modules.reactivemongo.MongoController
import scala.concurrent.Future

import services.RecipeService

import scala.util.{Failure, Success}


object Application extends Controller with MongoController {

  def gridFS = new GridFS(db)

  def index = Action.async {

    withMongoConnection {
      Recipe.readAll
    } map {
      listRecipes => Ok(views.html.index(listRecipes))
    } recover {
      case e => BadRequest(e.getMessage)
    }

  }

  /**
   * Serve GRIDFS files
   */
  def serveFile(id: String) = Action.async {

    val futureResult = BSONObjectID.parse(id) match {
      case Success(oid) => serve(gridFS, gridFS.find(BSONDocument("_id" -> oid)), CONTENT_DISPOSITION_INLINE)
      case Failure(e) => Future.failed(e)
    }

    futureResult.recover{
      case e => InternalServerError(e.getMessage)
    }
  }

  /**
   * Handle multiple file upload
   */
  def upload(id: String) = Action.async(gridFSBodyParser(gridFS)) { request =>

    Future.sequence(request.body.files.map(_.ref)) flatMap {
      seq => {
        withMongoConnection {
          RecipeService.addPicturesToRecipe(id, seq.map(_.id).toList)
        } map {
          lastError => Redirect(routes.Recipes.view(id))
        } recover {
          case e => {
            seq.map(gridFS.remove(_))  //remove uploaded files
            BadRequest(e.getMessage)
          }
        }
      }
    }

  }


}