package controllers

import play.api._
import play.api.cache._
import play.api.libs.iteratee.Enumerator
import play.api.mvc._
import reactivemongo.bson.{BSONObjectID, BSONDocument}

import utils.MongoConnection._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.api.gridfs.{DefaultFileToSave, GridFS}
import reactivemongo.api.gridfs.Implicits._

import models.Recipe
import play.modules.reactivemongo.MongoController
import scala.concurrent.Future


import services.RecipeService

import scala.util.{Failure, Success}


object Application extends Controller with MongoController {
	
  val gridFS = new GridFS(db)
  	
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
      case Success(oid) => {
        serve(gridFS, gridFS.find(BSONDocument("_id" -> oid)), CONTENT_DISPOSITION_INLINE).map(
          _.withHeaders(
			      CACHE_CONTROL -> "public, max-age=%d".format(60 * 60 * 24 * 365)
		      )
        )
      }
      case Failure(e) => Future.failed(e)
    }

    futureResult.recover { case e => InternalServerError(e.getMessage)}
  }

  /**
   * Handle multiple file upload
   */
  def upload(id: String) = Action.async(parse.multipartFormData) { request =>

    import com.sksamuel.scrimage._
    import com.sksamuel.scrimage.io.{JpegWriter, AsyncImageWriter}

    val futureSave = request.body.files map {
      f => {
        for {
          image <- AsyncImage(f.ref.file)
          scaled <- image.scaleToWidth(2048)
          streamed <- new AsyncImageWriter(new JpegWriter(scaled.toImage, 30, true)).toStream
          save <- gridFS.save(Enumerator.fromStream(streamed), DefaultFileToSave(f.filename, f.contentType))
        } yield save
      }
    }

    Future.sequence(futureSave) flatMap { seq =>
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
    } recover {
      case e => {
        InternalServerError(e.getMessage)
      }
    }

  }


}