package services

import libraries.Di._
import libraries.Di.Reader

import models.Recipe
import reactivemongo.api.DefaultDB

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.core.commands.LastError
import scala.concurrent.Future


/**
 * Provide application logic for recipes
 */
object RecipeService {

  /**
   * Prevent duplicate url with checking if a recipe exists and create it if don't
   */
  def createRecipeIfNotExists(recipe: Recipe): Reader[DefaultDB, Future[LastError]] =
    for {
      recipeInDB <- Recipe.read(recipe.name)
      result <- recipeInDB filter (_ == None) map (_ => Recipe.insert(recipe))
    } yield result

}
