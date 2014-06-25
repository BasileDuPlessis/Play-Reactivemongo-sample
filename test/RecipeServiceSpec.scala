import com.github.athieriot._

import org.specs2.mutable.Specification

import services.RecipeService

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * Integration test for RecipeService
 */
class RecipeServiceSpec extends Specification with EmbedConnection {
  sequential

  val db = libraries.MongoConnection.testConnection

  "The createRecipeIfNotExists Service" should {
    "create a new recipe" in {
      Await.result(
        RecipeService.createRecipeIfNotExists("my super cake")(db), Duration.Inf
      ).ok must beTrue
    }
  }

  "The createRecipeIfNotExists Service" should {
    "not create a recipe if one exists with the same name" in {
      Await.result(
        RecipeService.createRecipeIfNotExists("my super cake")(db), Duration.Inf
      ) must throwAn[Exception]
    }
  }



}
