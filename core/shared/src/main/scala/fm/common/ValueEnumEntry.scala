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

/**
 * Created by Lloyd on 4/11/16.
 *
 * Copyright 2016
 */
sealed trait ValueEnumEntry[ValueType] {

  /**
   * Value of this entry
   */
  def value: ValueType

}

object ValueEnumEntry {

  implicit class ValueEnumOps[A <: ValueEnumEntry[_]](val enumEntry: A) extends AnyVal {

    /**
     * Checks if the current enum value is contained by the set of enum values in the parameter list.
     *
      * @param firstEnum First enum of the list.
     * @param otherEnums Remaining enums.
     * @return `true` if the current value is contained by the parameter list.
     */
    def in(firstEnum: A, otherEnums: A*): Boolean = in(firstEnum +: otherEnums)

    /**
     * Checks if the current enum value is contained by the set of enum values in the parameter list.
     *
      * @param entries First enum of the list.
     * @return `true` if the current value is contained by the parameter list.
     */
    def in(entries: Seq[A]): Boolean = entries.contains(enumEntry)

  }
}

/**
 * Value Enum Entry parent class for `Int` valued entries
 */
abstract class IntEnumEntry extends ValueEnumEntry[Int]

/**
 * Value Enum Entry parent class for `Long` valued entries
 */
abstract class LongEnumEntry extends ValueEnumEntry[Long]

/**
 * Value Enum Entry parent class for `Short` valued entries
 */
abstract class ShortEnumEntry extends ValueEnumEntry[Short]

/**
 * Value Enum Entry parent class for `String` valued entries
 *
  * This is similar to [[Enum]], but different in that values must be
 * literal values. This restraint allows us to enforce uniqueness at compile time.
 *
 * Note that uniqueness is only guaranteed if you do not do any runtime string manipulation on values.
 */
abstract class StringEnumEntry extends ValueEnumEntry[String]

/**
 * Value Enum Entry parent class for `Byte` valued entries
 */
abstract class ByteEnumEntry extends ValueEnumEntry[Byte]

/**
 * Value Enum Entry parent class for `Char` valued entries
 */
abstract class CharEnumEntry extends ValueEnumEntry[Char]
