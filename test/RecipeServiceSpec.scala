import com.github.athieriot._
import models.Recipe

import org.specs2.mutable.Specification

import services.RecipeService
import utils.MongoConnection.testConnection

import scala.concurrent.Await
import scala.concurrent.duration.Duration


/**
 * Integration test for RecipeService
 */
class RecipeServiceSpec extends Specification with EmbedConnection {
  sequential

  val connection = testConnection
  val recipe = Recipe(None, "my super cake")

  "The createRecipeIfNotExists Service" should {
    "create a new recipe" in {
      Await.result(
        RecipeService.createRecipeIfNotExists(recipe)(connection), Duration.Inf
      ).ok must beTrue
    }
  }

  "The createRecipeIfNotExists Service" should {
    "not create a recipe if one exists with the same name" in {
      Await.result(
        RecipeService.createRecipeIfNotExists(recipe)(connection), Duration.Inf
      ) must throwAn[Exception]
    }
  }



}
