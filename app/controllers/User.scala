package controllers

import securesocial.controllers.BaseLoginPage
import play.api.mvc.{AnyContent, Action}
import play.api.Logger
import services.DemoUser
import utils.SecureSocialEnv


object User extends BaseLoginPage[DemoUser] with SecureSocialEnv {

  override def login: Action[AnyContent] = {
    Logger.debug("using CustomLoginController")
    super.login
  }
}
