package models

import _root_.utils.CustomMappings._

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats._
import reactivemongo.api._
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.core.commands.LastError

import scala.concurrent.Future




/**
 * Model Recipe
 */

case class Recipe(
  id: Option[BSONObjectID],
  name: String,
  pictures: List[BSONObjectID] = List[BSONObjectID]()
)

object Recipe {

  private val collectionName = "recipes"

  implicit object RecipeBSONReader extends BSONDocumentReader[Recipe] {
    def read(doc: BSONDocument): Recipe =
      Recipe(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[String]("name").get,
        doc.getAs[List[BSONObjectID]]("pictures").get
      )
  }

  implicit object RecipeBSONWriter extends BSONDocumentWriter[Recipe] {
    def write(recipe: Recipe): BSONDocument =
      BSONDocument(
        "_id" -> recipe.id.getOrElse(BSONObjectID.generate),
        "name" -> recipe.name,
        "pictures" -> recipe.pictures
      )
  }

  val recipeForm = Form(
    mapping(
      "id" -> optional(of[BSONObjectID]),
      "name" -> text,
      "pictures" -> ignored(List[BSONObjectID]())
    )(Recipe.apply)(Recipe.unapply)
  )

  /**
   * Insert a recipe in DB
   */
  def insert(recipe: Recipe): DefaultDB => Future[LastError] = {
    db:DefaultDB => {
      db[BSONCollection](collectionName).insert[Recipe](recipe)
    }
  }

  /**
   * Read a recipe from name
   */
  def read(name: String): DefaultDB => Future[Option[Recipe]]= {
    db:DefaultDB => db[BSONCollection](collectionName).find(BSONDocument("name" -> name)).one[Recipe]
  }

  /**
   * Read a recipe from id
   */
  def read(id: BSONObjectID): DefaultDB => Future[Option[Recipe]]= {
    db:DefaultDB => db[BSONCollection](collectionName).find(BSONDocument("_id" -> id)).one[Recipe]
  }

  /**
   * Read all recipes
   */
  def readAll: DefaultDB => Future[List[Recipe]] = {
    db:DefaultDB => db[BSONCollection](collectionName).find(BSONDocument()).cursor[Recipe].collect[List]()
  }

  /**
   * Update recipe
   */
  def update(id: BSONObjectID, modifier: BSONDocument): DefaultDB => Future[LastError]= {
    db:DefaultDB => db[BSONCollection](collectionName).update(BSONDocument("_id" -> id), modifier)
  }


}
