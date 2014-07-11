package controllers

import play.api._
import play.api.mvc._
import reactivemongo.bson.BSONObjectID
import utils.MongoConnection._

import services.RecipeService
import models.Recipe

import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future
import scala.util.{Failure, Success}


object Recipes extends Controller {

  def index = Action {
    Ok(views.html.recipes.add(Recipe.recipeForm))
  }

  def view(id: String) = Action.async {
    withMongoConnection {
      RecipeService.readFromId(id)
    } map {
      case Some(recipe: Recipe) => Ok(views.html.recipes.view(recipe))
      case _ => NotFound(s"Recipe $id not found")
    } recover {
      case e => BadRequest(e.getMessage)
    }
  }


  def create = Action.async { implicit request =>
    Recipe.recipeForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.recipes.add(formWithErrors))),
      recipe => {
        val id = BSONObjectID.generate
        withMongoConnection {
          RecipeService.createRecipeIfNotExists(recipe.copy(id = Some(id)))
        } map {
          lastError => Redirect(routes.Recipes.view(id.stringify))
        } recover {
          case e => BadRequest(e.getMessage)
        }
      }
    )
  }


}