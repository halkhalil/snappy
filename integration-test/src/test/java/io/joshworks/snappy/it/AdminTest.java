/*
 * Copyright 2017 Josue Gontijo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.joshworks.snappy.it;

import com.mashape.unirest.http.HttpResponse;
import io.joshworks.snappy.client.RestClient;
import io.joshworks.snappy.metric.MetricData;
import io.joshworks.snappy.metric.RestMetrics;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Optional;

import static io.joshworks.snappy.SnappyServer.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Josh Gontijo on 3/15/17.
 */
public class AdminTest {

    @BeforeClass
    public static void setup() {
        enableHttpMetrics();

        get("/test", exchange -> {
        });


        start();
    }

    @AfterClass
    public static void shutdown() {
        stop();
    }

    @Test
    public void metrics() throws Exception {
        int status = RestClient.get("http://localhost:9000/test").asString().getStatus();
        assertEquals(200, status);

        HttpResponse<MetricData> response = RestClient.get("http://localhost:9100/metrics").asObject(MetricData.class);
        assertEquals(200, response.getStatus());

        MetricData metrics = response.getBody();
        assertNotNull(metrics);
        assertEquals(1, metrics.resources.size());

        Optional<RestMetrics> foundMetrics = metrics.resources.stream()
                .filter(m -> m.getUrl().equals("/test"))
                .findFirst();

        assertTrue(foundMetrics.isPresent());
        RestMetrics metric = foundMetrics.get();
        assertEquals(1L, metric.getMetrics().getTotalRequests());
        assertEquals(1, metric.getMetrics().getResponses().size());
        assertEquals(1, metric.getMetrics().getResponses().get("200").get()); //200 OK

    }


}
