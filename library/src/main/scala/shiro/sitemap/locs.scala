package shiro.sitemap

import net.liftweb.http.S
import shiro.{Shiro,LoginRedirect}

/**
 * Lift SiteMap Integration
 */
object Locs {
  import net.liftweb.common.Full
  import net.liftweb.http.{RedirectResponse, RedirectWithState, S, RedirectState}
  import net.liftweb.sitemap.{Menu,Loc}
  import net.liftweb.sitemap.Loc.{If,EarlyResponse,DispatchLocSnippets}
  import shiro.Utils._
  
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
    RedirectWithState(indexURL, RedirectState(() => S.error(message)))
  
  val RequireAuthentication = If(
    () => isAuthenticated, 
    () => RedirectBackToReferrer)
  
  val RequireNoAuthentication = If(
    () => !isAuthenticated,
    () => RedirectToIndexURL)
  
  def logoutMenu = Menu(Loc("Logout", logoutURL, 
    S.??("logout"), logoutLocParams))
  
  private val logoutLocParams = RequireAuthentication :: 
    EarlyResponse(() => {
      if(isAuthenticated){ subject.logout() }
      Full(RedirectResponse(Shiro.indexURL.vend))
    }) :: Nil
  
  object DefaultLogin
    extends DispatchLocSnippets 
    with shiro.snippet.DefaultUsernamePasswordLogin { 
    def dispatch = { 
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
}
