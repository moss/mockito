/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.internal.creation;

import org.mockito.cglib.proxy.*;
import org.mockito.internal.*;
import org.mockito.internal.creation.cglib.*;
import org.mockito.internal.invocation.*;
import org.mockito.internal.invocation.realmethod.*;
import org.mockito.internal.progress.*;
import org.mockito.invocation.*;
import org.mockito.mock.*;

import java.io.*;
import java.lang.reflect.*;

/**
 * Should be one instance per mock instance, see CglibMockMaker.
 */
public class MethodInterceptorFilter implements MethodInterceptor, Serializable {

    private static final long serialVersionUID = 6182795666612683784L;
    private final InternalMockHandler handler;
    CGLIBHacker cglibHacker = new CGLIBHacker();
    private final MockCreationSettings mockSettings;
    private static final SpecialMethodBehavior[] specialMethodBehaviors = {
            new EqualsMethodBehavior(),
            new HashCodeMethodBehavior(),
            new AcrossJVMSerializationFeature()
    };

    public MethodInterceptorFilter(InternalMockHandler handler, MockCreationSettings mockSettings) {
        this.handler = handler;
        this.mockSettings = mockSettings;
    }

    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy)
            throws Throwable {
        for (SpecialMethodBehavior behavior : specialMethodBehaviors) {
            if (behavior.appliesTo(method)) {
                return behavior.handleMethod(proxy, method, args);
            }
        }

        MockitoMethodProxy mockitoMethodProxy = createMockitoMethodProxy(methodProxy);
        cglibHacker.setMockitoNamingPolicy(mockitoMethodProxy);

        MockitoMethod mockitoMethod = createMockitoMethod(method);

        FilteredCGLIBProxyRealMethod realMethod = new FilteredCGLIBProxyRealMethod(
                mockitoMethodProxy);
        Invocation invocation = new InvocationImpl(proxy, mockitoMethod, args,
                SequenceNumber.next(), realMethod);
        return handler.handle(invocation);
    }

    public MockHandler getHandler() {
        return handler;
    }

    public MockitoMethodProxy createMockitoMethodProxy(MethodProxy methodProxy) {
        if (mockSettings.isSerializable())
            return new SerializableMockitoMethodProxy(methodProxy);
        return new DelegatingMockitoMethodProxy(methodProxy);
    }

    public MockitoMethod createMockitoMethod(Method method) {
        if (mockSettings.isSerializable()) {
            return new SerializableMethod(method);
        } else {
            return new DelegatingMethod(method);
        }
    }
}