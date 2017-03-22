package io.joshworks.snappy.it;

import com.mashape.unirest.http.HttpResponse;
import io.joshworks.snappy.client.RestClient;
import io.joshworks.snappy.rest.ExceptionResponse;
import io.joshworks.snappy.rest.MediaType;
import io.undertow.util.Headers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.joshworks.snappy.SnappyServer.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Josh Gontijo on 3/15/17.
 */
public class RestErrorHandlerTest {


    private static final String EXCEPTION_MESSAGE = "SOME ERROR OCCURRED";

    @BeforeClass
    public static void setup() {
        get("/error1", (exchange) -> {
        });
        get("/exception", (exchange) -> {
            throw new RuntimeException(EXCEPTION_MESSAGE);
        });

        start();
    }

    @AfterClass
    public static void shutdown() {
        stop();
    }

    @Test
    public void unsupportedContentType() throws Exception {
        HttpResponse<ExceptionResponse> response = RestClient.get("http://localhost:9000/error1")
                .header("Content-Type", "application/xml").asObject(ExceptionResponse.class);

        assertEquals(415, response.getStatus());
        //default response type
        assertEquals(1, response.getHeaders().get(Headers.CONTENT_TYPE.toString()).size());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().get(Headers.CONTENT_TYPE.toString()).get(0));

        ExceptionResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(415, body.getStatus());
        assertNotNull(body.getMessage());
    }

    @Test
    public void unsupportedAcceptedType() throws Exception {
        HttpResponse<ExceptionResponse> response = RestClient.get("http://localhost:9000/error1")
                .header("Accept", "application/xml").asObject(ExceptionResponse.class);

        assertEquals(415, response.getStatus());
        //default response type
        assertEquals(1, response.getHeaders().get(Headers.CONTENT_TYPE.toString()).size());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().get(Headers.CONTENT_TYPE.toString()).get(0));

        ExceptionResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(415, body.getStatus());
        assertNotNull(body.getMessage());
    }

    @Test
    public void exceptionThrown() throws Exception {
        HttpResponse<ExceptionResponse> response = RestClient.get("http://localhost:9000/exception").asObject(ExceptionResponse.class);

        assertEquals(500, response.getStatus());
        //default response type
        assertEquals(1, response.getHeaders().get(Headers.CONTENT_TYPE.toString()).size());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().get(Headers.CONTENT_TYPE.toString()).get(0));

        ExceptionResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(500, body.getStatus());
        assertEquals(EXCEPTION_MESSAGE, body.getMessage());
    }

}
