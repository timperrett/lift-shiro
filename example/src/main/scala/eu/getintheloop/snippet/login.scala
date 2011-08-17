package eu.getintheloop.snippet

import shiro.snippet.DefaultUsernamePasswordLogin

class Login extends DefaultUsernamePasswordLogin {
  def dispatch = {
    case _ => render
  }
}
