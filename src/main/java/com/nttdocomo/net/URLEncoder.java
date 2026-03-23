package com.nttdocomo.net;

import java.nio.charset.Charset;

/**
 * Converts strings to strings in {@code x-www-form-urlencoded} format.
 */
public final class URLEncoder {
    private static final Charset DEFAULT_DOJA_ENCODING = Charset.forName("MS932");

    private URLEncoder() {
    }

    /**
     * Converts a string to a URL-encoded string.
     * The Unicode string is converted to a string in the default encoding and
     * is then encoded in {@code x-www-form-urlencoded} format.
     *
     * @param str the string to encode
     * @return the string converted to URL-encoded form
     * @throws NullPointerException if {@code str} is {@code null}
     */
    public static String encode(String str) {
        if (str == null) {
            throw new NullPointerException("str");
        }
        return java.net.URLEncoder.encode(str, DEFAULT_DOJA_ENCODING);
    }
}
