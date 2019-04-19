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

import com.google.cloud.spanner.Struct;
import com.google.protobuf.ListValue;
import com.google.protobuf.Value;
import io.r2dbc.spi.Row;
import java.util.List;

/**
 * TODO: everything
 */
public class GrpcSpannerRow implements Row {

	private List<Value> listValue;

	public GrpcSpannerRow(List<Value> listValue) {
		this.listValue = listValue;
	}

	// strings only for now
	// TODO: extract column names from metadata; enable looking up by column.
	@Override
	public <T> T get(Object identifier, Class<T> type) {
		// TODO: correct types
		System.out.println("== row.get: " + identifier + "; asked for type: " + type
		+ "; listvalue = " + this.listValue);
		return (T)this.listValue.get((int)identifier).getStringValue();
	}
}
