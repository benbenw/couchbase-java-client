/*
 * Copyright (c) 2017 Couchbase, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.couchbase.client.java.analytics;

import com.couchbase.client.core.annotations.InterfaceAudience;
import com.couchbase.client.core.annotations.InterfaceStability;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.document.json.JsonValue;
import com.couchbase.client.java.query.N1qlParams;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Parameters for Analytics Queries.
 *
 * @author Michael Nitschinger
 * @since 2.4.3
 */
@InterfaceStability.Uncommitted
@InterfaceAudience.Public
public class AnalyticsParams implements Serializable {

    private static final long serialVersionUID = 8888370260267213836L;

    private String clientContextId;
    private Map<String, Object> rawParams;

    private AnalyticsParams() { }

    /**
     * Modifies the given Analytics query (as a {@link JsonObject}) to reflect these {@link N1qlParams}.
     * @param queryJson the Analytics query
     */
    public void injectParams(JsonObject queryJson) {
        if (this.clientContextId != null) {
            queryJson.put("client_context_id", this.clientContextId);
        }

        // Always set pretty to false, since, why not?
        queryJson.put("pretty", false);

        if (this.rawParams != null) {
            for (Map.Entry<String, Object> entry : rawParams.entrySet()) {
                queryJson.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Adds a client context ID to the request, that will be sent back in the response, allowing clients
     * to meaningfully trace requests/responses when many are exchanged.
     *
     * @param clientContextId the client context ID (null to send none)
     * @return this {@link AnalyticsParams} for chaining.
     */
    public AnalyticsParams withContextId(String clientContextId) {
        this.clientContextId = clientContextId;
        return this;
    }

    /**
     * Allows to specify an arbitrary, raw Analytics param.
     *
     * Use with care and only provide options that are supported by the server and are not exposed as part of the
     * overall stable API in the {@link AnalyticsParams} class.
     *
     * @param name the name of the property.
     * @param value the value of the property, only JSON value types are supported.
     * @return this {@link AnalyticsParams} for chaining.
     */
    @InterfaceStability.Uncommitted
    public AnalyticsParams rawParam(String name, Object value) {
        if (this.rawParams == null) {
            this.rawParams = new HashMap<String, Object>();
        }

        if (!JsonValue.checkType(value)) {
            throw new IllegalArgumentException("Only JSON types are supported.");
        }

        rawParams.put(name, value);
        return this;
    }

    /**
     * Start building a {@link AnalyticsParams}, allowing to customize an Analytics requests.
     *
     * @return a new {@link AnalyticsParams}
     */
    public static AnalyticsParams build() {
        return new AnalyticsParams();
    }

}
