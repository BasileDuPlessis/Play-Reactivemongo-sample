import reactivemongo.api.{Cursor, DefaultDB}
import reactivemongo.api.collections.GenericQueryBuilder
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{BSONObjectID, BSONDocumentReader, BSONDocumentWriter, BSONDocument}

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

  "Recipe#insert" should {
    "call insert on collection" in {

      val mockDefaultDB = mock[DefaultDB]
      val mockCollection = mock[BSONCollection]
      val recipe = Recipe(None, "my super cake")

      when(
        mockDefaultDB[BSONCollection](anyString, any)(any)
      ) thenReturn mockCollection

      when(
        mockCollection.insert[Recipe](Matchers.eq(recipe), any)(any, any)
      ) thenReturn Future(new LastError(true, None, None, None, None, 0, false))

      Await.result(
        Recipe.insert(recipe)(mockDefaultDB), Duration.Inf
      )

      there was one(mockCollection).insert[Recipe](recipe)

    }
  }

  "Recipe#readAll" should {
    "call find with empty parameters on collection" in {

      val mockDefaultDB = mock[DefaultDB]
      val mockCollection = mock[BSONCollection]
      val mockCursor = mock[Cursor[Recipe]]
      val genericQueryBuilder = mock[GenericQueryBuilder[BSONDocument, BSONDocumentReader, BSONDocumentWriter]]
      val recipe = Recipe(None, "my super cake")

      when(
        mockDefaultDB[BSONCollection](anyString, any)(any)
      ) thenReturn mockCollection

      when(
        mockCollection.find(Matchers.eq(BSONDocument()))(any)
      ) thenReturn genericQueryBuilder

      when(
        genericQueryBuilder.cursor[Recipe](any, any)
      ) thenReturn mockCursor

      when(
        mockCursor.collect[List](any, any)(any, any)
      ) thenReturn Future(List(recipe))

      Await.result(
        Recipe.readAll(mockDefaultDB), Duration.Inf
      )

      there was one(mockCollection).find(BSONDocument())
      there was one(mockCursor).collect[List]()

    }
  }

  "Recipe#readOne(String)" should {
    "call find with name parameter on collection" in {
      val mockDefaultDB = mock[DefaultDB]
      val mockCollection = mock[BSONCollection]
      val recipe = Recipe(None, "my super cake")

      when(
        mockDefaultDB[BSONCollection](anyString, any)(any)
      ) thenReturn mockCollection

      val genericQueryBuilder = mock[GenericQueryBuilder[BSONDocument, BSONDocumentReader, BSONDocumentWriter]]

      when(
        mockCollection.find(Matchers.eq(BSONDocument("name" -> recipe.name)))(any)
      ) thenReturn genericQueryBuilder

      when(
        genericQueryBuilder.one[Recipe](any, any)
      ) thenReturn Future(Some(recipe))

      Await.result(
        Recipe.read(recipe.name)(mockDefaultDB), Duration.Inf
      )

      there was one(mockCollection).find(BSONDocument("name" -> recipe.name))
    }
  }

  "Recipe#readOne(BSONObjectID)" should {
    "call find with id parameter on collection" in {
      val mockDefaultDB = mock[DefaultDB]
      val mockCollection = mock[BSONCollection]
      val id = BSONObjectID.generate
      val recipe = Recipe(Some(id), "my super cake")

      when(
        mockDefaultDB[BSONCollection](anyString, any)(any)
      ) thenReturn mockCollection

      val genericQueryBuilder = mock[GenericQueryBuilder[BSONDocument, BSONDocumentReader, BSONDocumentWriter]]

      when(
        mockCollection.find(Matchers.eq(BSONDocument("_id" -> id)))(any)
      ) thenReturn genericQueryBuilder

      when(
        genericQueryBuilder.one[Recipe](any, any)
      ) thenReturn Future(Some(recipe))

      Await.result(
        Recipe.read(id)(mockDefaultDB), Duration.Inf
      )

      there was one(mockCollection).find(BSONDocument("_id" -> id))
    }
  }

}
