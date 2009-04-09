package com.agimatec.validation.jsr303;

import com.agimatec.validation.MetaBeanBuilder;
import com.agimatec.validation.model.Features;
import com.agimatec.validation.model.MetaBean;
import com.agimatec.validation.model.MetaProperty;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.validation.*;
import javax.validation.groups.Default;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Description: process the class annotations for constraint validations
 * to build the MetaBean<br/>
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 14:12:51 <br/>
 * Copyright: Agimatec GmbH 2008
 * TODO RSt - overrides attribute not yet implemented
 */
public class AnnotationMetaBeanBuilder extends MetaBeanBuilder {
    private static final Log log = LogFactory.getLog(AnnotationMetaBeanBuilder.class);
    private final ConstraintValidatorFactory constraintFactory;
    private static final String DEFAULT_CONSTAINTS =
          "com/agimatec/validation/jsr303/DefaultConstraints.properties";
    protected Map<String, Class[]> defaultConstraints;

    public AnnotationMetaBeanBuilder(ConstraintValidatorFactory constraintFactory) {
        this.constraintFactory = constraintFactory;
    }

    @Override
    protected MetaBean buildMetaBean(BeanInfo info) {
        MetaBean metabean = super.buildMetaBean(info);   // call super!
        try {
            applyAnnotations(metabean, info);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e.getTargetException());
        }
        return metabean;
    }

    /**
     * add the validation features to the metabean that come from jsr303
     * annotations in the beanClass
     */
    protected void applyAnnotations(MetaBean metabean, BeanInfo beanInfo)
          throws IllegalAccessException, InvocationTargetException {
        final Class<?> beanClass = beanInfo.getBeanDescriptor().getBeanClass();
        processGroupSequence(beanClass, metabean);
        for (Class interfaceClass : beanClass.getInterfaces()) {
            processClass(interfaceClass, metabean);
        }

        // process class, superclasses and interfaces
        List<Class> classSequence = new ArrayList<Class>();
        Class theClass = beanClass;
        while (theClass != null) {
            classSequence.add(theClass);
            theClass = theClass.getSuperclass();
        }
        // start with superclasses and go down the hierarchy so that
        // the child classes are processed last to have the chance to overwrite some declarations
        // of their superclasses and that they see what they inherit at the time of processing
        for (int i = classSequence.size() - 1; i >= 0; i--) {
            Class eachClass = classSequence.get(i);
            processClass(eachClass, metabean);
        }
    }

    /**
     * process class annotations, field and method annotations
     *
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void processClass(Class<?> beanClass, MetaBean metabean)
          throws IllegalAccessException, InvocationTargetException {
        processAnnotations(metabean, null, beanClass, null);

        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            MetaProperty metaProperty = metabean.getProperty(field.getName());
            // create a property for those fields for which there is not yet a MetaProperty
            if (metaProperty == null) {
                metaProperty = new MetaProperty();
                metaProperty.setName(field.getName());
                metaProperty.setType(field.getType());
                if (processAnnotations(metabean, metaProperty, field, null)) {
                    metabean.putProperty(metaProperty.getName(), metaProperty);
                }
            } else {
                processAnnotations(metabean, metaProperty, field, null);
            }
        }
        Method[] methods = beanClass.getDeclaredMethods();
        for (Method method : methods) {
            String propName = null;
            if (method.getName().startsWith("get") &&
                  method.getParameterTypes().length == 0) {
                propName = Introspector.decapitalize(method.getName().substring(3));
            } else if (method.getName().startsWith("is") &&
                  method.getParameterTypes().length == 0) {
                propName = Introspector.decapitalize(method.getName().substring(2));
            }
            // setter annotation is NOT supported in the spec
            /*  else if (method.getName().startsWith("set") && method.getParameterTypes().length == 1) {
                propName = Introspector.decapitalize(method.getName().substring(3));
            } */
            if (propName != null) {
                MetaProperty metaProperty = metabean.getProperty(propName);
                // only those methods, for which we have a MetaProperty
                if (metaProperty != null) {
                    processAnnotations(metabean, metaProperty, method, null);
                }
            }
        }
    }

    private boolean processAnnotations(MetaBean metabean, MetaProperty prop,
                                       AnnotatedElement element,
                                       ConstraintValidation validation)
          throws IllegalAccessException, InvocationTargetException {
        boolean changed = false;
        for (Annotation annotation : element.getDeclaredAnnotations()) {
            changed |= processAnnotation(annotation, prop, metabean, element, validation);
        }
        return changed;
    }

    private boolean processAnnotation(Annotation annotation, MetaProperty prop,
                                      MetaBean metabean, AnnotatedElement element,
                                      ConstraintValidation validation)
          throws IllegalAccessException, InvocationTargetException {
        if (annotation instanceof Valid) {
            return processValid(/*element, metabean, */prop);
        } else {
            /*
            * An annotation is considered a constraint
            * definition if its retention policy contains RUNTIME and if
            * the annotation itself is annotated with javax.validation.Constraint.
            * or if it is a defaultConstraint
            */
            Constraint vcAnno =
                  annotation.annotationType().getAnnotation(Constraint.class);
            Class<? extends ConstraintValidator<?, ?>>[] validatorClass;
            if (vcAnno == null) {
                validatorClass = getDefaultConstraintValidator(annotation);
            } else {
                validatorClass = vcAnno.validatedBy();
            }
            if (validatorClass != null) {
                applyConstraint(annotation, validatorClass, metabean, prop, element,
                      validation);
                return true;
            } else {
                /**
                 * Multi-valued constraints:
                 * To support this, the bean validation provider treats annotations
                 * with a value annotation element
                 * with a return type of an array of constraint annotations
                 * and whose retention is RUNTIME as a list of
                 * annotations that are processed by the Bean Validation implementation.
                 * This means that each constraint specified in
                 * the value element is applied to the target.
                 */
                Object result = getAnnotationValue(annotation, "value");
                if (result != null && result instanceof Annotation[]) {
                    for (Annotation each : (Annotation[]) result) {
                        processAnnotation(each, prop, metabean, element, validation);
                    }
                    return ((Annotation[]) result).length > 0;
                }
            }
        }
        return false;
    }

    private Class<? extends ConstraintValidator<?, ?>>[] getDefaultConstraintValidator(
          Annotation annotation) {
        return getDefaultConstraints().get(annotation.annotationType().getName());
    }

    protected Map<String, Class[]> getDefaultConstraints() {
        if (defaultConstraints == null) {
            Properties defaultConstraintProperties = new Properties();
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            if (classloader == null) classloader = getClass().getClassLoader();
            InputStream stream = classloader.getResourceAsStream(DEFAULT_CONSTAINTS);
            if (stream != null) {
                try {
                    defaultConstraintProperties.load(stream);
                } catch (IOException e) {
                    log.error("cannot load " + DEFAULT_CONSTAINTS, e);
                }
            } else {
                log.warn("cannot find " + DEFAULT_CONSTAINTS);
            }
            defaultConstraints = new HashMap();
            for (Map.Entry entry : defaultConstraintProperties.entrySet()) {

                StringTokenizer tokens =
                      new StringTokenizer((String) entry.getValue(), ", ");
                LinkedList classes = new LinkedList();
                while (tokens.hasMoreTokens()) {
                    String eachClassName = tokens.nextToken();
                    try {
                        Class constraintValidatorClass =
                              Class.forName(eachClassName, true, classloader);
                        classes.add(constraintValidatorClass);
                    } catch (ClassNotFoundException e) {
                        log.error("Cannot find class " + entry.getValue(), e);
                    }
                }
                defaultConstraints
                      .put((String) entry.getKey(),
                            (Class[]) classes.toArray(new Class[classes.size()]));

            }
        }
        return defaultConstraints;
    }

    private boolean processValid(/*AnnotatedElement element, MetaBean metabean, */
                                 MetaProperty prop) {
        if (prop != null && prop.getMetaBean() == null) {
            prop.putFeature(Features.Property.REF_CASCADE, Boolean.TRUE);
            // support for runtime type determination: therefore here no action to find the bean type
            /*   if (Collection.class.isAssignableFrom(prop.getType())) { // determine beanType
         Class clazz;
         clazz = findBeanType(element, metabean, prop);
         if (clazz != null) {
             prop.putFeature(Features.Property.REF_BEAN_TYPE, clazz);
         }
     }       */
            return true;
        }
        return false;
    }

    private void processGroupSequence(Class<?> beanClass, MetaBean metabean) {
        GroupSequence annotation = beanClass.getAnnotation(GroupSequence.class);
        List<Class<?>> groupSeq = metabean.getFeature(Jsr303Features.Bean.GROUP_SEQ);
        if (groupSeq == null) {
            groupSeq = new ArrayList(annotation == null ? 1 : annotation.value().length);
            metabean.putFeature(Jsr303Features.Bean.GROUP_SEQ, groupSeq);
        }
        if (annotation == null) {
            groupSeq.add(Default.class);
        } else {
            for (Class<?> group : annotation.value()) {
                if (group.getName().equals(beanClass.getName())) {
                    groupSeq.add(Default.class);
                /*// TODO RSt - clarify: is this behavior meant by the spec?
                } else if (group.getName().equals(Default.class.getName())) {
                    throw new ValidationException(
                          "'Default.class' must not appear in @GroupSequence! Use '" +
                                beanClass.getSimpleName() + ".class' instead."); */
                } else {
                    groupSeq.add(group);
                }
            }
        }
    }

    /*  private Class findBeanType(AnnotatedElement element, MetaBean metabean, MetaProperty prop) {
  Class clazz;
  if (element instanceof Field) {
      clazz = ReflectUtils.getBeanTypeFromField((Field) element);
  } else if (element instanceof Method) {
      Method m = (Method) element;
      if (m.getParameterTypes().length == 0) {
          clazz = ReflectUtils.getBeanTypeFromGetter(m);
      } else {
          clazz = ReflectUtils.getBeanTypeFromSetter(m);
      }
  } else {
      clazz = ReflectUtils.getBeanType(metabean.getBeanClass(), prop.getName());
  }
  return clazz;
}      */

    private Object getAnnotationValue(Annotation annotation, String name)
          throws IllegalAccessException, InvocationTargetException {
        Method valueMethod = null;
        try {
            valueMethod = annotation.annotationType().getDeclaredMethod(name);
        } catch (NoSuchMethodException ex) { /* do nothing */ }
        if (null != valueMethod) {
            return valueMethod.invoke(annotation);
        }
        return null;
    }

    /**
     * @param parentValidation - null or the parent validation when it is a composed validation
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void applyConstraint(Annotation annotation,
                                 Class<? extends ConstraintValidator>[] constraintClasses,
                                 MetaBean metabean, MetaProperty prop,
                                 AnnotatedElement element,
                                 ConstraintValidation parentValidation)
          throws IllegalAccessException, InvocationTargetException {
        // The lifetime of a constraint validation implementation instance is undefined.
        for (Class constraintClass : constraintClasses) {
            ConstraintValidator constraint =
                  constraintFactory.getInstance(constraintClass);
            constraint.initialize(annotation);
            Object groups = getAnnotationValue(annotation, "groups");
            if (groups instanceof Class<?>) {
                groups = new Class<?>[]{(Class<?>) groups};
            }
            if (!(groups instanceof Class<?>[])) {
                groups = null;
            }
            ConstraintValidation validation = new ConstraintValidation(
                  new ConstraintValidator[]{constraint}, (Class<?>[]) groups, annotation,
                  element);
            if (parentValidation == null) {
                if (prop != null) {
                    prop.addValidation(validation);
                } else {
                    metabean.addValidation(validation);
                }
            } else {
                parentValidation.addComposed(validation);
            }
            // process composed constraints:
            // here are not other superclasses possible, because annotations do not inherit!
            processAnnotations(metabean, prop, annotation.annotationType(),
                  validation); // recursion!
        }
    }
}
