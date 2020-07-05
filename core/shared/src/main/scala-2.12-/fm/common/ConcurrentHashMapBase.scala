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

package fm.common

private[common] trait ConcurrentHashMapBase[A, B] extends scala.collection.mutable.Map[A, B] {
  protected def addOneImpl(kv: (A, B)): this.type
  protected def subtractOneImpl(key: A): this.type

  override def +=(kv: (A, B)): this.type = addOneImpl(kv)
  override def -=(k: A): this.type = subtractOneImpl(k)
}
