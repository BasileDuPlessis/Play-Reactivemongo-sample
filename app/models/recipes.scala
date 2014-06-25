package models

import reactivemongo.api._
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{BSONDocumentWriter, BSONDocument, BSONDocumentReader, BSONObjectID}

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.core.commands.LastError

import scala.concurrent.Future


/**
 * Model Recipe
 */

case class Recipe(
  id: Option[BSONObjectID],
  name: String
)

object Recipe {

  private val collectionName = "recipes"

  implicit object RecipeBSONReader extends BSONDocumentReader[Recipe] {
    def read(doc: BSONDocument): Recipe =
      Recipe(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[String]("name").get
      )
  }

  implicit object RecipeBSONWriter extends BSONDocumentWriter[Recipe] {
    def write(recipe: Recipe): BSONDocument =
      BSONDocument(
        "_id" -> recipe.id.getOrElse(BSONObjectID.generate),
        "name" -> recipe.name
      )
  }

  /**
   * Create a recipe from a name
   */
  def create(name: String): DefaultDB => Future[LastError] = {
    db:DefaultDB => db[BSONCollection](collectionName).insert[Recipe](Recipe(None, name))
  }

  /**
   * Read a recipe from name
   */
  def readOne(name: String): DefaultDB => Future[Option[Recipe]]= {
    db:DefaultDB => db[BSONCollection](collectionName).find(BSONDocument("name" -> name)).one[Recipe]
  }


}
