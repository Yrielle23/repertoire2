package com.sprit.routing;

import java.util.Objects;

public final class UrlMethode {
    private final String url;
    private final String method;

    public UrlMethode(String url, String method) {
        this.url = url == null ? "" : url.trim();
        this.method = method == null ? "" : method.trim().toUpperCase();
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UrlMethode)) return false;
        UrlMethode that = (UrlMethode) o;
        return Objects.equals(url, that.url) && Objects.equals(method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, method);
    }

    @Override
    public String toString() {
        return method + " " + url;
    }
}
