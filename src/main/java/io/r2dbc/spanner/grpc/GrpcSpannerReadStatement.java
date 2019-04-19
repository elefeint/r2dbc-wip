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

import com.google.spanner.v1.ExecuteSqlRequest;
import com.google.spanner.v1.PartialResultSet;
import com.google.spanner.v1.Session;
import io.grpc.ClientCall;
import io.grpc.stub.ClientCalls;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 */
public class GrpcSpannerReadStatement implements Statement {

	private ClientCall<ExecuteSqlRequest, PartialResultSet> streamingSqlCall;

	private String sql;

	private Mono<Session> sessionMono;

	public GrpcSpannerReadStatement(
		ClientCall<ExecuteSqlRequest, PartialResultSet> executeStreamingSqlCall, Mono<Session> sessionMono, String sql) {

		this.streamingSqlCall = executeStreamingSqlCall;
		this.sql = sql;
		this.sessionMono = sessionMono;
	}

	@Override
	public Statement add() {
		return null;
	}

	@Override
	public Statement bind(Object o, Object o1) {
		return null;
	}

	@Override
	public Statement bind(int i, Object o) {
		return null;
	}

	@Override
	public Statement bindNull(Object o, Class<?> aClass) {
		return null;
	}

	@Override
	public Statement bindNull(int i, Class<?> aClass) {
		return null;
	}

	@Override
	public Publisher<? extends Result> execute() {

		System.out.println("=== GrpcSpannerReadStatement: execute; sql = " + this.sql);


		return this.sessionMono.flatMapMany(
			session -> {
				System.out.println("got session; flatmapping it into result sets");
				// Async calls don't return anything, so can't just map -- Flux.create() is needed.
				return Flux.<GrpcSpannerResult>create(sink -> {

					ClientCalls.asyncServerStreamingCall(
						this.streamingSqlCall,
						createRequest(session),
						new FluxStreamObserver<PartialResultSet,GrpcSpannerResult>(sink, resultSet -> new GrpcSpannerResult(resultSet)));

				});
			});
	}

	private ExecuteSqlRequest createRequest(Session session) {
		return ExecuteSqlRequest
			.newBuilder()
			.setSession(session.getName())
			.setSql(this.sql)
			.build();
	}
}
