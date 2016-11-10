import play.api.libs.json._

import scala.xml.{Elem, Node}

package object pretty {

  trait CanPrettify[A] {
    def prettify(value: A): String
  }

  object CanPrettify {
    def apply[A: CanPrettify]: CanPrettify[A] = implicitly
  }

  def prettify[A](value: A)(implicit evidence: CanPrettify[A]): String = evidence.prettify(value)
  // equivalent function, but using some scala syntax sugar
  // def prettify[A: CanPrettify](value: A): String = CanPrettify[A].prettify(value)

  object implicits {

    object json {
      implicit object JsValueCanPrettify extends CanPrettify[JsValue] {
        override def prettify(value: JsValue): String = Json.prettyPrint(value)
      }
      implicit object JsObjectCanPrettify extends CanPrettify[JsObject] {
        override def prettify(value: JsObject): String = Json.prettyPrint(value)
      }
      implicit object JsArrayCanPrettify extends CanPrettify[JsArray] {
        override def prettify(value: JsArray): String = Json.prettyPrint(value)
      }
      implicit object JsNumberCanPrettify extends CanPrettify[JsNumber] {
        override def prettify(value: JsNumber): String = Json.prettyPrint(value)
      }
      implicit object JsStringCanPrettify extends CanPrettify[JsString] {
        override def prettify(value: JsString): String = Json.prettyPrint(value)
      }
      implicit object JsUndefinedCanPrettify extends CanPrettify[JsUndefined] {
        override def prettify(value: JsUndefined): String = "undefined"
      }
      implicit object JsBooleanCanPrettify extends CanPrettify[JsBoolean] {
        override def prettify(value: JsBoolean): String = Json.prettyPrint(value)
      }
    }

    object xml {
      implicit object NodeCanPrettify extends CanPrettify[Node] {
        override def prettify(value: Node): String = new scala.xml.PrettyPrinter(80, 2).format(value)
      }
      implicit object ElemCanPrettify extends CanPrettify[Elem] {
        override def prettify(value: Elem): String = new scala.xml.PrettyPrinter(80, 2).format(value)
      }
    }
  }
}


import pretty._
import pretty.implicits.json._
import pretty.implicits.xml._

object Test extends App {
  println(
    prettify(
      Json.obj("hello" -> "World!")
    )
  )
  println(
    Json.obj("Goodbye" -> "World").prettify
  )
  println(
    <root><hello>World!</hello></root>.prettify
  )
}
