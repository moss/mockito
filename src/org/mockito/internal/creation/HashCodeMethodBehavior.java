package org.mockito.internal.creation;

import org.mockito.internal.util.*;

import java.lang.reflect.*;

public class HashCodeMethodBehavior implements SpecialMethodBehavior {
    private final ObjectMethodsGuru objectMethodsGuru = new ObjectMethodsGuru();

    @Override public boolean appliesTo(Method method) {
        return objectMethodsGuru.isHashCodeMethod(method);
    }

    @Override public Object handleMethod(Object proxy, Method method, Object[] args) {
        return System.identityHashCode(proxy);
    }
}
