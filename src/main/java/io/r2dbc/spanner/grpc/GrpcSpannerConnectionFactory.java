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

import io.r2dbc.spanner.SpannerConnectionConfiguration;
import io.r2dbc.spanner.SpannerConnectionFactoryMetadata;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryMetadata;
import reactor.core.publisher.Mono;

/**
 */
public class GrpcSpannerConnectionFactory implements ConnectionFactory {

	private SpannerConnectionConfiguration config;

	public GrpcSpannerConnectionFactory(SpannerConnectionConfiguration config) {
		this.config = config;
	}

	@Override
	public Mono<Connection> create() {
		return Mono.defer(() -> connect());
	}

	private Mono<Connection> connect() {
		return Mono.just(new GrpcSpannerConnection(config));

/*		SpannerOptions options = SpannerOptions.newBuilder().build();
		Spanner spanner = options.getService();

		DatabaseId db = DatabaseId.of(options.getProjectId(), config.getInstanceName(), config.getDatabaseName());
		DatabaseClient dbClient = spanner.getDatabaseClient(db);

		return Mono.just(new ClientLibrarySpannerConnection(dbClient));*/

	}

	@Override
	public ConnectionFactoryMetadata getMetadata() {
		return SpannerConnectionFactoryMetadata.INSTANCE;
	}
}
