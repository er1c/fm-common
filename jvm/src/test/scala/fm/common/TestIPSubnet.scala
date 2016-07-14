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
package fm.common

import org.scalatest.{FunSuite, Matchers}

final class TestIPSubnet extends FunSuite with Matchers {
  import IPSubnet._
  
  test("Check Private") {
    def check(ip: String, isPrivate: Boolean) = TestHelpers.withCallerInfo{ IP(ip).isPrivate should equal(isPrivate) }
    
    Seq(
      "192.168.0.1",
      "192.168.123.123",
      "192.168.255.255",
      "127.0.0.1",
      "10.0.0.0",
      "10.255.255.255",
      "10.10.0.1",
      "10.10.255.255"
    ).foreach{ check(_, isPrivate = true) }
    
    Seq(
      "123.123.123.123",
      "216.9.0.141",
      "192.167.255.255",
      "192.169.0.1",
      "9.255.255.255",
      "11.0.0.0"
    ).foreach{ check(_, isPrivate = false) }
    
  }
  
  test("isValidMask") {
    isValidMask(IP("255.255.255.255")) should equal (true)
    isValidMask(IP("255.255.255.0")) should equal (true)
    isValidMask(IP("255.255.0.0")) should equal (true)
    isValidMask(IP("255.0.0.0")) should equal (true)
    isValidMask(IP("0.0.0.0")) should equal (true)
    
    isValidMask(IP("0.0.0.255")) should equal (false)
    isValidMask(IP("0.0.255.255")) should equal (false)
    isValidMask(IP("0.255.255.255")) should equal (false)
    
    isValidMask(IP("0.0.0.2")) should equal (false)
    isValidMask(IP("0.0.0.4")) should equal (false)
    isValidMask(IP("0.4.0.0")) should equal (false)
  }
  
  test("isValidRange") {
    isValidRange(IP("192.168.0.0"), IP("192.168.0.255")) should equal (true)
    isValidRange(IP("192.168.0.0"), IP("192.168.255.255")) should equal (true)
    
    isValidRange(IP("192.168.0.255"), IP("192.168.0.0")) should equal (false)
    isValidRange(IP("192.168.0.0"), IP("192.168.255.0")) should equal (false)
    
    isValidRange(IP("192.168.0.0"), IP("192.168.255.0")) should equal (false)
    isValidRange(IP("192.168.0.0"), IP("192.168.255.254")) should equal (false)
    isValidRange(IP("192.168.0.0"), IP("192.168.254.255")) should equal (false)
  }
  
  test("parse - 192.168.0.0/24") {
    def check(subnet: IPSubnet): Unit = TestHelpers.withCallerInfo{ subnet.toString should equal("192.168.0.0/24") }
    
    check(parse("192.168.0.0/24"))
    check(parse("192.168.0.0 - 192.168.0.255"))
    check(parse("192.168.0.0-192.168.0.255"))
    check(forRangeOrMask(IP("192.168.0.0"), IP("255.255.255.0")))
    check(forRangeOrMask(IP("192.168.0.0"), IP("192.168.0.255")))
    check(forMask(IP("192.168.0.0"), IP("255.255.255.0")))
    check(forRange(IP("192.168.0.0"), IP("192.168.0.255")))
    
    val net = IPSubnet.parse("192.168.0.0/24")
    
    net.isQuadZero should equal(false)
    net.isDefaultRoute should equal(false)
    
    net.contains(IP("192.168.0.0")) should equal(true)
    net.contains(IP("192.168.0.1")) should equal(true)
    net.contains(IP("192.168.0.254")) should equal(true)
    net.contains(IP("192.168.0.255")) should equal(true)
    net.contains(IP("192.168.0.128")) should equal(true)
    
    net.contains(IP("192.168.1.0")) should equal(false)
    net.contains(IP("192.168.255.0")) should equal(false)
    net.contains(IP("191.168.0.0")) should equal(false)
    net.contains(IP("193.168.0.0")) should equal(false)
  }
  
  test("0.0.0.0/0") {
    val net = IPSubnet.parse("0.0.0.0/0")
    
    net.isQuadZero should equal(true)
    net.isDefaultRoute should equal(true)
    
    net.contains(IP("0.0.0.0")) should equal(true)
    net.contains(IP("1.2.3.4")) should equal(true)
    net.contains(IP("128.128.128.128")) should equal(true)
    net.contains(IP("255.255.255.255")) should equal(true)
  }
}
