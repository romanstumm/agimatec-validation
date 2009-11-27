package com.agimatec.validation.jsr303.xml;

import com.agimatec.validation.jsr303.util.SecureActions;

import javax.validation.Payload;
import javax.validation.ValidationException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: Holds the information required to create an annotation. <br/>
 * User: roman <br/>
 * Date: 27.11.2009 <br/>
 * Time: 14:13:12 <br/>
 * Copyright: Agimatec GmbH
 */
public class MetaAnnotation<A extends Annotation> {
    private static final String ANNOTATION_PAYLOAD = "payload";
    private static final String ANNOTATION_GROUPS = "groups";
    private static final String ANNOTATION_MESSAGE = "message";

    private final Class<A> type;

    private final Map<String, Object> elements = new HashMap<String, Object>();

    public MetaAnnotation(Class<A> annotationType) {
        this.type = annotationType;
    }

    public MetaAnnotation(Class<A> annotationType, Map<String, Object> elements) {
        this.type = annotationType;
        for (Map.Entry<String, Object> entry : elements.entrySet()) {
            this.elements.put(entry.getKey(), entry.getValue());
        }
    }

    public void putValue(String elementName, Object value) {
        elements.put(elementName, value);
    }

    public Object getValue(String elementName) {
        return elements.get(elementName);
    }

    public boolean contains(String elementName) {
        return elements.containsKey(elementName);
    }

    public int size() {
        return elements.size();
    }

    public Class<A> getType() {
        return type;
    }

    public void setMessage(String message) {
        putValue(ANNOTATION_MESSAGE, message);
    }

    public void setGroups(Class<?>[] groups) {
        putValue(ANNOTATION_GROUPS, groups);
    }

    public void setPayload(Class<? extends Payload>[] payload) {
        putValue(ANNOTATION_PAYLOAD, payload);
    }

    public A createAnnotation() {
        ClassLoader classLoader = SecureActions.getClassLoader(getClass());
        Class<A> proxyClass = (Class<A>) Proxy.getProxyClass(classLoader, getType());
        InvocationHandler handler = new AnnotationProxy(this);
        try {
            return SecureActions.getConstructor(proxyClass, InvocationHandler.class)
                  .newInstance(handler);
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new ValidationException(
                  "Unable to create annotation for configured constraint", e);
        }
    }

}
