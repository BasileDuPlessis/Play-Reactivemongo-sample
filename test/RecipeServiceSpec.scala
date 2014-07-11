import com.github.athieriot._
import models.Recipe

import org.specs2.mutable.Specification
import reactivemongo.bson.BSONObjectID

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
  val recipe = Recipe(Some(BSONObjectID.generate), "my super cake")
  val pictures = (for (i <- 0 to 10) yield BSONObjectID.generate).toList


  "RecipeService#createRecipeIfNotExists" should {
    "create a new recipe" in {
      Await.result(
        RecipeService.createRecipeIfNotExists(recipe)(connection), Duration.Inf
      ).ok must beTrue
    }
  }

  "RecipeService#createRecipeIfNotExists" should {
    "not create a recipe if one exists with the same name" in {
      Await.result(
        RecipeService.createRecipeIfNotExists(recipe)(connection), Duration.Inf
      ) must throwAn[Exception]
    }
  }

  "RecipeService.addPicturesToRecipe" should {
    "add array of pictures ID to recipe" in {
      Await.result(
        RecipeService.addPicturesToRecipe(recipe.id.get.stringify, pictures)(connection), Duration.Inf
      ).ok must beTrue

      Await.result(
        Recipe.read(recipe.id.get)(connection), Duration.Inf
      ).get.pictures must beEqualTo(pictures)

    }
  }

  "RecipeService.readFromId" should {
    "read a recipe from a string ID" in {

      Await.result(
        RecipeService.readFromId(recipe.id.get.stringify)(connection), Duration.Inf
      ).get.id must beEqualTo(recipe.id)

    }
  }

  "RecipeService.readFromId" should {
    "throw an error if ID is not valid" in {

      Await.result(
        RecipeService.readFromId(recipe.id.get.stringify + "z")(connection), Duration.Inf
      ) must throwAn[Exception]

    }
  }



}
