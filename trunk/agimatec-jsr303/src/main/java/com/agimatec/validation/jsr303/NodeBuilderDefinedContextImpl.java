package com.agimatec.validation.jsr303;

import com.agimatec.validation.ValidationResults;
import com.agimatec.validation.jsr303.util.NodeImpl;
import com.agimatec.validation.jsr303.util.PathImpl;

import javax.validation.ConstraintValidatorContext;

/**
 * Description: <br/>
 * User: roman <br/>
 * Date: 28.09.2009 <br/>
 * Time: 15:29:03 <br/>
 * Copyright: Agimatec GmbH
 */
final class NodeBuilderDefinedContextImpl
      implements ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext {
    private final ConstraintValidatorContextImpl parent;
    private final String messageTemplate;
    private final PathImpl propertyPath;

    NodeBuilderDefinedContextImpl(ConstraintValidatorContextImpl contextImpl, String template,
                    PathImpl path) {
        parent = contextImpl;
        messageTemplate = template;
        propertyPath = path;
    }

    public ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext addNode(
          String name) {
        NodeImpl node = new NodeImpl(name);
        propertyPath.addNode(node);
        return new NodeBuilderCustomizableContextImpl(parent, messageTemplate, propertyPath);
    }

    public ConstraintValidatorContext addConstraintViolation() {
        parent.errorMessages.add(new ValidationResults.Error(messageTemplate, null,
              propertyPath.toString()));
        return parent;
    }
}
