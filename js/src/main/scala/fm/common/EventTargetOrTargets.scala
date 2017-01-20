/*
 * Copyright 2016 Frugal Mechanic (http://frugalmechanic.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fm.common

import org.scalajs.dom.raw.Event
import org.scalajs.jquery.{JQuery, JQueryEventObject}
import scala.scalajs.js

trait EventAttachments[T] {
  def apply(f: T => Unit): Unit
  def apply(f: js.Function1[T, Unit]): Unit
}

trait JQueryEventAttachments {
  def apply(f: JQueryEventObject => Unit): Unit
  def apply(f: (JQueryEventObject, js.Any) => Unit): Unit

  def apply(f: js.Function1[JQueryEventObject, Unit]): Unit
  def apply(f: js.Function2[JQueryEventObject, js.Any, Unit]): Unit
}

trait EventTargetOrTargets extends Any {
  protected def jQueryElements: JQuery
  
  final def onJQueryEvent(tpe: String) = new JQueryEventAttachments {
    def apply(f: JQueryEventObject => Unit): Unit = jQueryElements.on(tpe, f)
    def apply(f: (JQueryEventObject, js.Any) => Unit): Unit = jQueryElements.on(tpe, f)

    def apply(f: js.Function1[JQueryEventObject, Unit]): Unit = jQueryElements.on(tpe, f)
    def apply(f: js.Function2[JQueryEventObject, js.Any, Unit]): Unit = jQueryElements.on(tpe, f)
  }

  final def offJQueryEvent(tpe: String): Unit = jQueryElements.off(tpe)
  final def offJQueryEvent(tpe: String, f: js.Function1[JQueryEventObject, js.Any]): Unit = jQueryElements.off(tpe, null, f)

  final def oneJQueryEvent(tpe: String) = new JQueryEventAttachments {
    def apply(f: JQueryEventObject => Unit): Unit = jQueryElements.one(tpe, f)
    def apply(f: (JQueryEventObject, js.Any) => Unit): Unit = jQueryElements.one(tpe, f)

    def apply(f: js.Function1[JQueryEventObject, Unit]): Unit = jQueryElements.one(tpe, f)
    def apply(f: js.Function2[JQueryEventObject, js.Any, Unit]): Unit = jQueryElements.one(tpe, f)
  }

  def addEventListener[T <: Event](tpe: String)(f: js.Function1[T,Unit]): Unit
  def removeEventListener[T <: Event](tpe: String)(f: js.Function1[T,Unit]): Unit
  
  final def addEventListener[T <: Event](tpe: EventType[T])(f: js.Function1[T,Unit]): Unit = addEventListener(tpe.name)(f)
  final def removeEventListener[T <: Event](tpe: EventType[T])(f: js.Function1[T,Unit]): Unit = removeEventListener(tpe.name)(f)

  final def on[T <: Event](tpe: String): EventAttachments[T] = new EventAttachments[T] {
    def apply(f: T => Unit): Unit = addEventListener(tpe)(f)
    def apply(f: js.Function1[T, Unit]): Unit = addEventListener(tpe)(f)
  }

  final def on[T <: Event](tpe: EventType[T]): EventAttachments[T] = new EventAttachments[T] {
    def apply(f: T => Unit): Unit = addEventListener(tpe)(f)
    def apply(f: js.Function1[T, Unit]): Unit = addEventListener(tpe)(f)
  }

  /**
   * Only execute the event once
   *
   * Note: This is NOT compatible with off()
   */
  final def one[T <: Event](tpe: String)(f: T => Unit): Unit = {

    // Our function definition needs to be able to reference
    // this in order to call removeEventListener.
    var handler: js.Function1[T, Unit] = null

    // Note: this gets implicitly converted to a js.Function1[T, Unit]
    //       which is the only way the removeEventListener call can work.
    handler = { event: T =>
      removeEventListener(tpe)(handler)
      f(event)
    }

    addEventListener(tpe)(handler)
  }
  
  /** Only execute the event once */
  final def one[T <: Event](tpe: EventType[T])(f: T => Unit): Unit = one(tpe.name)(f)

  final def off[T <: Event](tpe: String)(f: js.Function1[T, Unit]): Unit = removeEventListener(tpe)(f)
  final def off[T <: Event](tpe: EventType[T])(f: js.Function1[T, Unit]): Unit = removeEventListener(tpe)(f)
}