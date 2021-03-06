package com.netflix.karyon.server.interceptor;

import com.netflix.karyon.server.MockChannelHandlerContext;
import com.netflix.karyon.transport.interceptor.DuplexInterceptor;
import com.netflix.karyon.transport.interceptor.InterceptorKey;
import com.netflix.karyon.transport.interceptor.KeyEvaluationContext;
import io.netty.buffer.ByteBuf;
import rx.Observable;

/**
 * @author Nitesh Kant
 */
public class TestableDuplexInterceptor implements DuplexInterceptor<ByteBuf, ByteBuf> {

    private final InterceptorKey<ByteBuf, KeyEvaluationContext> filterKey;
    private volatile boolean inCalled;
    private volatile boolean outCalled;
    private volatile boolean wasLastInCallValid;
    private volatile boolean wasLastOutCallValid;

    @SuppressWarnings("unchecked")
    public TestableDuplexInterceptor(InterceptorKey<ByteBuf, KeyEvaluationContext> filterKey) {
        this.filterKey = filterKey;
    }

    public boolean isCalledForIn() {
        return inCalled;
    }

    public boolean isCalledForOut() {
        return outCalled;
    }

    public boolean wasLastInCallValid() {
        return wasLastInCallValid;
    }

    public boolean wasLastOutCallValid() {
        return wasLastOutCallValid;
    }

    @Override
    public Observable<Void> in(ByteBuf request, ByteBuf response) {
        inCalled = true;
        MockChannelHandlerContext mock = new MockChannelHandlerContext("mock");
        wasLastInCallValid = filterKey.apply(request, new KeyEvaluationContext(mock));
        return Observable.empty();
    }

    @Override
    public Observable<Void> out(ByteBuf response) {
        outCalled = true;
        wasLastOutCallValid = filterKey.apply(response, new KeyEvaluationContext(new MockChannelHandlerContext("")));
        return Observable.empty();
    }
}
