package shiro

object Utils extends Utils
private[shiro] trait Utils {
  import org.apache.shiro.SecurityUtils
  import org.apache.shiro.subject.Subject
  import net.liftweb.common.Box
  
  implicit def subject = SecurityUtils.getSubject
  
  private def test(f: Subject => Boolean)(implicit subject: Subject): Boolean =
    f(subject)
  
  def principal[T]: Box[T] = 
    Box !! subject.getPrincipal.asInstanceOf[T]
  
  def isAuthenticated = 
    test { _.isAuthenticated }
  
  def hasRole(role: String) = 
    test { _.hasRole(role) }
  
  def lacksRole(role: String) = 
    !hasRole(role)
  
  def hasPermission(permission: String) = 
    test { _.isPermitted(permission) }
  
  def lacksPermission(permission: String) = 
    !hasPermission(permission)
  
  def hasAnyRoles(roles: Seq[String]) = test { subject =>
    roles.map(r => subject.hasRole(r.trim)
      ).contains(true)
  }
}


import net.liftweb._, 
  common.{Box,Failure,Full,Empty}, 
  util.Helpers.tryo, http.S
import org.apache.shiro.authc.{
  AuthenticationToken, IncorrectCredentialsException, UnknownAccountException, 
  LockedAccountException, ExcessiveAttemptsException}

trait SubjectLifeCycle {
  import Utils._
  
  protected def logout() = subject.logout
  
  protected def login[T <: AuthenticationToken](token: T){
    def redirect = S.redirectTo(LoginRedirect.is.openOr("/"))
    if(!isAuthenticated){
      
      println("~~~~~~")
      println(isAuthenticated)
      
      tryo(subject.login(token)) match {
        case Failure(_,Full(err),_) => err match {
          case x: UnknownAccountException => 
            S.error("Unkown user account")
          case x: IncorrectCredentialsException => 
            S.error("Invalid username or password")
          case x: LockedAccountException => 
            S.error("Your account has been locked")
          case x: ExcessiveAttemptsException => 
            S.error("You have exceeded the number of login attempts")
          case _ => 
            S.error("Unexpected login error")
        }
        case _ => redirect
      }
    } else redirect
  }
}