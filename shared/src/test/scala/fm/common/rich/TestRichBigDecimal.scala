/*
 * Copyright 2014 Frugal Mechanic (http://frugalmechanic.com)
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

import java.math.BigDecimal
import org.scalatest.{FunSuite,Matchers}
import fm.common.Implicits._

class TestRichBigDecimal extends FunSuite with Matchers {
  private def bd(s: String): BigDecimal = new BigDecimal(s)
  
  test("isZero") {
    bd("0").isZero should equal (true)
    bd("0.00").isZero should equal (true)
    
    bd("0.001").isZero should equal (false)
    bd("1").isZero should equal (false)
    bd("123").isZero should equal (false)
  }
  
  test("isNotZero") {
    bd("0").isNotZero should equal (false)
    bd("0.00").isNotZero should equal (false)
    
    bd("0.001").isNotZero should equal (true)
    bd("1").isNotZero should equal (true)
    bd("123").isNotZero should equal (true) 
  }
  
  test("isOne") {
    bd("1").isOne should equal (true)
    bd("1.00000").isOne should equal (true)
    
    bd("0").isOne should equal (false)
  }
  
}
