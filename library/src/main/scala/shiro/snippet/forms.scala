package shiro.snippet

import net.liftweb._, util.Helpers._, http.{DispatchSnippet,SHtml}
import org.apache.shiro.authc.UsernamePasswordToken

trait DefaultUsernamePasswordLogin extends DispatchSnippet with shiro.SubjectLifeCycle {
  def render = {
    var username = ""
    var password = ""
    "type=text" #> SHtml.text(username, username = _) &
    "type=password" #> SHtml.password(password, password = _) &
    "type=submit" #> SHtml.submit("Login", () => 
      login(new UsernamePasswordToken(username, password)))
  }
}
