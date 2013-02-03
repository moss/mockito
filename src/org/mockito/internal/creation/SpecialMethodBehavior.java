package org.mockito.internal.creation;

import java.lang.reflect.*;

public interface SpecialMethodBehavior {
    boolean appliesTo(Method method);

    Object handleMethod(Object proxy, Method method, Object[] args) throws Exception;
}
