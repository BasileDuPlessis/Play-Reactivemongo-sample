import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.GenericQueryBuilder
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter, BSONDocument}

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.mockito.Mockito._
import org.mockito.Matchers

import models.Recipe
import reactivemongo.core.commands.LastError

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
 * Unit test for Recipe model
 */
class RecipeModelSpec extends Specification with Mockito {

  "Recipe#create" should {
    "insert a new recipe in DB" in {

      val mockDefaultDB = mock[DefaultDB]
      val mockCollection = mock[BSONCollection]

      when(
        mockDefaultDB[BSONCollection](anyString, any)(any)
      ) thenReturn mockCollection

      when(
        mockCollection.insert[Recipe](Matchers.eq(Recipe(None, "my super cake")), any)(any, any)
      ) thenReturn Future(new LastError(true, None, None, None, None, 0, false))

      Await.result(
        Recipe.create("my super cake")(mockDefaultDB), Duration.Inf
      ).ok must beTrue

    }
  }

  "Recipe#readOne" should {
    "read one recipe from DB" in {

      val mockDefaultDB = mock[DefaultDB]
      val mockCollection = mock[BSONCollection]

      when(
        mockDefaultDB[BSONCollection](anyString, any)(any)
      ) thenReturn mockCollection

      val genericQueryBuilder = mock[GenericQueryBuilder[BSONDocument, BSONDocumentReader, BSONDocumentWriter]]

      when(
        mockCollection.find(any)(any)
      ) thenReturn genericQueryBuilder

      when(
        genericQueryBuilder.one[Recipe](any, any)
      ) thenReturn Future(Some(Recipe(None, "my super cake")))

      Await.result(
        Recipe.readOne("my super cake")(mockDefaultDB), Duration.Inf
      ) must beSome[Recipe]

    }
  }

}