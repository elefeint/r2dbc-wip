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

import io.grpc.stub.StreamObserver;
import java.util.function.Function;
import reactor.core.publisher.FluxSink;

/**
 * S - source
 * T - target
 */
public class FluxStreamObserver<S,T> implements StreamObserver<S> {

	private FluxSink<T> sink;

	private Function<S,T> converter;

	public FluxStreamObserver(FluxSink<T> sink, Function<S,T> converter) {
		this.sink = sink;
		this.converter = converter;
	}

	@Override public void onNext(S sourceValue) {
		sink.next(this.converter.apply(sourceValue));
	}

	@Override public void onError(Throwable throwable) {
		this.sink.error(throwable);
	}

	@Override public void onCompleted() {
		this.sink.complete();
	}
}
