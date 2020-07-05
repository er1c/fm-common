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

import java.util.concurrent.atomic.AtomicInteger

final class RichAtomicInteger(val self: AtomicInteger) extends AnyVal with Ordered[Int] {
  def +=(value: Int): Unit = { self.addAndGet(value); () }
  def -=(value: Int): Unit = { self.addAndGet(-1 * value); () }

  def +=(value: AtomicInteger): Unit = { self.addAndGet(value.get); () }
  def -=(value: AtomicInteger): Unit = { self.addAndGet(-1 * value.get); () }

  def +(value: Int): Int = self.get() + value
  def -(value: Int): Int = self.get() - value

  def +(value: AtomicInteger): AtomicInteger = new AtomicInteger(self.get + value.get)
  def -(value: AtomicInteger): AtomicInteger = new AtomicInteger(self.get - value.get)

  def compare(that: Int): Int = self.get.compare(that)
}
