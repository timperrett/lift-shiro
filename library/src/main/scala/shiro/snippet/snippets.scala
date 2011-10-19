package shiro.snippet

import scala.xml.NodeSeq
import net.liftweb.http.{DispatchSnippet,S}
import net.liftweb.util.Helpers._
import shiro.Utils._

sealed trait ShiroShippet {
  def verification(xhtml: NodeSeq)(f: Boolean): NodeSeq = 
    if (f) xhtml else NodeSeq.Empty

  def serve(xhtml: NodeSeq, attribute: String = "name")(f: String => Boolean): NodeSeq =
    if (S.attr(attribute) exists f) xhtml else NodeSeq.Empty
}

trait SubjectSnippet extends DispatchSnippet with ShiroShippet {
  def dispatch = {
    case _ => render _
  }
  def render(xhtml: NodeSeq): NodeSeq
}

object HasRole extends SubjectSnippet {
  def render(xhtml: NodeSeq): NodeSeq = serve(xhtml){ 
    hasRole(_)
  }
}

object LacksRole extends SubjectSnippet {
  def render(xhtml: NodeSeq): NodeSeq = serve(xhtml){
    lacksRole(_)
  }
}

object HasPermission extends SubjectSnippet {
  def render(xhtml: NodeSeq): NodeSeq = serve(xhtml){
    hasPermission(_)
  }
}

object LacksPermission extends SubjectSnippet {
  def render(xhtml: NodeSeq): NodeSeq = serve(xhtml){
    lacksPermission(_)
  }
}

object HasAnyRoles extends SubjectSnippet {
  def render(xhtml: NodeSeq): NodeSeq = 
    serve(xhtml, attribute = "roles"){ roles => 
      hasAnyRoles(roles.split(","))
    }
}

object HasAllRoles extends SubjectSnippet {
  def render(xhtml: NodeSeq): NodeSeq = 
    serve(xhtml, attribute = "roles"){ roles => 
      hasAllRoles(roles.split(","))
    }
}

object IsGuest extends SubjectSnippet {
  def render(xhtml: NodeSeq): NodeSeq = verification(xhtml){
    !isAuthenticatedOrRemembered
  }
}

object IsUser extends SubjectSnippet {
  def render(xhtml: NodeSeq): NodeSeq = verification(xhtml){
    isAuthenticatedOrRemembered
  }
}

object IsAuthenticated extends SubjectSnippet {
  def render(xhtml: NodeSeq): NodeSeq = verification(xhtml){
    isAuthenticated
  }
}

object IsNotAuthenticated extends SubjectSnippet {
  def render(xhtml: NodeSeq): NodeSeq = verification(xhtml){
    !isAuthenticated
  }
}

