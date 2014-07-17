package utils

import securesocial.core.providers.TwitterProvider
import securesocial.core.{RuntimeEnvironment, SecureSocial}
import services.{DemoUser, InMemoryUserService}
import securesocial.core.services._
import scala.collection.immutable.ListMap

/**
 * Created by basile.duplessis on 17/07/2014.
 */
trait SecureSocialEnv{
  this: SecureSocial[DemoUser] =>
    override implicit val env = new RuntimeEnvironment.Default[DemoUser]{
      override lazy val userService: InMemoryUserService = new InMemoryUserService()

      override lazy val routes: RoutesService = new RoutesService.Default()
      override lazy val cacheService: CacheService = new CacheService.Default()

      override lazy val providers = ListMap(
        include(new TwitterProvider(routes, cacheService, oauth1ClientFor(TwitterProvider.Twitter)))
      )
    }
}
