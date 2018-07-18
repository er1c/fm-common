/*
 * Copyright 2018 Frugal Mechanic (http://frugalmechanic.com)
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

import org.scalatest.{FunSuite,Matchers}

class TestAnyRefNullChecks extends FunSuite with Matchers {

  test("isNull / isNotNull / nonNull") {
    val strNull: String = null
    val strNonNull: String = "non-null"

    strNull.isNull should equal (true)
    strNull.isNotNull should equal (false)
    strNull.nonNull should equal (false)

    strNonNull.isNull should equal (false)
    strNonNull.isNotNull should equal (true)
    strNonNull.nonNull should equal (true)
  }

}
