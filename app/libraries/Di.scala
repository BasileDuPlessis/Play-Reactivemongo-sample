package libraries

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.util.Try

/**
 *
 * http://blog.fakod.eu/2012/06/14/scalas-built-in-dependency-injection/
 */
object Di {

  case class Reader[-C, +A](g: C => A) {
    def apply(c: C) = g(c)
    def map[B](f: A => B): Reader[C, B] = Reader(c => f(g(c)))
    def flatMap[D <: C, B](f: A => Reader[D, B]): Reader[D, B] = Reader(c => f(g(c))(c))
  }

  implicit def reader[A, B](f: A => B) = Reader(f)

  def pure[C, A](a: A) = Reader[C, A](c => a)

  /**
   * Convert a Future[A => Future[B]] to a Reader[A, Future[B]]
   */
  implicit def FutureReaderToReaderFuture[A, B](future: Future[A => Future[B]]): Reader[A, Future[B]] =
    (conn: A) => future.flatMap(f => f(conn))

  /**
   * Convert a Try[A => Future[B]] to a Reader[A, Future[B]]
   */
  implicit def TryReaderFutureToReaderFuture[A, B](tryFuture: Try[A => Future[B]]): Reader[A, Future[B]] =
    Future(tryFuture.get)


}
