/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.galeb.fork.undertow.io;

import io.galeb.fork.undertow.UndertowLogger;
import io.galeb.fork.undertow.UndertowMessages;
import io.galeb.fork.undertow.server.HttpServerExchange;
import io.galeb.fork.undertow.util.Headers;
import io.galeb.fork.undertow.util.StatusCodes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;

/**
 * @author Stuart Douglas
 */
public class BlockingReceiverImpl implements Receiver {
    private static final ErrorCallback END_EXCHANGE = new ErrorCallback() {
        @Override
        public void error(HttpServerExchange exchange, IOException e) {
            if(!exchange.isResponseStarted()) {
                exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
            }
            exchange.setPersistent(false);
            UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
            exchange.endExchange();
        }
    };
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    private final HttpServerExchange exchange;
    private final InputStream inputStream;

    private int maxBufferSize = -1;
    private boolean paused = false;
    private boolean done = false;

    public BlockingReceiverImpl(HttpServerExchange exchange, InputStream inputStream) {
        this.exchange = exchange;
        this.inputStream = inputStream;
    }

    @Override
    public void setMaxBufferSize(int maxBufferSize) {
        this.maxBufferSize = maxBufferSize;
    }

    @Override
    public void receiveFullString(final FullStringCallback callback, ErrorCallback errorCallback) {
        receiveFullString(callback, errorCallback, StandardCharsets.ISO_8859_1);
    }

    @Override
    public void receiveFullString(FullStringCallback callback) {
        receiveFullString(callback, END_EXCHANGE, StandardCharsets.ISO_8859_1);
    }

    @Override
    public void receivePartialString(PartialStringCallback callback, ErrorCallback errorCallback) {
        receivePartialString(callback, errorCallback, StandardCharsets.ISO_8859_1);
    }

    @Override
    public void receivePartialString(PartialStringCallback callback) {
        receivePartialString(callback, END_EXCHANGE, StandardCharsets.ISO_8859_1);
    }

    @Override
    public void receiveFullString(final FullStringCallback callback, final ErrorCallback errorCallback, final Charset charset) {
        if(done) {
            throw UndertowMessages.MESSAGES.requestBodyAlreadyRead();
        }
        final ErrorCallback error = errorCallback == null ? END_EXCHANGE : errorCallback;
        if (callback == null) {
            throw UndertowMessages.MESSAGES.argumentCannotBeNull("callback");
        }
        if (exchange.isRequestComplete()) {
            callback.handle(exchange, "");
            return;
        }
        String contentLengthString = exchange.getRequestHeaders().getFirst(Headers.CONTENT_LENGTH);
        long contentLength;
        final ByteArrayOutputStream sb;
        if (contentLengthString != null) {
            contentLength = Long.parseLong(contentLengthString);
            if (contentLength > Integer.MAX_VALUE) {
                error.error(exchange, new RequestToLargeException());
                return;
            }
            sb = new ByteArrayOutputStream((int) contentLength);
        } else {
            contentLength = -1;
            sb = new ByteArrayOutputStream();
        }
        if (maxBufferSize > 0) {
            if (contentLength > maxBufferSize) {
                error.error(exchange, new RequestToLargeException());
                return;
            }
        }
        byte[] buffer = new byte[1024];
        int s;
        try {
            while ((s = inputStream.read(buffer)) > 0) {
                sb.write(buffer, 0, s);
            }
            callback.handle(exchange, sb.toString(charset.name()));
        } catch (IOException e) {
            error.error(exchange, e);
        }

    }

    @Override
    public void receiveFullString(FullStringCallback callback, Charset charset) {
        receiveFullString(callback, END_EXCHANGE, charset);
    }

    @Override
    public void receivePartialString(final PartialStringCallback callback, final ErrorCallback errorCallback, Charset charset) {
        if(done) {
            throw UndertowMessages.MESSAGES.requestBodyAlreadyRead();
        }
        final ErrorCallback error = errorCallback == null ? END_EXCHANGE : errorCallback;
        if (callback == null) {
            throw UndertowMessages.MESSAGES.argumentCannotBeNull("callback");
        }
        if (exchange.isRequestComplete()) {
            callback.handle(exchange, "", true);
            return;
        }
        String contentLengthString = exchange.getRequestHeaders().getFirst(Headers.CONTENT_LENGTH);
        long contentLength;
        if (contentLengthString != null) {
            contentLength = Long.parseLong(contentLengthString);
            if (contentLength > Integer.MAX_VALUE) {
                error.error(exchange, new RequestToLargeException());
                return;
            }
        } else {
            contentLength = -1;
        }
        if (maxBufferSize > 0) {
            if (contentLength > maxBufferSize) {
                error.error(exchange, new RequestToLargeException());
                return;
            }
        }
        CharsetDecoder decoder = charset.newDecoder();
        byte[] buffer = new byte[1024];
        int s;
        try {
            while ((s = inputStream.read(buffer)) > 0) {
                CharBuffer res = decoder.decode(ByteBuffer.wrap(buffer, 0, s));
                callback.handle(exchange, res.toString(), false);
            }
            callback.handle(exchange, "", true);
        } catch (IOException e) {
            error.error(exchange, e);
        }

    }

    @Override
    public void receivePartialString(PartialStringCallback callback, Charset charset) {
        receivePartialString(callback, END_EXCHANGE, charset);
    }

    @Override
    public void receiveFullBytes(final FullBytesCallback callback, final ErrorCallback errorCallback) {
        if(done) {
            throw UndertowMessages.MESSAGES.requestBodyAlreadyRead();
        }
        final ErrorCallback error = errorCallback == null ? END_EXCHANGE : errorCallback;
        if (callback == null) {
            throw UndertowMessages.MESSAGES.argumentCannotBeNull("callback");
        }
        if (exchange.isRequestComplete()) {
            callback.handle(exchange, EMPTY_BYTE_ARRAY);
            return;
        }
        String contentLengthString = exchange.getRequestHeaders().getFirst(Headers.CONTENT_LENGTH);
        long contentLength;
        final ByteArrayOutputStream sb;
        if (contentLengthString != null) {
            contentLength = Long.parseLong(contentLengthString);
            if (contentLength > Integer.MAX_VALUE) {
                error.error(exchange, new RequestToLargeException());
                return;
            }
            sb = new ByteArrayOutputStream((int) contentLength);
        } else {
            contentLength = -1;
            sb = new ByteArrayOutputStream();
        }
        if (maxBufferSize > 0) {
            if (contentLength > maxBufferSize) {
                error.error(exchange, new RequestToLargeException());
                return;
            }
        }
        byte[] buffer = new byte[1024];
        int s;
        try {
            while ((s = inputStream.read(buffer)) > 0) {
                sb.write(buffer, 0, s);
            }
            callback.handle(exchange, sb.toByteArray());
        } catch (IOException e) {
            error.error(exchange, e);
        }

    }

    @Override
    public void receiveFullBytes(FullBytesCallback callback) {
        receiveFullBytes(callback, END_EXCHANGE);
    }

    @Override
    public void receivePartialBytes(final PartialBytesCallback callback, final ErrorCallback errorCallback) {
        if(done) {
            throw UndertowMessages.MESSAGES.requestBodyAlreadyRead();
        }
        final ErrorCallback error = errorCallback == null ? END_EXCHANGE : errorCallback;
        if (callback == null) {
            throw UndertowMessages.MESSAGES.argumentCannotBeNull("callback");
        }
        if (exchange.isRequestComplete()) {
            callback.handle(exchange, EMPTY_BYTE_ARRAY, true);
            return;
        }
        String contentLengthString = exchange.getRequestHeaders().getFirst(Headers.CONTENT_LENGTH);
        long contentLength;
        if (contentLengthString != null) {
            contentLength = Long.parseLong(contentLengthString);
            if (contentLength > Integer.MAX_VALUE) {
                error.error(exchange, new RequestToLargeException());
                return;
            }
        } else {
            contentLength = -1;
        }
        if (maxBufferSize > 0) {
            if (contentLength > maxBufferSize) {
                error.error(exchange, new RequestToLargeException());
                return;
            }
        }
        byte[] buffer = new byte[1024];
        int s;
        try {
            while ((s = inputStream.read(buffer)) > 0) {
                byte[] newData = new byte[s];
                System.arraycopy(buffer, 0, newData, 0, s);
                callback.handle(exchange, newData, false);
            }
            callback.handle(exchange, EMPTY_BYTE_ARRAY, true);
        } catch (IOException e) {
            error.error(exchange, e);
        }
    }

    @Override
    public void receivePartialBytes(PartialBytesCallback callback) {
        receivePartialBytes(callback, END_EXCHANGE);
    }

    @Override
    public void pause() {
        this.paused = true;
    }

    @Override
    public void resume() {
        this.paused = false;
    }
}
