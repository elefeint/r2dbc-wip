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

import io.r2dbc.h2.H2Result;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import io.r2dbc.spi.Statement;
import java.util.concurrent.CountDownLatch;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static io.r2dbc.spi.ConnectionFactoryOptions.PROTOCOL;

/**
 */
public class SampleSpannerTest {
	@Test
	public void testsomething() throws InterruptedException {

		System.out.println("*** starting");
		CountDownLatch latch = new CountDownLatch(2);

		ConnectionFactory connectionFactory = ConnectionFactories.get(ConnectionFactoryOptions.builder()
			.option(DRIVER, "spanner")
			//.option(PROTOCOL, "mem")  // file, mem
			//.option(DATABASE, "reactivesample")
			//.option(Option.valueOf("url"), "jdbc:h2:mem:test")
			//.option(Option.valueOf("options"), "INIT=runscript from 'classpath:schema.sql'")
			.build());

		//	new H2C

		// had to cast; fix example in https://github.com/r2dbc/r2dbc-h2
		Mono<Connection> connection = (Mono<Connection>)connectionFactory.create();
		System.out.println("*** connection mono " + connection);


		connection.doOnError(e -> {
			System.out.println("*** error: " + e);
			latch.countDown();
		});



		connection.subscribe(conn -> {
			System.out.println("*** subscribed: " + conn);

			Statement statement = conn.createStatement("select count(*) from book");
			Flux<SpannerResult> resultflux = (Flux)statement.execute();

			resultflux.subscribe(result -> {
				System.out.println("*** result: " + result);
				result.map((Row row, RowMetadata meta) -> {
					System.out.println("*** fake column 0 requested: " + row.toString());
					return (String)row.get(0);
				}).subscribe((String s) -> {
					System.out.println("Row string retrieved: " + s);
					latch.countDown();
				});
			});
		});

		latch.await();


	}
}