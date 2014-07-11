package services

import libraries.Di._
import libraries.Di.Reader

import models.Recipe
import reactivemongo.api.DefaultDB

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.bson._
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


  /**
   * Try to parse recipe id as a BSONObjectID and add pictures to this recipe
   */
  def addPicturesToRecipe(id: String, pictures: List[BSONValue]): Reader[DefaultDB, Future[LastError]] =
    for {
      tryId <- pure(BSONObjectID.parse(id))
      result <- tryId map {oid => Recipe.update(oid, BSONDocument(
        "$push" -> BSONDocument("pictures" -> BSONDocument("$each" -> BSONArray(pictures)))
       ))}
    } yield result

  /**
   * Try to parse recipe id as a BSONObjectID and read recipe
   */
  def readFromId(id: String): Reader[DefaultDB, Future[Option[Recipe]]] =
    for {
      tryId <- pure(BSONObjectID.parse(id))
      result <- tryId map {oid => Recipe.read(oid)}
    } yield result


}

