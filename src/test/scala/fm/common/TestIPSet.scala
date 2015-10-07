/*
 * Copyright 2015 Frugal Mechanic (http://frugalmechanic.com)
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

import org.scalatest.FunSuite
import org.scalatest.Matchers

final class TestIPSet extends FunSuite with Matchers {

  private def yes(set: IPSetMutable, ip: String): Unit = check(set, ip, true)
  private def no(set: IPSetMutable, ip: String): Unit = check(set, ip, false)
  
  private def check(set: IPSetMutable, ip: String, res: Boolean): Unit = {
    set.contains(ip) should equal (res)
    set.result.contains(ip) should equal (res)
  }
  
  test("Contains") {
    val set = IPSetMutable()
    set += "192.168.0.123"
    
    def check192(): Unit = {
      yes(set, "192.168.0.123")
      
      no(set, "192.168.0.122")
      no(set, "192.168.0.124")
      no(set, "0.0.0.123")
    }
    
    def check127(): Unit = {
      yes(set, "127.0.0.0")
      yes(set, "127.255.255.255")
      yes(set, "127.123.123.123")
      yes(set, "127.0.0.123")
      
      no(set, "126.0.0.0")
      no(set, "126.255.255.255")
      no(set, "128.0.0.0")
    }
    
    check192()
    
    set += "127.0.0.0/8"

    check127()    
    check192()
    
    set += "127.0.0.123"
    
    check127()    
    check192()
  }
}
