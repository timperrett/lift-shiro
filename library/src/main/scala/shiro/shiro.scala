package shiro

import net.liftweb.common.{Box,Full,Empty}
import net.liftweb.util.Helpers
import net.liftweb.http.{LiftRules, SessionVar, Factory}

import org.apache.shiro.SecurityUtils
import org.apache.shiro.util.{Factory => ShiroFactory}
import org.apache.shiro.config.IniSecurityManagerFactory
import org.apache.shiro.mgt.SecurityManager

object Shiro extends Factory {
  def init(factory: ShiroFactory[SecurityManager]){
    
    import Utils._
    import shiro.snippet._
    
    SecurityUtils.setSecurityManager(factory.getInstance);
    
    LiftRules.loggedInTest = Full(() => isAuthenticated)
    
    LiftRules.snippetDispatch.append {
      case "has_role" | "hasRole" | "HasRole" => HasRole
      case "lacks_role" | "lacksRole" | "LacksRole" => LacksRole
      case "has_permission" | "hasPermission" | "HasPermission" => HasPermission
      case "lacks_permission" | "lacksPermission" | "LacksPermission" => LacksPermission
      case "has_any_roles" | "hasAnyRoles" | "HasAnyRoles" => HasAnyRoles
      case "is_guest" | "isGuest" | "IsGuest" => IsGuest
      case "is_user" | "isUser" | "IsUser" => IsUser
      case "is_authenticated" | "isAuthenticated" | "IsAuthenticated" => IsAuthenticated
      case "is_not_authenticated" | "isNotAuthenticated" | "IsNotAuthenticated" => IsNotAuthenticated
    }
  }
  
  def init(){ 
    init(new IniSecurityManagerFactory("classpath:shiro.ini")) 
  }
  
  /** 
   * Speedy setup helpers
   */
  import net.liftweb.sitemap.Menu
  import shiro.sitemap.Locs
  
  def menus: List[Menu] = sitemap
  private lazy val sitemap = List(Locs.logoutMenu)
  
  /** 
   * Configurations
   */
  type Path = List[String]
  val indexURL = new FactoryMaker[Path](Nil){}
  val baseURL = new FactoryMaker[Path](Nil){}
  val loginURL = new FactoryMaker[Path]("login" :: Nil){}
  val logoutURL = new FactoryMaker[Path]("logout" :: Nil){}
}

object LoginRedirect extends SessionVar[Box[String]](Empty){
  override def __nameSalt = Helpers.nextFuncName
}

