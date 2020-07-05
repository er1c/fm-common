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

import fm.common.{QueryParams, URL}
import scala.util.Try

/**
 * Adds JVM specific helpers for creating QueryParams from URI/URL instances
 */
object RichQueryParams {
  def get(url: URL): Option[QueryParams] = Try { apply(url) }.toOption

  /** Create Query Params form a URL */
  def apply(url: URL): QueryParams = QueryParams(url.getQuery)
}
