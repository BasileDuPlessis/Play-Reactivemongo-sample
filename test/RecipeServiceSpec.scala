import com.github.athieriot._

import org.specs2.mutable.Specification
import reactivemongo.core.commands.LastError
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
      ) must beAnInstanceOf[LastError]
    }
  }

  "The createRecipeIfNotExists Service" should {
    "not create a recipe" in {
      Await.result(
        RecipeService.createRecipeIfNotExists("my super cake")(db), Duration.Inf
      ) must throwA[NoSuchElementException]("Future.filter predicate is not satisfied")
    }
  }



}
