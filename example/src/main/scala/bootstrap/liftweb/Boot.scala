package bootstrap.liftweb

import net.liftweb.http.LiftRules
import net.liftweb.sitemap._
import shiro.Shiro
import shiro.sitemap.Locs._

class Boot {
  def boot {
    Shiro.init()
    
    LiftRules.addToPackages("eu.getintheloop")
    
    LiftRules.setSiteMap(SiteMap(List(
      Menu("Home") / "index" >> RequireAuthentication,
      Menu("Role Test") / "restricted" >> RequireAuthentication >> HasRole("admin"),
      Menu("Login") / "login" >> RequireNoAuthentication
      ) ::: Shiro.menus: _*
    ))
  }
}
