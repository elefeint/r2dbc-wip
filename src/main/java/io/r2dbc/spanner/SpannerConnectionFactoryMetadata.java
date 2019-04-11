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

import io.r2dbc.spi.ConnectionFactoryMetadata;
import javax.print.attribute.standard.MediaSize;

/**
 */
public class SpannerConnectionFactoryMetadata implements ConnectionFactoryMetadata {

	public static final String NAME = "Spanner";

	public static final SpannerConnectionFactoryMetadata INSTANCE = new SpannerConnectionFactoryMetadata();

	private SpannerConnectionFactoryMetadata() {
	}

	@Override public String getName() {
		return NAME;
	}
}
