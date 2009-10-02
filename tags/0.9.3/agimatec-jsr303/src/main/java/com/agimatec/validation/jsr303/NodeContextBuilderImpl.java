package com.agimatec.validation.jsr303;

import com.agimatec.validation.ValidationResults;
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
      implements ConstraintValidatorContext.ErrorBuilder.NodeContextBuilder {
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

    public ConstraintValidatorContext.ErrorBuilder.NodeBuilderDefinedContext atKey(
          Object key) {
        propertyPath.getLeafNode().setKey(key);
        return new NodeBuilderDefinedContextImpl(parent, messageTemplate, propertyPath);
    }

    public ConstraintValidatorContext.ErrorBuilder.NodeBuilderDefinedContext atIndex(
          Integer index) {
        propertyPath.getLeafNode().setIndex(index);
        return new NodeBuilderDefinedContextImpl(parent, messageTemplate, propertyPath);
    }

    public ConstraintValidatorContext.ErrorBuilder.NodeBuilderCustomizableContext addSubNode(
          String name) {
        Path.Node node = new NodeImpl(name);
        propertyPath.addNode(node);
        return new NodeBuilderCustomizableContextImpl(parent, messageTemplate, propertyPath);
    }

    public ConstraintValidatorContext addError() {
        parent.errorMessages.add(new ValidationResults.Error(messageTemplate, null,
              propertyPath.toString()));
        return parent;
    }
}