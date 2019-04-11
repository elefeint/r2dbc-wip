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

import com.google.api.gax.core.GoogleCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.spanner.DatabaseClient;
import com.google.spanner.v1.CreateSessionRequest;
import com.google.spanner.v1.Session;
import com.google.spanner.v1.SpannerGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.auth.MoreCallCredentials;
import io.r2dbc.spi.Batch;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.IsolationLevel;
import io.r2dbc.spi.Statement;
import java.io.IOException;
import org.reactivestreams.Publisher;

/**
 */
public class GrpcSpannerConnection implements Connection {

	private SpannerConnectionConfiguration config;

	SpannerGrpc.SpannerBlockingStub stub;

	private Session session;

	public GrpcSpannerConnection(SpannerConnectionConfiguration config) {
		System.out.println("=== spannerConnection constructor; fully qualified db = " + config.getFullyQualifiedDatabaseName());
		this.config = config;

		ManagedChannel channel = ManagedChannelBuilder
			.forAddress("spanner.googleapis.com", 443)
			.build();
		this.stub = SpannerGrpc.newBlockingStub(channel)
			.withCallCredentials(MoreCallCredentials.from(getCredentials()));

		this.session = this.stub.createSession(CreateSessionRequest.newBuilder()
			.setDatabase(config.getFullyQualifiedDatabaseName())
			.build());
	}

	private GoogleCredentials getCredentials() {
		try {
			return GoogleCredentials.getApplicationDefault();
		} catch (IOException e) {
			System.out.println("can't get credentials");
			e.printStackTrace();
			return null;
		}
	}

	public Publisher<Void> beginTransaction() {
		return null;
	}

	public Publisher<Void> close() {
		return null;
	}

	public Publisher<Void> commitTransaction() {
		return null;
	}

	public Batch createBatch() {
		return null;
	}

	public Publisher<Void> createSavepoint(String s) {
		return null;
	}

	public Statement createStatement(String sql) {
		// plug into spring data spanner here?
		// or streaming read vs streaming sql
		System.out.println("=== Creating statement");
		return new GrpcSpannerReadStatement(this.stub, this.session, sql);
	}

	public Publisher<Void> releaseSavepoint(String s) {
		return null;
	}

	public Publisher<Void> rollbackTransaction() {
		return null;
	}

	public Publisher<Void> rollbackTransactionToSavepoint(String s) {
		return null;
	}

	public Publisher<Void> setTransactionIsolationLevel(IsolationLevel isolationLevel) {
		return null;
	}
}
