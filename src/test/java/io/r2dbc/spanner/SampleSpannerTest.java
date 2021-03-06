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

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import io.r2dbc.spi.Statement;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * Data setup for this was done manually.
 */
public class SampleSpannerTest {

	@Test
	public void testConnectionOverGrpc() throws InterruptedException {

		System.out.println("*** starting");
		CountDownLatch latch = new CountDownLatch(2);

		ConnectionFactory connectionFactory =
			ConnectionFactories.get(ConnectionFactoryOptions.builder()
				.option(DRIVER, "spanner")
				.option(Option.valueOf("instance"), "reactivetest")
				.option(DATABASE, "testdb")
				.option(Option.valueOf("impl"), "grpc") // comment out for client library path
				.build());

		Mono<Connection> connection = (Mono<Connection>) connectionFactory.create();
		System.out.println("*** connection mono " + connection);

		connection.doOnError(e -> {
			fail("connection errored out: " + e.getMessage());
		});

		Vector<String> titles = new Vector<>();

		connection.flatMapMany(conn -> {
			System.out.println("*** subscribed: " + conn);

			Statement statement = conn.createStatement("select title from book");
			return statement.execute();
		}).flatMap(result -> result.map((Row row, RowMetadata meta) -> {
			// gets the first column (which happens to be the only column).
			System.out.println("*** column 0 requested: " + row.toString());
			return (String) row.get(0);
		})).subscribe((String s) -> {
			titles.add(s);
			latch.countDown();
		});

		latch.await();
		assertThat(titles).containsExactlyInAnyOrder("three musketeers", "and then there was thunder");
	}


	@Test
	public void testConnectionOverClientLibrary() throws InterruptedException {

		System.out.println("*** starting");
		CountDownLatch latch = new CountDownLatch(2);

		ConnectionFactory connectionFactory =
			ConnectionFactories.get(ConnectionFactoryOptions.builder()
				.option(DRIVER, "spanner")
				.option(Option.valueOf("instance"), "reactivetest")
				.option(DATABASE, "testdb")
				// no grpc option; defaults to client library
				.build());

		Mono<Connection> connection = (Mono<Connection>) connectionFactory.create();
		System.out.println("*** connection mono " + connection);

		connection.doOnError(e -> {
			fail("connection errored out: " + e.getMessage());
		});

		Vector<String> titles = new Vector<>();

		connection.flatMapMany(conn -> {
			System.out.println("*** subscribed: " + conn);

			Statement statement = conn.createStatement("select title from book");
			return statement.execute();
		}).flatMap(result -> result.map((Row row, RowMetadata meta) -> {
			// gets the first column (which happens to be the only column).
			System.out.println("*** column 0 requested: " + row.toString());
			return (String) row.get(0);
		})).subscribe((String s) -> {
			titles.add(s);
			latch.countDown();
		});

		latch.await();
		assertThat(titles).containsExactlyInAnyOrder("three musketeers", "and then there was thunder");
	}
}