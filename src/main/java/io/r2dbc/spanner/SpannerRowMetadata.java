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

import io.r2dbc.spi.ColumnMetadata;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * TBD, obvs.
 */
public class SpannerRowMetadata implements RowMetadata {

	@Override public ColumnMetadata getColumnMetadata(Object identifier) {
		return new SpannerColumnMetadata();
	}

	@Override public Iterable<? extends ColumnMetadata> getColumnMetadatas() {
		return Arrays.asList(new SpannerColumnMetadata());
	}
}
