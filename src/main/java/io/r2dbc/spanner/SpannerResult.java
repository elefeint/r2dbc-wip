/*
 * Copyright 2017-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.r2dbc.spanner;

import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.function.BiFunction;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static reactor.function.TupleUtils.function;

/**
 */
public class SpannerResult implements Result {

	private Flux<SpannerRow> rows;

	private Mono<SpannerRowMetadata> rowMetadata;

	public SpannerResult(Flux<SpannerRow> rows, Mono<SpannerRowMetadata> metadata) {
		this.rows = rows;
		this.rowMetadata = metadata;
	}

	@Override public Publisher<Integer> getRowsUpdated() {
		// TODO: implement
		return Mono.just(1);
	}

	@Override public <T> Flux<T> map(BiFunction<Row, RowMetadata, ? extends T> f) {
		return this.rows
			.zipWith(this.rowMetadata.repeat())
			.map(function(f::apply));
	}
}