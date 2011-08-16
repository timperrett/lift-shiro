package shiro

import org.apache.shiro.SecurityUtils
import net.liftweb.http.S

/**
 * Lift SiteMap Integration
 */
object Locs {
  import net.liftweb.common.{Full,Box}
  import net.liftweb.http.{RedirectResponse, RedirectWithState, S, RedirectState}
  import net.liftweb.sitemap.{Menu,Loc}
  import net.liftweb.sitemap.Loc.{If,EarlyResponse,Unless,Hidden,Link}
  import org.apache.shiro.SecurityUtils
  
  implicit def listToPath(in: List[String]): String = in.mkString("/","/","")
  
  private val loginURL = Shiro.baseURL.vend ::: Shiro.loginURL.vend
  private val indexURL = Shiro.baseURL.vend ::: Shiro.indexURL.vend
  private val logoutURL = Shiro.baseURL.vend ::: Shiro.logoutURL.vend
  
  def RedirectBackToReferrer = {
    val uri = S.uriAndQueryString
    RedirectWithState(loginURL, RedirectState(() => { LoginRedirect.set(uri) }))
  }
  
  private def DisplayError(message: String) = () => 
    RedirectWithState(indexURL, RedirectState(() => S.error(message)))
  
  val RequireAuthentication = If(
    () => SecurityUtils.getSubject.isAuthenticated, 
    () => RedirectBackToReferrer)
  
  val RequireNoAuthentication = Unless(
    () => SecurityUtils.getSubject.isAuthenticated,
    () => RedirectBackToReferrer)
  
  def logoutMenu = Menu(Loc("Logout", logoutURL, 
    S.??("logout"), logoutLocParams))
  
  private val logoutLocParams = RequireAuthentication :: 
    EarlyResponse(() => {
      val subject = SecurityUtils.getSubject
      if(subject.isAuthenticated){ subject.logout() }
      Full(RedirectResponse(Shiro.indexURL.vend))
    }) :: Nil
  
  def HasRole(role: String) = 
    If(() => SecurityUtils.getSubject.hasRole(role), 
      DisplayError("You are the wrong role to access that resource."))

  def HasPermission(permission: String) = 
    If(() => SecurityUtils.getSubject.isPermitted(permission), 
      DisplayError("Insufficient permissions to access that resource."))

  def LacksPermission(permission: String) = 
    If(() => !SecurityUtils.getSubject.isPermitted(permission), 
      DisplayError("Overqualified permissions to access that resource."))
}
