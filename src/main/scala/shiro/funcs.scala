package shiro

trait SubjectFuncs {
  import org.apache.shiro.SecurityUtils
  import org.apache.shiro.subject.Subject
  
  implicit protected lazy val subject = SecurityUtils.getSubject
  
  private def test(f: Subject => Boolean)(implicit subject: Subject): Boolean =
    f(subject)
  
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