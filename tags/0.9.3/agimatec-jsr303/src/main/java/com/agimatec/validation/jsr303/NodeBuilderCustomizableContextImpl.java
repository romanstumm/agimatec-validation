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
 * Time: 15:30:03 <br/>
 * Copyright: Agimatec GmbH
 */
final class NodeBuilderCustomizableContextImpl
      implements ConstraintValidatorContext.ErrorBuilder.NodeBuilderCustomizableContext {
    private final ConstraintValidatorContextImpl parent;
    private final String messageTemplate;
    private final PathImpl propertyPath;

    NodeBuilderCustomizableContextImpl(ConstraintValidatorContextImpl contextImpl, String template,
                              PathImpl path) {
        parent = contextImpl;
        messageTemplate = template;
        propertyPath = path;
    }

    public ConstraintValidatorContext.ErrorBuilder.NodeContextBuilder inIterable() {
        return new NodeContextBuilderImpl(parent, messageTemplate, propertyPath);
    }

    public ConstraintValidatorContext.ErrorBuilder.NodeBuilderCustomizableContext addSubNode(
          String name) {
        Path.Node node = new NodeImpl(name);
        propertyPath.addNode(node);
        return this;
    }

    public ConstraintValidatorContext addError() {
        parent.errorMessages.add(new ValidationResults.Error(messageTemplate, null,
              propertyPath.toString()));
        return parent;
    }
}
