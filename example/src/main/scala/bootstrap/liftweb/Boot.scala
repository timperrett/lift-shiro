package bootstrap.liftweb

import net.liftweb.http.LiftRules
import net.liftweb.sitemap._
import shiro.Shiro
import shiro.sitemap.Locs._
import net.liftmodules.FoBoJQ


class Boot {
  def boot() {

    FoBoJQ.Toolkit.Init=FoBoJQ.Toolkit.JQuery191
    Shiro.init()
    
    LiftRules.addToPackages("eu.getintheloop")
    
    LiftRules.setSiteMap(SiteMap(List(
      Menu("Home") / "index" >> RequireAuthentication,
      Menu("Role Test") / "restricted" >> RequireAuthentication >> HasRole("admin"),
      Menu("Login") / "login" >> DefaultLogin >> RequireNoAuthentication
      ) ::: Shiro.menus: _*
    ))
  }
}
