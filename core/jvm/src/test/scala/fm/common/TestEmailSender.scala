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

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class TestEmailSender extends AnyFunSuite with Matchers {
  test("isValidEmail") {
    import EmailSender._

    def testAddresses(addresses: Seq[String], result: Boolean): Unit = {
      addresses.foreach { email: String =>
        withClue(email) { isValidEmail(email) shouldBe result }
      }
    }

    // Some (decent-ish) test cases: https://blogs.msdn.microsoft.com/testing123/2009/02/06/email-address-test-cases/

    val validAddresses: Seq[String] = Seq(
      "email@domain.com",
      "firstname.lastname@domain.com",
      "email@subdomain.domain.com",
      "firstname+lastname@domain.com",
      "email@123.123.123.123",
      //"email@[123.123.123.123]",
      //"“email”@domain.com",
      "1234567890@domain.com",
      "email@domain-one.com",
      "_______@domain.com",
      "email@domain.name",
      "email@domain.co.jp",
      "firstname-lastname@domain.com"
    )

    testAddresses(validAddresses, true)

    val invalidAddresses: Seq[String] = Seq(
      "plainaddress",
      "#@%^%#$@#$@#.com",
      "@domain.com",
      "Joe Smith <email@domain.com>",
      "email.domain.com",
      "email@domain@domain.com",
      //".email@domain.com",
      //"email.@domain.com",
      //"email..email@domain.com",
      "あいうえお@domain.com",
      "email@domain.com (Joe Smith)",
      "email@domain",
      "email@-domain.com",
      //"email@111.222.333.44444",
      "email@domain..com"
    )

    testAddresses(invalidAddresses, false)
  }
}
