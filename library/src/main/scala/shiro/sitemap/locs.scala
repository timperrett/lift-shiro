package shiro.sitemap

import net.liftweb.common.Full
import net.liftweb.http.{RedirectResponse, RedirectState, RedirectWithState, S}
import net.liftweb.sitemap.Loc.{DispatchLocSnippets, EarlyResponse, If}
import net.liftweb.sitemap.{Loc, Menu}
import shiro.Utils._
import shiro.{LoginRedirect, Shiro}

/**
 * Lift SiteMap Integration
 */
object Locs {
  implicit def listToPath(in: List[String]): String = in.mkString("/","/","")

  private val loginURL = Shiro.baseURL.vend ::: Shiro.loginURL.vend
  private val indexURL = Shiro.baseURL.vend ::: Shiro.indexURL.vend
  private val logoutURL = Shiro.baseURL.vend ::: Shiro.logoutURL.vend
  
  def RedirectBackToReferrer = {
    val uri = S.uriAndQueryString
    RedirectWithState(loginURL, RedirectState(() => { LoginRedirect.set(uri) }))
  }
  
  def RedirectToIndexURL = RedirectResponse(indexURL)
  
  private def DisplayError(message: String) = () => 
    RedirectWithState(loginURL, RedirectState(() => S.error(message)))
  
  def RequireAuthentication = If(
    () => isAuthenticated, 
    () => RedirectBackToReferrer)
  
  def RequireNoAuthentication = If(
    () => !isAuthenticated,
    () => RedirectToIndexURL)
  
  def RequireRemembered = If(
    () => isAuthenticatedOrRemembered,
    () => RedirectBackToReferrer)
  
  def RequireNotRemembered = If(
    () => !isAuthenticatedOrRemembered,
    () => RedirectToIndexURL)
  
  def logoutMenu = Menu(Loc("Logout", logoutURL, 
    S.?("logout"), logoutLocParams))
  
  private val logoutLocParams = RequireRemembered :: 
    EarlyResponse(() => {
        if(isAuthenticatedOrRemembered){ subject.logout() }
      Full(RedirectResponse(Shiro.indexURL.vend))
    }) :: Nil
  
  object DefaultLogin
    extends DispatchLocSnippets 
    with shiro.snippet.DefaultUsernamePasswordLogin { 
    def dispatch: DispatchIt = {
      case "login" => render 
    }
  }
  
  def HasRole(role: String) = 
    If(() => hasRole(role), 
      DisplayError("You are the wrong role to access that resource."))
  
  def LacksRole(role: String) = 
    If(() => lacksRole(role),
      DisplayError("You lack the sufficient role to access that resource."))
  
  def HasPermission(permission: String) = 
    If(() => hasPermission(permission), 
      DisplayError("Insufficient permissions to access that resource."))

  def LacksPermission(permission: String) = 
    If(() => lacksPermission(permission), 
      DisplayError("Overqualified permissions to access that resource."))
 
  def HasAnyRoles(roles: Seq[String]) = 
    If(() => hasAnyRoles(roles),
       DisplayError("You are the wrong role to access that resource."))
}
