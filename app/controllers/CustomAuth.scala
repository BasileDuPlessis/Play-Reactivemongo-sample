package controllers

import securesocial.controllers.{BaseProviderController, BaseLoginPage}
import services.DemoUser
import utils.SecureSocialEnv

import play.api.mvc.{RequestHeader, AnyContent, Action}
import play.api.Logger
import securesocial.core.IdentityProvider
import securesocial.core.services.RoutesService

object CustomLoginController extends BaseLoginPage[DemoUser] with SecureSocialEnv {

  override def login: Action[AnyContent] = {
    Logger.debug("using CustomLoginController")
    super.login
  }

}

class CustomRoutesService extends RoutesService.Default {
  override def loginPageUrl(implicit req: RequestHeader): String = controllers.routes.CustomLoginController.login().absoluteURL(IdentityProvider.sslEnabled)

  override def authenticationUrl(provider: String, redirectTo: Option[String] = None)(implicit req: RequestHeader): String =
    controllers.routes.CustomProviderController.authenticate(provider).absoluteURL(IdentityProvider.sslEnabled)
}


object CustomProviderController extends BaseProviderController[DemoUser] with SecureSocialEnv
