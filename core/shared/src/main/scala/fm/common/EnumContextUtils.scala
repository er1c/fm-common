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

object EnumContextUtils extends EnumContextUtilsBase {

  type Context = scala.reflect.macros.blackbox.Context

  // Constant types
  type CTLong = Long
  type CTInt = Int
  type CTChar = Char

  /**
   * Returns a TermName
   */
  def termName(c: Context)(name: String): c.universe.TermName = {
    c.universe.TermName(name)
  }

  /**
   * Returns a companion symbol
   */
  def companion(c: Context)(sym: c.Symbol): c.universe.Symbol = sym.companion

  /**
   * Returns a PartialFunction for turning symbols into names
   */
  def constructorsToParamNamesPF(
    c: Context
  ): PartialFunction[c.universe.Symbol, List[c.universe.Name]] = {
    case m if m.isConstructor =>
      m.asMethod.paramLists.flatten.map(_.asTerm.name)
  }

  /**
   * Returns the reserved constructor name
   */
  def constructorName(c: Context): c.universe.TermName = {
    c.universe.termNames.CONSTRUCTOR
  }
}
