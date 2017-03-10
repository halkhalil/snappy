package com.josue.simpletow;

/**
 * Created by josh on 3/7/17.
 */
class MappedEndpoint {

    final String prefix; //HTTP methods or WS etc
    final String url;
    final Type type;

    MappedEndpoint(String prefix, String url, Type type) {
        this.prefix = prefix;
        this.url = url;
        this.type = type;
    }

    public enum Type {
        REST, WS, STATIC, SSE
    }
}