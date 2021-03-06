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

import fm.common.rich.{
  RichElementTraversable,
  RichEventTargetTraversable,
  RichHTMLElementTraversable,
  RichNodeTraversable
}
import org.scalajs.dom.raw.{Element, EventTarget, HTMLElement, Node}

object Implicits extends Implicits {
  // Duplicated in both the JVM and JS version of JSImplicits.scala
  implicit class ToImmutableArrayByte(val col: TraversableOnce[Byte]) extends AnyVal {
    def toImmutableArray: ImmutableArray[Byte] = ImmutableArray.copy(col)
  }
  implicit class ToImmutableArrayShort(val col: TraversableOnce[Short]) extends AnyVal {
    def toImmutableArray: ImmutableArray[Short] = ImmutableArray.copy(col)
  }
  implicit class ToImmutableArrayInt(val col: TraversableOnce[Int]) extends AnyVal {
    def toImmutableArray: ImmutableArray[Int] = ImmutableArray.copy(col)
  }
  implicit class ToImmutableArrayLong(val col: TraversableOnce[Long]) extends AnyVal {
    def toImmutableArray: ImmutableArray[Long] = ImmutableArray.copy(col)
  }
  implicit class ToImmutableArrayFloat(val col: TraversableOnce[Float]) extends AnyVal {
    def toImmutableArray: ImmutableArray[Float] = ImmutableArray.copy(col)
  }
  implicit class ToImmutableArrayDouble(val col: TraversableOnce[Double]) extends AnyVal {
    def toImmutableArray: ImmutableArray[Double] = ImmutableArray.copy(col)
  }
  implicit class ToImmutableArrayBoolean(val col: TraversableOnce[Boolean]) extends AnyVal {
    def toImmutableArray: ImmutableArray[Boolean] = ImmutableArray.copy(col)
  }
  implicit class ToImmutableArrayChar(val col: TraversableOnce[Char]) extends AnyVal {
    def toImmutableArray: ImmutableArray[Char] = ImmutableArray.copy(col)
  }

  implicit class ToImmutableArrayAnyRef[T <: AnyRef](val col: TraversableOnce[T]) extends AnyVal {
    def toImmutableArray: ImmutableArray[T] = ImmutableArray.copy[AnyRef](col).asInstanceOf[ImmutableArray[T]]
  }
}

trait Implicits extends JSImplicitsBase {
  implicit def toRichEventTargetTraversable(target: Traversable[EventTarget]): RichEventTargetTraversable =
    new RichEventTargetTraversable(target)
  implicit def toRichNodeTraversable(elems: Traversable[Node]): RichNodeTraversable = new RichNodeTraversable(elems)
  implicit def toRichElementTraversable(elems: Traversable[Element]): RichElementTraversable =
    new RichElementTraversable(elems)
  implicit def toRichHTMLElementTraversable(elems: Traversable[HTMLElement]): RichHTMLElementTraversable =
    new RichHTMLElementTraversable(elems)
}
