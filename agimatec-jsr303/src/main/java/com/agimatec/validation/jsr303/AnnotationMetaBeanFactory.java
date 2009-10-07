/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package com.agimatec.validation.jsr303;

import com.agimatec.validation.MetaBeanFactory;
import com.agimatec.validation.jsr303.groups.Group;
import com.agimatec.validation.jsr303.util.SecureActions;
import com.agimatec.validation.model.Features;
import com.agimatec.validation.model.MetaBean;
import com.agimatec.validation.model.MetaProperty;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.validation.*;
import javax.validation.groups.Default;
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
public class AnnotationMetaBeanFactory implements MetaBeanFactory {
    private static final Log log = LogFactory.getLog(AnnotationMetaBeanFactory.class);
    private static final String ANNOTATION_VALUE = "value";
    private static final String DEFAULT_CONSTAINTS =
          "com/agimatec/validation/jsr303/DefaultConstraints.properties";

    private final ConstraintValidatorFactory constraintFactory;
    protected Map<String, Class[]> defaultConstraints;


    public AnnotationMetaBeanFactory(ConstraintValidatorFactory constraintFactory) {
        this.constraintFactory = constraintFactory;
    }

    /**
     * add the validation features to the metabean that come from jsr303
     * annotations in the beanClass
     */
    public void buildMetaBean(MetaBean metabean) {
        try {
            final Class<?> beanClass = metabean.getBeanClass();
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
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e.getTargetException());
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
        processAnnotations(metabean, null, beanClass, beanClass, null);

        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            MetaProperty metaProperty = metabean.getProperty(field.getName());
            // create a property for those fields for which there is not yet a MetaProperty
            if (metaProperty == null) {
                metaProperty = new MetaProperty();
                metaProperty.setName(field.getName());
                metaProperty.setType(field.getType());
                if (processAnnotations(metabean, metaProperty, beanClass, field, null)) {
                    metabean.putProperty(metaProperty.getName(), metaProperty);
                }
            } else {
                processAnnotations(metabean, metaProperty, beanClass, field, null);
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
                    processAnnotations(metabean, metaProperty, beanClass, method, null);
                }
            }
        }
    }

    private boolean processAnnotations(MetaBean metabean, MetaProperty prop, Class owner,
                                       AnnotatedElement element,
                                       ConstraintValidation validation)
          throws IllegalAccessException, InvocationTargetException {
        boolean changed = false;
        for (Annotation annotation : element.getDeclaredAnnotations()) {
            changed |= processAnnotation(annotation, prop, metabean, owner, element,
                  validation);
        }
        return changed;
    }

    private boolean processAnnotation(Annotation annotation, MetaProperty prop,
                                      MetaBean metabean, Class owner,
                                      AnnotatedElement element,
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
            Class<? extends ConstraintValidator<?, ?>>[] validatorClasses;
            if (vcAnno == null) {
                validatorClasses = getDefaultConstraintValidator(annotation);
            } else {
                validatorClasses = vcAnno.validatedBy();
                if (validatorClasses == null || validatorClasses.length == 0) {
                    validatorClasses = getDefaultConstraintValidator(annotation);
                }
            }
            if (validatorClasses != null) {
                applyConstraint(annotation, validatorClasses, metabean, prop, owner,
                      element, validation);
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
                Object result =
                      SecureActions.getAnnotationValue(annotation, ANNOTATION_VALUE);
                if (result != null && result instanceof Annotation[]) {
                    for (Annotation each : (Annotation[]) result) {
                        processAnnotation(each, prop, metabean, owner, element,
                              validation);
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
        List<Group> groupSeq = metabean.getFeature(Jsr303Features.Bean.GROUP_SEQUENCE);
        if (groupSeq == null) {
            groupSeq = new ArrayList(annotation == null ? 1 : annotation.value().length);
            metabean.putFeature(Jsr303Features.Bean.GROUP_SEQUENCE, groupSeq);
        }
        if (annotation == null) {
            groupSeq.add(Group.DEFAULT);
        } else {
            boolean containsDefault = false;
            for (Class<?> groupClass : annotation.value()) {
                if (groupClass.getName().equals(beanClass.getName())) {
                    groupSeq.add(Group.DEFAULT);
                    containsDefault = true;
                } else if (groupClass.getName().equals(Default.class.getName())) {
                    throw new GroupDefinitionException(
                          "'Default.class' must not appear in @GroupSequence! Use '" +
                                beanClass.getSimpleName() + ".class' instead.");
                } else {
                    groupSeq.add(new Group(groupClass));
                }
            }
            if (!containsDefault) {
                throw new GroupDefinitionException(
                      "Redefined default group sequence must contain " +
                            beanClass.getName());
            }
            if (log.isDebugEnabled()) {
                log.debug("Default group sequence for bean " + beanClass.getName() +
                      " is: " + groupSeq);
            }
        }
    }

    /**
     * @param parentValidation - null or the parent validation when it is a composed validation
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void applyConstraint(Annotation annotation,
                                 Class<? extends ConstraintValidator>[] constraintClasses,
                                 MetaBean metabean, MetaProperty prop, Class owner,
                                 AnnotatedElement element,
                                 ConstraintValidation parentValidation)
          throws IllegalAccessException, InvocationTargetException {
        // The lifetime of a constraint validation implementation instance is undefined.
        for (Class constraintClass : constraintClasses) {
            ConstraintValidator constraint =
                  constraintFactory.getInstance(constraintClass);
            constraint.initialize(annotation);

            ConstraintValidation validation = new ConstraintValidation(
                  new ConstraintValidator[]{constraint}, annotation, owner, element);
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
            processAnnotations(metabean, prop, owner, annotation.annotationType(),
                  validation); // recursion!
        }
    }
}
