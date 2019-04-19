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

package io.r2dbc.spanner.grpc;

import com.google.protobuf.ListValue;
import com.google.protobuf.Value;
import com.google.spanner.v1.PartialResultSet;
import com.google.spanner.v1.ResultSet;
import com.google.spanner.v1.ResultSetMetadata;
import com.google.spanner.v1.StructType;
import io.r2dbc.spanner.SpannerRowMetadata;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import java.util.function.BiFunction;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 */
public class GrpcSpannerResult implements Result {

	/*
	private Flux<SpannerRow> rows;

	private Mono<SpannerRowMetadata> rowMetadata;

	public SpannerResult(Flux<SpannerRow> rows, Mono<SpannerRowMetadata> metadata) {
		this.rows = rows;
		this.rowMetadata = metadata;
	}
	*/

	private PartialResultSet resultSet;

	public GrpcSpannerResult(PartialResultSet resultSet) {
		this.resultSet = resultSet;
	}

	@Override
	public Publisher<Integer> getRowsUpdated() {

		// TODO: implement
		return Mono.just(1);
	}

	@Override
	public <T> Flux<T> map(BiFunction<Row, RowMetadata, ? extends T> f) {
		return Flux.create(sink -> {
			// TODO: on request is not doing anything with streaming read. Discuss.
			sink.onRequest(n -> {
					System.out.println("=== demand = " + n);
				});

			// TODO: store result metadata; it only shows up in first result.
			ResultSetMetadata metadata =  resultSet.getMetadata();
			StructType rowType = metadata.getRowType();
			int fieldsPerRow = rowType.getFieldsCount();

			// In the basic test with a single column being returned from 2 rows, all results come back
			// as a single PartialResultSet.
			System.out.println("!!!!!!!!!!!!!!!! field count in a row = " + fieldsPerRow);

			List<Value> values = resultSet.getValuesList();
			System.out.println("!!!!!!!!!!!!!!!! values = " + values.size());

			// TODO: account for chunked values (field split between partial result sets).
			int startIndex = 0;
			int endIndex = fieldsPerRow;

			// this loop takes care of multiple rows per PartialResultSet
			while (endIndex <= values.size()) {
				System.out.println("looking up columns from " + startIndex + " to " + endIndex);
				sink.next((
					f.apply(
						new GrpcSpannerRow(values.subList(startIndex, endIndex)), new SpannerRowMetadata())));
				startIndex += fieldsPerRow;
				endIndex += fieldsPerRow;

			}

			// TODO: buffer partials; add them to the n
			// TODO: resuming streaming call with resumeToken

				sink.complete();
			});

	}
}
