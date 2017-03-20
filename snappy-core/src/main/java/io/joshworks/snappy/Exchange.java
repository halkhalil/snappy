package io.joshworks.snappy;

import io.joshworks.snappy.handler.ConnegHandler;
import io.joshworks.snappy.parser.Parser;
import io.joshworks.snappy.parser.Parsers;
import io.joshworks.snappy.rest.MediaType;
import io.joshworks.snappy.rest.Property;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.PathTemplateMatch;

import java.util.Deque;
import java.util.Map;

/**
 * Created by Josh Gontijo on 3/19/17.
 */
public class Exchange {

    protected final HttpServerExchange exchange;
    protected MediaType responseContentType = MediaType.TEXT_PLAIN_TYPE;


    public Exchange(HttpServerExchange exchange) {
        this.exchange = exchange;
        setNegotiatedContentType();
    }

    //If client accepts anything, json will be used
    private void setNegotiatedContentType() {
        ConnegHandler.NegotiatedMediaType negotiatedMediaType = exchange.getAttachment(ConnegHandler.NEGOTIATED_MEDIA_TYPE);
        MediaType negotiated = negotiatedMediaType == null ? responseContentType : negotiatedMediaType.produces;
        setResponseMediaType(negotiated);
    }


    public HeaderMap headers() {
        return exchange.getRequestHeaders();
    }

    public HeaderValues header(String headerName) {
        return exchange.getRequestHeaders().get(headerName);
    }

    public int status() {
        return exchange.getStatusCode();
    }

    public String pathParameter(String key) {
        PathTemplateMatch pathMatch = exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
        return pathMatch.getParameters().get(key);
    }

    public Property pathParameterValue(String key) {
        return new Property(pathParameter(key));
    }

    public Map<String, Deque<String>> queryParameters() {
        return exchange.getQueryParameters();
    }

    public String queryParameter(String key) {
        Deque<String> params = exchange.getQueryParameters().get(key);
        return params.isEmpty() ? null : params.getFirst();
    }

    public Property queryParameterVal(String key) {
        return new Property(queryParameter(key));
    }

    public Deque<String> queryParameters(String key) {
        return exchange.getQueryParameters().get(key);
    }

    public Cookie cookie(String key) {
        return exchange.getRequestCookies().get(key);
    }

    public Map<String, Cookie> cookies() {
        return exchange.getRequestCookies();
    }

    public String protocol() {
        return exchange.getProtocol().toString();
    }

    public String host() {
        return exchange.getHostName();
    }

    public int port() {
        return exchange.getHostPort();
    }

    public String method() {
        return exchange.getRequestMethod().toString();
    }

    public String scheme() {
        return exchange.getRequestScheme();
    }

    public String path() {
        return exchange.getRequestPath();
    }

    public String userAgent() {
        HeaderValues userAgent = exchange.getRequestHeaders().get(Headers.USER_AGENT);
        if (userAgent != null && !userAgent.isEmpty()) {
            return userAgent.getFirst();
        }
        return null;
    }

    public MediaType type() {
        HeaderValues contentType = exchange.getRequestHeaders().get(Headers.CONTENT_TYPE);
        if (contentType != null && !contentType.isEmpty()) {
            return MediaType.valueOf(contentType.getFirst());
        }
        return MediaType.WILDCARD_TYPE;
    }

    //--------------- Response ---------------
    public Exchange header(String name, String value) {
        exchange.getResponseHeaders().add(HttpString.tryFromString(name), value);
        return this;
    }

    public Exchange header(String name, long value) {
        exchange.getResponseHeaders().add(HttpString.tryFromString(name), value);
        return this;
    }

    public Exchange type(MediaType mediaType) {
        return setResponseMediaType(mediaType);
    }

    public Exchange type(String mediaType) {
        return setResponseMediaType(MediaType.valueOf(mediaType));
    }

    public Exchange status(int status) {
        exchange.setStatusCode(status);
        return this;
    }

    public void send(Object response) {
        this.response(response);
    }

    public void send(Object response, String mediaType) {
        send(response, MediaType.valueOf(mediaType));
    }

    public void send(Object response, MediaType mediaType) {
        type(mediaType);
        this.response(response);
    }

    private Exchange setResponseMediaType(MediaType mediaType) {
        responseContentType = mediaType;
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, mediaType.toString());
        return this;
    }

    private void response(Object response) {
        try {

            Parser responseParser = Parsers.getParser(responseContentType);
            if (responseParser == null) {
                throw new RuntimeException("Could not find Parser for type " + responseContentType.toString());
            }
            exchange.getResponseSender().send(responseParser.writeValue(response));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}