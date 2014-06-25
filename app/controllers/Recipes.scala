package controllers

import play.api._
import play.api.mvc._


import libraries.MongoConnection._

import services.RecipeService

import play.api.libs.concurrent.Execution.Implicits.defaultContext


object Recipes extends Controller {

  def create(name: String) = Action.async {

    withMongoConnection {
      RecipeService.createRecipeIfNotExists(name)
    } map {
        l => Created(s"Recipe $name succesfully created with last error: $l")
    } recover {
      case e => Unauthorized(e.getMessage)
    }

  }

}