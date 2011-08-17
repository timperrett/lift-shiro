package shiro.snippet

import scala.xml.NodeSeq
import net.liftweb.common.{Box,Full,Empty,Failure}
import net.liftweb.http.{DispatchSnippet,S}
import net.liftweb.util.Helpers.tryo
import org.apache.shiro.SecurityUtils
import org.apache.shiro.subject.Subject
import shiro.Utils._

sealed trait ShiroShippet {
  def serve(xhtml: NodeSeq, attribute: String = "name")(f: String => Boolean): NodeSeq = 
    (for { 
      attr <- S.attr(attribute) if f(attr)
    } yield xhtml) openOr NodeSeq.Empty
}

// sealed trait Utils {
//   protected def serve(xhtml: NodeSeq)(f: (Subject, String) => Boolean): NodeSeq =
//     serve("name", xhtml)(f)
//   
//   protected def serve(attribute: String, xhtml: NodeSeq)(f: (Subject, String) => Boolean): NodeSeq = 
//     (for {
//       s <- Box.!!(SecurityUtils.getSubject)
//       attr <- S.attr(attribute) if f(s,attr)
//     } yield xhtml) getOrElse NodeSeq.Empty
// }

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
  def render(xhtml: NodeSeq): NodeSeq = {
    val delimiter = ","
    serve(xhtml, attribute = "roles"){ roles => 
      hasAnyRoles(roles.split(delimiter))
    }
  }
}

