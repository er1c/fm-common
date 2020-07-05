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

import fm.common.Implicits._
import org.scalajs.dom.raw.Node

final class RichNodeIterable(val elems: Iterable[Node]) extends AnyVal {
  def textContent: IndexedSeq[String] = elems.map { _.textContent }.toVector

  def textContent_=(value: String): Unit = elems.foreach { _.textContent = value }

  def remove(): Unit = elems.foreach { _.remove() }
}
