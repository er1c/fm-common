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

package fm.common.rich

import fm.common.Implicits._
import java.util.Locale
import java.util.function.IntConsumer // not scala-js compatible

final class RichJVMString(val s: String) extends AnyVal {
  // Parse a language tag to a locale - Using Locale.Builder() instead of Locale.forLanguageTag catches malformed strings
  def toLocaleOption: Option[Locale] =
    scala.util.Try { new Locale.Builder().setLanguageTag(s).build() }.toOption.filter { _.isValid }
  def toLocale: Locale = toLocaleOption.getOrElse(throw new Exception(s"Invalid locale language tag: $s"))

  // Note: java.util.function.IntConsumer is not scala-js compatible
  def toUnicodeEscapedJavaString: String = {
    if (null == s) return ""

    val sb: java.lang.StringBuilder = new java.lang.StringBuilder()

    // Using "new IntConsumer ..." maintains Scala 2.11.x compatibility
    s.codePoints()
      .forEach(new IntConsumer {
        override def accept(codepoint: Int): Unit = appendUnicodeEscapedSequence(sb, codepoint)
      })

    sb.toString()
  }

  // Note: java.util.function.IntConsumer is not scala-js compatible
  def toUnicodeEscapedJavaStringExceptASCII: String = {
    if (null == s) return ""

    val sb: java.lang.StringBuilder = new java.lang.StringBuilder()

    // Using "new IntConsumer ..." maintains Scala 2.11.x compatibility
    s.codePoints()
      .forEach(new IntConsumer {
        override def accept(codepoint: Int): Unit = {
          if (codepoint < 128 && !Character.isISOControl(codepoint)) sb.appendCodePoint(codepoint)
          else appendUnicodeEscapedSequence(sb, codepoint)
          ()
        }
      })

    sb.toString()
  }

  private def appendUnicodeEscapedSequence(sb: java.lang.StringBuilder, codepoint: Int): Unit = {
    if (Character.isSupplementaryCodePoint(codepoint)) {
      sb.append("\\u" + String.format("%04X", Character.highSurrogate(codepoint).toInt: Integer))
      sb.append("\\u" + String.format("%04X", Character.lowSurrogate(codepoint).toInt: Integer))
    } else {
      sb.append("\\u" + String.format("%04X", codepoint: Integer))
    }
    ()
  }
}
