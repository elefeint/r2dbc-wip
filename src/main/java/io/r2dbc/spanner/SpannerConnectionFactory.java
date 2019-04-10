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

import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryMetadata;
import reactor.core.publisher.Mono;

/**
 */
public class SpannerConnectionFactory implements ConnectionFactory {

	private SpannerConnectionConfiguration config;

	public SpannerConnectionFactory(SpannerConnectionConfiguration config) {
		this.config = config;
	}

	@Override
	public Mono<ClientLibrarySpannerConnection> create() {
		return Mono.defer(() -> connect());
	}

	private Mono<ClientLibrarySpannerConnection> connect() {

		SpannerOptions options = SpannerOptions.newBuilder().build();
		Spanner spanner = options.getService();

		DatabaseId db = DatabaseId.of(options.getProjectId(), config.getInstanceName(), config.getDatabaseName());
		DatabaseClient dbClient = spanner.getDatabaseClient(db);

		return Mono.just(new ClientLibrarySpannerConnection(dbClient));

	}

	@Override
	public ConnectionFactoryMetadata getMetadata() {
		return SpannerConnectionFactoryMetadata.INSTANCE;
	}
}
