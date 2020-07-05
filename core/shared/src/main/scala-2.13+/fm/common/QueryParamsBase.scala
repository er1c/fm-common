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

import scala.collection.{mutable, IterableFactory, SeqOps}

final class QueryParamsBuilder extends QueryParamsBuilderBase {
  override def addOne(param: (String, String)): this.type = { builder += param; this }
}

private[common] trait QueryParamsBase extends SeqOps[(String, String), Seq, QueryParams] {
  protected def params: Seq[(String, String)]

  //
  // SeqOps Implementation:
  //
  override def iterator: Iterator[(String, String)] = params.iterator
  override def apply(idx: Int): (String, String) = params(idx)
  override def length: Int = params.length

  override protected def coll: QueryParams = this.asInstanceOf[QueryParams]
  override def iterableFactory: IterableFactory[Seq] = Seq
  override def toIterable: Iterable[(String, String)] = params

  override def fromSpecific(it: IterableOnce[(String, String)]): QueryParams = {
    val builder: QueryParamsBuilder = new QueryParamsBuilder
    it.iterator.foreach { builder += _ }
    builder.result()
  }

  protected[this] override def newSpecificBuilder: mutable.Builder[(String, String), QueryParams] =
    new QueryParamsBuilder
}
