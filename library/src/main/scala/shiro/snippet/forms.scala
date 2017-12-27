package shiro.snippet

import net.liftweb.http.{DispatchSnippet, SHtml}
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._
import org.apache.shiro.authc.UsernamePasswordToken

trait DefaultUsernamePasswordLogin extends DispatchSnippet with shiro.SubjectLifeCycle {
  def render: CssSel = {
    var username = ""
    var password = ""
    "type=text" #> SHtml.text(username, username = _) &
    "type=password" #> SHtml.password(password, password = _) &
    "type=submit" #> SHtml.submit("Login", () => 
      login(new UsernamePasswordToken(username, password)))
  }
}
