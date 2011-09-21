package shiro.snippet

import scala.xml.NodeSeq
import net.liftweb.http.{DispatchSnippet,S}
import net.liftweb.util.Helpers._
import shiro.Utils._

sealed trait ShiroShippet {
  def serve(xhtml: NodeSeq)(f: Boolean): NodeSeq = 
    if (f) xhtml else NodeSeq.Empty

  def serve(xhtml: NodeSeq, attribute: String)(f: String => Boolean): NodeSeq = 
    (for { 
      attr <- S.attr(attribute) if f(attr)
    } yield xhtml) openOr NodeSeq.Empty
}

trait SubjectSnippet extends DispatchSnippet with ShiroShippet {
  def dispatch = {
    case _ => render _
  }
  def render(xhtml: NodeSeq): NodeSeq
}

object HasRole extends SubjectSnippet {
  def render(xhtml: NodeSeq): NodeSeq = serve(xhtml, "name"){ 
    hasRole(_)
  }
}

object LacksRole extends SubjectSnippet {
  def render(xhtml: NodeSeq): NodeSeq = serve(xhtml, "name"){
    lacksRole(_)
  }
}

object HasPermission extends SubjectSnippet {
  def render(xhtml: NodeSeq): NodeSeq = serve(xhtml, "name"){
    hasPermission(_)
  }
}

object LacksPermission extends SubjectSnippet {
  def render(xhtml: NodeSeq): NodeSeq = serve(xhtml, "name"){
    lacksPermission(_)
  }
}

object HasAnyRoles extends SubjectSnippet {
  def render(xhtml: NodeSeq): NodeSeq = {
    val delimiter = ","
    serve(xhtml, attribute = "roles"){ roles => 
      hasAnyRoles(roles.split(delimiter))
    }
  }
}

object GuestTag extends SubjectSnippet {
  def render(xhtml: NodeSeq): NodeSeq = serve(xhtml){
    !isAuthenticatedOrRemembered
  }
}

object UserTag extends SubjectSnippet {
  def render(xhtml: NodeSeq): NodeSeq = serve(xhtml){
    isAuthenticatedOrRemembered
  }
}

object AuthenticatedTag extends SubjectSnippet {
  def render(xhtml: NodeSeq): NodeSeq = serve(xhtml){
    isAuthenticated
  }
}

object NotAuthenticatedTag extends SubjectSnippet {
  def render(xhtml: NodeSeq): NodeSeq = serve(xhtml){
    !isAuthenticated
  }
}

object PrincipalTag extends DispatchSnippet {
  def dispatch = {
    case _ => render
  }
  
  def render = "*" #> (principal openOr S.attr("name").openOr("Principal or default value not found")).toString
}

