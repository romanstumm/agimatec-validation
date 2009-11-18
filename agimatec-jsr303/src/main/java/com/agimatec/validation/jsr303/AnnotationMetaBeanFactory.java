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
import com.agimatec.validation.jsr303.util.TypeUtils;
import com.agimatec.validation.model.Features;
import com.agimatec.validation.model.MetaBean;
import com.agimatec.validation.model.MetaProperty;
import com.agimatec.validation.util.AccessStrategy;
import com.agimatec.validation.util.FieldAccess;
import com.agimatec.validation.util.MethodAccess;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.validation.*;
import javax.validation.groups.Default;
import java.beans.Introspector;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * Description: process the class annotations for constraint validations
 * to build the MetaBean<br/>
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 14:12:51 <br/>
 * Copyright: Agimatec GmbH 2008
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
            while (theClass != null && theClass != Object.class) {
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
        processAnnotations(metabean, null, beanClass, beanClass, null, null);

        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            MetaProperty metaProperty = metabean.getProperty(field.getName());
            // create a property for those fields for which there is not yet a MetaProperty
            if (metaProperty == null) {
                metaProperty = new MetaProperty();
                metaProperty.setName(field.getName());
                metaProperty.setType(field.getType());
                if (processAnnotations(metabean, metaProperty, beanClass, field,
                      new FieldAccess(field), null)) {
                    metabean.putProperty(metaProperty.getName(), metaProperty);
                }
            } else {
                processAnnotations(metabean, metaProperty, beanClass, field,
                      new FieldAccess(field), null);
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
                // create a property for those methods for which there is not yet a MetaProperty
                if (metaProperty == null) {
                    metaProperty = new MetaProperty();
                    metaProperty.setName(propName);
                    metaProperty.setType(method.getReturnType());
                    if (processAnnotations(metabean, metaProperty, beanClass, method,
                          new MethodAccess(method), null)) {
                        metabean.putProperty(propName, metaProperty);
                    }
                } else {
                    processAnnotations(metabean, metaProperty, beanClass, method,
                          new MethodAccess(method), null);
                }
            }
        }
    }

    private boolean processAnnotations(MetaBean metabean, MetaProperty prop, Class owner,
                                       AnnotatedElement element, AccessStrategy access,
                                       AnnotationConstraintBuilder validation)
          throws IllegalAccessException, InvocationTargetException {
        boolean changed = false;
        for (Annotation annotation : element.getDeclaredAnnotations()) {
            changed |= processAnnotation(annotation, prop, metabean, owner, access,
                  validation);
        }
        return changed;
    }

    private boolean processAnnotation(Annotation annotation, MetaProperty prop,
                                      MetaBean metabean, Class owner,
                                      AccessStrategy access,
                                      AnnotationConstraintBuilder validation)
          throws IllegalAccessException, InvocationTargetException {
        if (annotation instanceof Valid) {
            return processValid(prop, access);
        } else {
            /**
             * An annotation is considered a constraint definition if its retention
             * policy contains RUNTIME and if the annotation itself is annotated with
             * javax.validation.Constraint.
             */
            Constraint vcAnno =
                  annotation.annotationType().getAnnotation(Constraint.class);
            Class<? extends ConstraintValidator<?, ?>>[] validatorClasses;
            if (vcAnno != null) {
                validatorClasses = vcAnno.validatedBy();
                if (validatorClasses.length == 0) {
                    validatorClasses = getDefaultConstraintValidator(annotation);
                }
                return applyConstraint(annotation, validatorClasses, metabean, prop,
                      owner, access, validation);
            } else {
                /**
                 * Multi-valued constraints:
                 * To support this requirement, the bean validation provider treats
                 * regular annotations (annotations not annotated by @Constraint)
                 * whose value element has a return type of an array of
                 * constraint annotations in a special way.
                 */
                Object result =
                      SecureActions.getAnnotationValue(annotation, ANNOTATION_VALUE);
                if (result != null && result instanceof Annotation[]) {
                    boolean changed = false;
                    for (Annotation each : (Annotation[]) result) {
                        changed |= processAnnotation(each, prop, metabean, owner, access,
                              validation);
                    }
                    return changed;
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

    private boolean processValid(MetaProperty prop, AccessStrategy access) {
        if (prop != null/* && prop.getMetaBean() == null*/) {
            AccessStrategy[] strategies = prop.getFeature(Features.Property.REF_CASCADE);
            if (strategies == null) {
                strategies = new AccessStrategy[]{access};
                prop.putFeature(Features.Property.REF_CASCADE, strategies);
            } else {
                if (!ArrayUtils.contains(strategies, access)) {
                    AccessStrategy[] strategies_new =
                          new AccessStrategy[strategies.length + 1];
                    System.arraycopy(strategies, 0, strategies_new, 0, strategies.length);
                    strategies_new[strategies.length] = access;
                    prop.putFeature(Features.Property.REF_CASCADE, strategies_new);
                }
            }
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
    private boolean applyConstraint(Annotation annotation,
                                    Class<? extends ConstraintValidator<?, ?>>[] constraintClasses,
                                    MetaBean metabean, MetaProperty prop, Class owner,
                                    AccessStrategy access,
                                    AnnotationConstraintBuilder parentValidation)
          throws IllegalAccessException, InvocationTargetException {

        final ConstraintValidator validator;
        if (constraintClasses != null) {
            Type type = determineTargetedType(owner, access);
            /**
             * spec says in chapter 3.5.3.:
             * The ConstraintValidator chosen to validate a
             * declared type T is the one where the type supported by the
             * ConstraintValidator is a supertype of T and where
             * there is no other ConstraintValidator whose supported type is a
             * supertype of T and not a supertype of the chosen
             * ConstraintValidator supported type.
             */
            List<Type> assignableTypes = new ArrayList(constraintClasses.length);
            Map<Type, Class<? extends ConstraintValidator<?, ?>>> validatorTypes =
                  TypeUtils.getValidatorsTypes(constraintClasses);
            fillAssignableTypes(type, validatorTypes.keySet(), assignableTypes);
            reduceAssignableTypes(assignableTypes);
            checkOneType(annotation, owner, type, assignableTypes);
            validator = constraintFactory
                  .getInstance(validatorTypes.get(assignableTypes.get(0)));
            validator.initialize(annotation);
        } else {
            validator = null;
        }
        final AnnotationConstraintBuilder builder = new AnnotationConstraintBuilder(
              constraintClasses, validator, annotation, owner, access);
        // process composed constraints:
        // here are not other superclasses possible, because annotations do not inherit!
        if (processAnnotations(metabean, prop, owner, annotation.annotationType(), access,
              builder) || validator != null) {  // recursion!
            if (parentValidation == null) {
                if (prop != null) {
                    prop.addValidation(builder.getConstraintValidation());
                } else {
                    metabean.addValidation(builder.getConstraintValidation());
                }
            } else {
                parentValidation.addComposed(builder.getConstraintValidation());
            }
            return true;
        } else {
            return false;
        }
    }

    /** implements spec chapter 3.5.3. ConstraintValidator resolution algorithm. */
    private Type determineTargetedType(Class owner, AccessStrategy access) {
        // if the constraint declaration is hosted on a class or an interface,
        // the targeted type is the class or the interface.
        if (access == null) return owner;
        Type type = access.getJavaType();
        if (type == null) return Object.class;
        if (type instanceof Class) type = ClassUtils.primitiveToWrapper((Class) type);
        return type;
    }

    private void checkOneType(Annotation anno, Class owner, Type clazz,
                              Collection<Type> types) {
        if (types.isEmpty()) {
            throw new UnexpectedTypeException("No validator could be found for " +
                  typeName(clazz) + ". See: " + owner.getName() + " @" +
                  anno.annotationType().getSimpleName());
        } else if (types.size() > 1) {
            StringBuilder builder = new StringBuilder();
            builder.append("Ambiguous validators for ");
            builder.append(typeName(clazz));
            builder.append(". See: ").append(owner.getName()).append(" @")
                  .append(anno.annotationType().getSimpleName());
            builder.append(". Validators are: ");
            boolean comma = false;
            for (Type each : types) {
                if (comma) builder.append(", ");
                comma = true;
                builder.append(each);
            }
            throw new UnexpectedTypeException(builder.toString());
        }
    }

    private Object typeName(Type clazz) {
        if (clazz instanceof Class) {
            if(((Class)clazz).isArray()) {
                return ((Class)clazz).getComponentType().getName() + "[]";
            } else {
                return ((Class)clazz).getName();
            }
        } else {
            return clazz.toString();
        }
    }

    private void fillAssignableTypes(Type type, Set<Type> validatorsTypes,
                                     List<Type> suitableTypes) {
        for (Type validatorType : validatorsTypes) {
            if (TypeUtils.isAssignable(validatorType, type) &&
                  !suitableTypes.contains(validatorType)) {
                suitableTypes.add(validatorType);
            }
        }
    }

    /**
     * Tries to reduce all assignable classes down to a single class.
     *
     * @param assignableTypes The set of all classes which are assignable to the class of the value to be validated and
     *                        which are handled by at least one of the validators for the specified constraint.
     */
    private void reduceAssignableTypes(List<Type> assignableTypes) {
        if (assignableTypes.size() <= 1) {
            return; // no need to reduce
        }
        boolean removed;
        do {
            removed = false;
            final Type type = assignableTypes.get(0);
            for (int i = 1; i < assignableTypes.size(); i++) {
                Type nextType = assignableTypes.get(i);
                if (TypeUtils.isAssignable(type, nextType)) {
                    assignableTypes.remove(0);
                    i--;
                    removed = true;
                } else if (TypeUtils.isAssignable(nextType, type)) {
                    assignableTypes.remove(i--);
                    removed = true;
                }
            }
        } while (removed && assignableTypes.size() > 1);
    }
}
