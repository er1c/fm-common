/*
 * Copyright (c) 2019 Frugal Mechanic (http://frugalmechanic.com)
 * Copyright (c) 2020 the fm-common contributors.
 * See the project homepage at: https://er1c.github.io/fm-common/
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

package fm.common.rich

import fm.common.EventTargetOrTargets
import org.scalajs.dom.raw.{Event, EventTarget}
import org.scalajs.jquery.{jQuery, JQuery}
import scala.scalajs.js

final class RichEventTargetIterable(val self: Iterable[EventTarget]) extends AnyVal with EventTargetOrTargets {
  protected def jQueryElements: JQuery = jQuery(js.Array(self.toIndexedSeq: _*))

  def addEventListener[T <: Event](tpe: String)(f: js.Function1[T, _]): Unit =
    self.foreach { _.addEventListener(tpe, f) }
  def removeEventListener[T <: Event](tpe: String)(f: js.Function1[T, _]): Unit =
    self.foreach { _.removeEventListener(tpe, f) }
}
