package utils

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}


/**
 * Handle date format
 */
object DateHelper {

  val lastModifiedFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss")

  def currentDate: Date = Calendar.getInstance().getTime()

}
