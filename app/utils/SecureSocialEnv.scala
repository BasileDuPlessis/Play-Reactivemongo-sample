package utils

import controllers.CustomRoutesService
import securesocial.core.providers.TwitterProvider
import securesocial.core.{RuntimeEnvironment, SecureSocial}
import services.{DemoUser, InMemoryUserService}
import securesocial.core.services._
import scala.collection.immutable.ListMap

/**
 * Custom environment for SecureSocial
 */
object CustomRuntimeEnvironment extends RuntimeEnvironment.Default[DemoUser] {
  override lazy val userService = new InMemoryUserService()
  override lazy val routes = new CustomRoutesService()
  override lazy val cacheService: CacheService = new CacheService.Default()

  override lazy val providers = ListMap(
    include(new TwitterProvider(routes, cacheService, oauth1ClientFor(TwitterProvider.Twitter)))
  )
}

/**
 * Provide Custom environment to controllers
 */
trait SecureSocialEnv {
  this: SecureSocial[DemoUser] =>
    override implicit val env = CustomRuntimeEnvironment
}