package com.agimatec.utility.validation.integration;

import com.agimatec.utility.validation.MetaBeanManagerFactory;
import com.agimatec.utility.validation.ValidationResults;
import com.agimatec.utility.validation.BeanValidator;
import com.agimatec.utility.validation.model.MetaBean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Description: Validatator used for annotated methods. The current validation
 * context is put into a {@link ThreadLocal} <br/>
 * User: roman.stumm <br/>
 * Date: 09.07.2007 <br/>
 * Time: 13:49:11 <br/>
 *
 * @see Validate
 * @see ThreadValidationContext
 */
public class ThreadBeanValidator extends BeanValidator {

    /**
     * validate the method parameters based on @Validate annotations.
     * Requirements:
     * 1. Method must be annotated with @Valiadate
     * (otherwise this method returns and no current validation context is created)
     * 2. Parameter, that are to be validated must also be annotated with @Validate
     *
     * @param method     -  a method
     * @param parameters - the parameters suitable to the method
     * @return a validation result
     */
    public ValidationResults validateCall(Method method, Object[] parameters) {
        if (parameters.length > 0) {
            // shortcut (for performance!)
            if(method.getAnnotation(Validate.class) == null) return null;
            ValidationResults results = new ValidationResults();
            ThreadValidationContext context = new ThreadValidationContext();
            context.setValidationResults(results);
            ThreadValidationContext.setCurrent(context);

            Annotation[][] annotations = method.getParameterAnnotations();
            for (int i = 0; i < parameters.length; i++) {
                for (Annotation anno : annotations[i]) {
                    if (anno instanceof Validate) {
                        if (determineMetaBean((Validate) anno, parameters[i], context)) {
                            validate(context, results);
                        }
                    }
                }
            }
            return results;
        }
        return null;
    }

    /**
     * @param validate
     * @param parameter
     * @param context
     * @return true when validation should happen, false to skip it
     */
    private boolean determineMetaBean(Validate validate, Object parameter,
                                      ThreadValidationContext context) {
        if (validate.value().length() == 0) {
            if(parameter == null) return false;
            Class beanClass;
            if (parameter instanceof Collection) {   // do not validate empty collection
                Collection coll = ((Collection) parameter);
                if (coll.isEmpty()) return false;
                beanClass = coll.iterator().next().getClass(); // get first object
            } else {
                beanClass = parameter.getClass();
            }
            context.setBean(parameter,
                    MetaBeanManagerFactory.getFinder().findForClass(beanClass));
        } else {
            context.setBean(parameter,
                    MetaBeanManagerFactory.getFinder().findForId(validate.value()));
        }
        return true;
    }

    public ValidationResults validate(Object bean) {
        MetaBean metaBean =
                MetaBeanManagerFactory.getFinder().findForClass(bean.getClass());
        return validate(bean, metaBean);
    }

    @Override
    public ValidationResults validate(Object bean, MetaBean metaBean) {
        final ThreadValidationContext context = new ThreadValidationContext();
        context.setValidationResults(new ValidationResults());
        ThreadValidationContext.setCurrent(context);
        context.setBean(bean, metaBean);
        validate(context, context.getValidationResults());
        return context.getValidationResults();
    }
}
