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

import scala.collection.immutable._

/**
 * All the cool kids have their own Enumeration implementation, most of which try to
 * do so in the name of implementing exhaustive pattern matching.
 *
 * This is yet another one.
 *
 * Example:
 *
 * {{{
 * scala> sealed trait DummyEnum extends EnumEntry
 * defined trait DummyEnum
 *
 * scala> object DummyEnum extends Enum[DummyEnum] {
 *      |   val values = findValues
 *      |   case object Hello   extends DummyEnum
 *      |   case object GoodBye extends DummyEnum
 *      |   case object Hi      extends DummyEnum
 *      | }
 *
 * scala> DummyEnum.withNameOption("Hello")
 * res0: Option[DummyEnum] = Some(Hello)
 *
 * scala> DummyEnum.withNameOption("Nope")
 * res1: Option[DummyEnum] = None
 * }}}
 *
 * @tparam A The sealed trait
 */
trait Enum[A <: EnumEntry] {

  /**
   * Map of `A` object names to `A`s
   */
  lazy val namesToValuesMap: Map[String, A] =
    values.map(v => v.entryName -> v).toMap

  /**
   * Map of `A` object names in lower case to `A`s for case-insensitive comparison
   */
  lazy final val lowerCaseNamesToValuesMap: Map[String, A] =
    namesToValuesMap.map { case (k, v) => k.toLowerCase -> v }

  /**
   * Map of `A` object names in upper case to `A`s for case-insensitive comparison
   */
  lazy final val upperCaseNameValuesToMap: Map[String, A] =
    namesToValuesMap.map { case (k, v) => k.toUpperCase() -> v }

  /**
   * Map of `A` to their index in the values sequence.
   *
   * A performance optimisation so that indexOf can be found in constant time.
   */
  lazy final val valuesToIndex: Map[A, Int] = values.zipWithIndex.toMap

  /**
   * The sequence of values for your [[Enum]]. You will typically want
   * to implement this in your extending class as a `val` so that `withName`
   * and friends are as efficient as possible.
   *
   * Feel free to implement this however you'd like (including messing around with ordering, etc) if that
   * fits your needs better.
   */
  def values: scala.IndexedSeq[A]
  // Note: Changed back (by Eluvio) to using scala.collection.IndexedSeq instead of immutable.IndexedSeq

  /**
   * Tries to get an `A` by the supplied name. The name corresponds to the .name
   * of the case objects implementing `A`
   *
   * Like [[withName]], this method will throw if the name does not match any of the values'
   * .entryName values.
   */
  def withName(name: String): A =
    withNameOption(name).getOrElse(throw new NoSuchElementException(buildNotFoundMessage(name)))

  /**
   * Optionally returns an `A` for a given name.
   */
  def withNameOption(name: String): Option[A] = namesToValuesMap.get(name)

  /**
   * Tries to get an `A` by the supplied name. The name corresponds to the .name
   * of the case objects implementing `A`, disregarding case
   *
   * Like [[withName]], this method will throw if the name does not match any of the values'
   * .entryName values.
   */
  def withNameInsensitive(name: String): A =
    withNameInsensitiveOption(name).getOrElse(throw new NoSuchElementException(buildNotFoundMessage(name)))

  /**
   * Tries to get an `A` by the supplied name. The name corresponds to the .name
   * of the case objects implementing `A` transformed to upper case
   *
   * Like [[withName]], this method will throw if the name does not match any of the values'
   * .entryName values.
   */
  def withNameUppercaseOnly(name: String): A =
    withNameUppercaseOnlyOption(name).getOrElse(throw new NoSuchElementException(buildNotFoundMessage(name)))

  /**
   * Tries to get an `A` by the supplied name. The name corresponds to the .name
   * of the case objects implementing `A` transformed to lower case
   *
   * Like [[withName]], this method will throw if the name does not match any of the values'
   * .entryName values.
   */
  def withNameLowercaseOnly(name: String): A =
    withNameLowercaseOnlyOption(name).getOrElse(throw new NoSuchElementException(buildNotFoundMessage(name)))

  /**
   * Optionally returns an `A` for a given name, disregarding case
   */
  def withNameInsensitiveOption(name: String): Option[A] =
    lowerCaseNamesToValuesMap.get(name.toLowerCase)

  /**
   * Optionally returns an `A` for a given name assuming the value is upper case
   */
  def withNameUppercaseOnlyOption(name: String): Option[A] =
    upperCaseNameValuesToMap.get(name)

  /**
   * Optionally returns an `A` for a given name assuming the value is lower case
   */
  def withNameLowercaseOnlyOption(name: String): Option[A] =
    lowerCaseNamesToValuesMap.get(name)

  /**
   * Returns the index number of the member passed in the values picked up by this enum
   *
    * @param member the member you want to check the index of
   * @return the index of the first element of values that is equal (as determined by ==) to member, or -1, if none exists.
   */
  def indexOf(member: A): Int = valuesToIndex.getOrElse(member, -1)

  /**
   * Method that returns a Seq of `A` objects that the macro was able to find.
   *
    * You will want to use this in some way to implement your [[values]] method. In fact,
   * if you aren't using this method...why are you even bothering with this lib?
   */
  protected def findValues: IndexedSeq[A] = macro EnumMacros.findValuesImpl[A]

  private def buildNotFoundMessage(notFoundName: String): String = {
    s"$notFoundName is not a member of Enum ($existingEntriesString)"
  }

  private lazy val existingEntriesString =
    values.map(_.entryName).mkString(", ")

}

object Enum {

  /**
   * Finds the Enum companion object for a particular EnumEntry
   */
  implicit def materializeEnum[A <: EnumEntry]: Enum[A] = macro EnumMacros.materializeEnumImpl[A]

}
