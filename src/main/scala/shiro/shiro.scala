package shiro

import net.liftweb.common.{Box,Full,Empty}
import net.liftweb.util.Helpers
import net.liftweb.http.{LiftRules, S, SessionVar, RequestVar, Factory}

import org.apache.shiro.SecurityUtils
import org.apache.shiro.util.{Factory => ShiroFactory}
import org.apache.shiro.config.IniSecurityManagerFactory
import org.apache.shiro.mgt.SecurityManager
import org.apache.shiro.subject.Subject

object Shiro extends Factory {
  def init(factory: ShiroFactory[SecurityManager]){
    SecurityUtils.setSecurityManager(factory.getInstance);
    
    LiftRules.loggedInTest = Full(() => SecurityUtils.getSubject.isAuthenticated)
    
    LiftRules.snippetDispatch.append {
	  case "subject" | "Subject" | "subjects" | "Subjects" => Subjects
	}
  }
  
  def init(){ 
    init(new IniSecurityManagerFactory("classpath:shiro.ini")) 
  }
  
  /** 
   * Speedy setup helpers
   */
  import net.liftweb.sitemap.Menu
  
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

