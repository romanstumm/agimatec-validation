package com.agimatec.validation.jsr303;

import com.agimatec.validation.jsr303.util.NodeImpl;
import com.agimatec.validation.jsr303.util.PathImpl;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Path;

/**
 * Description: <br/>
 * User: roman <br/>
 * Date: 28.09.2009 <br/>
 * Time: 15:30:38 <br/>
 * Copyright: Agimatec GmbH
 */
final class NodeContextBuilderImpl
      implements ConstraintValidatorContext.ConstraintViolationBuilder.NodeContextBuilder {
    private final ConstraintValidatorContextImpl parent;
    private final String messageTemplate;
    private final PathImpl propertyPath;

    NodeContextBuilderImpl(ConstraintValidatorContextImpl contextImpl,
                                    String template, PathImpl path) {
        parent = contextImpl;
        messageTemplate = template;
        propertyPath = path;
        propertyPath.getLeafNode().setInIterable(true);
    }

    public ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext atKey(
          Object key) {
        propertyPath.getLeafNode().setKey(key);
        return new NodeBuilderDefinedContextImpl(parent, messageTemplate, propertyPath);
    }

    public ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext atIndex(
          Integer index) {
        propertyPath.getLeafNode().setIndex(index);
        return new NodeBuilderDefinedContextImpl(parent, messageTemplate, propertyPath);
    }

    public ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext addNode(
          String name) {
        Path.Node node = new NodeImpl(name);
        propertyPath.addNode(node);
        return new NodeBuilderCustomizableContextImpl(parent, messageTemplate, propertyPath);
    }

    public ConstraintValidatorContext addConstraintViolation() {
        parent.addError(messageTemplate, propertyPath);
        return parent;
    }
}