package utils

import reactivemongo.bson.BSONObjectID

import play.api.data.format.Formatter
import play.api.data.Mapping
import play.api.data.FormError

/**
 * Custom Mapping
 */
object CustomMappings {

  implicit val BSONObjectIDFormatter = new Formatter[BSONObjectID] {

    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], BSONObjectID] =
      data.get(key).map(BSONObjectID(_)).toRight(Seq(FormError(key, "format.BSONObjectID", Nil)))

    def unbind(key: String, value: BSONObjectID): Map[String, String] = Map(key -> value.toString)

  }

}
