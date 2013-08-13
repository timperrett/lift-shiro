package example

import org.apache.shiro.realm.AuthorizingRealm
import org.apache.shiro.authc._
import org.apache.shiro.authz._
import org.apache.shiro.authz.permission.WildcardPermission
import org.apache.shiro.subject.PrincipalCollection

import collection.JavaConverters._

/**
 * An example class to demonstrate setting up a custom realm in shiro.
 */
class ExampleRealm extends AuthorizingRealm {
  class User(val username: String, val password: String)

  /**
   * A fake DAO for storing user credentials, roles, permissions, etc.
   * In practice this will probably be a db/persistence obj of some sort.
   */
  object UserDAO {
    // Passwords are stored plain here but in real life please at least BCrypt them like a decent human being.
    private[this] val userCredentials = Map(
      "root" -> "secret",
      "guest" -> "guest",
      "presidentskroob" -> "12345",
      "darkhelmet" -> "ludicrousspeed",
      "lonestarr" -> "vespa"
    )

    private[this] val userRoles = Map(
      "root" -> Set("admin"),
      "guest" -> Set("guest"),
      "presidentskroob" -> Set("president"),
      "darkhelmet" -> Set("darklord", "schwartz"),
      "lonestarr" -> Set("goodguy", "schwartz")
    )

    private[this] val rolePermissions = Map(
      "admin" -> Set(new WildcardPermission("*")),
      "schwartz" -> Set(new WildcardPermission("lightsaber:*")),
      "darklord" -> Set(new WildcardPermission("winnebago:steal:eagle5")),
      // Good guys can do whatever they want with the eagle5.
      "goodguy" -> Set(new WildcardPermission("winnebago:*:eagle5"))
    )

    def getUser(username: String, password: String): Option[User] = for {
      pass <- userCredentials.get(username)
      if (pass == password)
    } yield new User(username, password)

    def getRoles(user: User): Set[String] = {
      userRoles.getOrElse(user.username, Set())
    }

    def getRolePermissions(role: String): Set[WildcardPermission] = {
      rolePermissions.getOrElse(role, Set())
    }
  }

  // The methods from AuthorizingRealm that actually have to be implemented.

  def doGetAuthenticationInfo(token: AuthenticationToken): AuthenticationInfo = {
    val userpassToken = token.asInstanceOf[UsernamePasswordToken]
    val username = userpassToken.getUsername() 
    val password = userpassToken.getPassword() 

    UserDAO.getUser(username, password.mkString("")) match {
      case Some(user: User) => new SimpleAuthenticationInfo(user, user.password, "ExampleRealm")
      case None => throw new AuthenticationException("Invalid credentials provided!")
    }
  }

  def doGetAuthorizationInfo(principals: PrincipalCollection): AuthorizationInfo = {
    val roles: Set[String] = principals.asScala.flatMap(p => UserDAO.getRoles(p.asInstanceOf[User])).toSet
    val permissions: Set[Permission] = roles.flatMap(r => UserDAO.getRolePermissions(r)).toSet

    val authInfo = new SimpleAuthorizationInfo(roles.asJava)
    authInfo.setObjectPermissions(permissions.asJava)

    return authInfo 
  }
}
