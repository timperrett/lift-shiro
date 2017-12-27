package shiro

object Utils extends Utils
private[shiro] trait Utils {
  import org.apache.shiro.SecurityUtils
  import org.apache.shiro.subject.Subject
  import net.liftweb.common.Box
  
  implicit def subject: Subject = SecurityUtils.getSubject

  private def test(f: Subject => Boolean)(implicit subject: Subject): Boolean = f(subject)
  
  def principal[T]: Box[T] = (Box !! subject.getPrincipal).map(_.asInstanceOf[T])
  
  def isAuthenticated: Boolean = test(_.isAuthenticated)
  
  def isRemembered: Boolean = test(_.isRemembered)
  
  def isAuthenticatedOrRemembered: Boolean = isAuthenticated || isRemembered
  
  def hasRole(role: String): Boolean = test(_.hasRole(role))
  
  def lacksRole(role: String): Boolean = !hasRole(role)
  
  def hasPermission(permission: String): Boolean = test(_.isPermitted(permission))
  
  def lacksPermission(permission: String): Boolean = !hasPermission(permission)
  
  def hasAnyRoles(roles: Seq[String]): Boolean = roles.exists(r => hasRole(r.trim))
    
  def hasAllRoles(roles: Seq[String]): Boolean = roles.forall(r => hasRole(r.trim))
}

import net.liftweb.common.{Failure,Full}
import net.liftweb.util.Helpers.tryo
import net.liftweb.http.S
import org.apache.shiro.authc.{
  AuthenticationToken, IncorrectCredentialsException, UnknownAccountException, 
  LockedAccountException, ExcessiveAttemptsException}

trait SubjectLifeCycle {
  import Utils._
  
  protected def logout(): Unit = subject.logout()
  
  protected def login[T <: AuthenticationToken](token: T){
    def redirect = S.redirectTo(LoginRedirect.is.openOr("/"))

    if(!isAuthenticated){

      tryo(subject.login(token)) match {
        case Failure(_,Full(err),_) => err match {
          case _: UnknownAccountException =>
            S.error("Unkown user account")
          case _: IncorrectCredentialsException =>
            S.error("Invalid username or password")
          case _: LockedAccountException =>
            S.error("Your account has been locked")
          case _: ExcessiveAttemptsException =>
            S.error("You have exceeded the number of login attempts")
          case _ => 
            S.error("Unexpected login error")
        }
        case _ => redirect
      }
    } else redirect
  }
}
