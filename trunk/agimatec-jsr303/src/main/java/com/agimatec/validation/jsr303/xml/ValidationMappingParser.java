/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.agimatec.validation.jsr303.xml;

import com.agimatec.validation.jsr303.AgimatecValidatorFactory;
import com.agimatec.validation.jsr303.ConstraintCached;
import com.agimatec.validation.jsr303.ConstraintDefaults;
import com.agimatec.validation.jsr303.ConstraintValidation;
import com.agimatec.validation.jsr303.util.ConverterUtils;
import com.agimatec.validation.jsr303.util.SecureActions;
import com.agimatec.validation.util.AccessStrategy;
import com.agimatec.validation.util.FieldAccess;
import com.agimatec.validation.util.MethodAccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.Payload;
import javax.validation.ValidationException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.*;


/**
 * Uses JAXB to parse constraints.xml based on validation-mapping-1.0.xsd.<br>
 * Copyright: Agimatec GmbH, 2009
 */
public class ValidationMappingParser {
    private static final Log log = LogFactory.getLog(ValidationMappingParser.class);
    private static final String VALIDATION_MAPPING_XSD = "META-INF/validation-mapping-1.0.xsd";
    private static final String[] RESERVED_PARAMS = {"message", "groups", "payload"};

    private final Set<Class> processedClasses;
    private final Map<Class<?>, List<Class<?>>> defaultSequences;
    private final Map<Class<?>, List<Member>> cascadedMembers;
    private final Map<Class<?>, List<MetaConstraint<?, ? extends Annotation>>> constraintMap;
    private final AgimatecValidatorFactory factory;

    public ValidationMappingParser(AgimatecValidatorFactory factory) {
        this.factory = factory;

        processedClasses = new HashSet<Class>();
        defaultSequences = new HashMap<Class<?>, List<Class<?>>>();
        cascadedMembers = new HashMap<Class<?>, List<Member>>();
        constraintMap = new HashMap<Class<?>, List<MetaConstraint<?, ? extends Annotation>>>();
    }

    /** @param xmlStreams One or more contraints.xml file streams to parse */
    public void processMappingConfig(Set<InputStream> xmlStreams) throws ValidationException {
        for (InputStream xmlStream : xmlStreams) {
            ConstraintMappingsType mapping = parseXmlMappings(xmlStream);

            String defaultPackage = mapping.getDefaultPackage();
            processConstraintDefinitions(mapping.getConstraintDefinition(), defaultPackage);
            for (BeanType bean : mapping.getBean()) {
                Class<?> beanClass = loadClass(bean.getClazz(), defaultPackage);
                if (!processedClasses.add(beanClass)) {
                    // spec: A given class must not be described more than once amongst all
                    //  the XML mapping descriptors.
                    throw new ValidationException(
                          beanClass.getName() + " has already be configured in xml.");
                }
                getAnnotationIgnores()
                      .setDefaultIgnoreAnnotation(beanClass, bean.isIgnoreAnnotations());
                processClassLevel(bean.getClassType(), beanClass, defaultPackage);
                processFieldLevel(bean.getField(), beanClass, defaultPackage);
                processPropertyLevel(bean.getGetter(), beanClass, defaultPackage);
                processedClasses.add(beanClass);
            }

        }
    }

    /** @param in XML stream to parse using the validation-mapping-1.0.xsd */
    private ConstraintMappingsType parseXmlMappings(InputStream in) {
        ConstraintMappingsType mappings;
        try {
            JAXBContext jc = JAXBContext.newInstance(ConstraintMappingsType.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            unmarshaller.setSchema(getSchema());
            StreamSource stream = new StreamSource(in);
            JAXBElement<ConstraintMappingsType> root =
                  unmarshaller.unmarshal(stream, ConstraintMappingsType.class);
            mappings = root.getValue();
        } catch (JAXBException e) {
            throw new ValidationException("Failed to parse XML deployment descriptor file.",
                  e);
        }
        return mappings;
    }

    /** @return validation-mapping-1.0.xsd based schema */
    private Schema getSchema() {
        return ValidationParser.getSchema(VALIDATION_MAPPING_XSD);
    }

    // TODO RSt -  finish....

    private void processClassLevel(ClassType classType, Class<?> beanClass,
                                   String defaultPackage) {
        /*
   When ignore-annotations is true, class-level Bean Validation annotations are ignored
   for this class (including the @GroupSequence).
   When ignore-annotations is false:
   •  Constraints declared in XML and constraints declared in annotations are added and form
      the list of class-level declared constraints.
   •  @GroupSequence is considered unless group-sequence element is explicitly used.
        */
        if (classType == null) {
            return;
        }

        // ignore annotation
        if (classType.isIgnoreAnnotations() != null) {
            getAnnotationIgnores()
                  .setIgnoreAnnotationsOnClass(beanClass, classType.isIgnoreAnnotations());
        }

        // group sequence
        List<Class<?>> groupSequence =
              createGroupSequence(classType.getGroupSequence(), defaultPackage);
        if (!groupSequence.isEmpty()) {
            defaultSequences.put(beanClass, groupSequence);
        }

        // constraints
        for (ConstraintType constraint : classType.getConstraint()) {
            MetaConstraint<?, ?> metaConstraint =
                  createConstraint(constraint, beanClass, null, defaultPackage);
            addMetaConstraint(beanClass, metaConstraint);
        }
    }

    private <A extends Annotation, T> MetaConstraint<?, ?> createConstraint(
          ConstraintType constraint, Class<T> beanClass, Member member,
          String defaultPackage) {
        Class<A> annotationClass =
              (Class<A>) loadClass(constraint.getAnnotation(), defaultPackage);
        MetaAnnotation<A> annotationDescriptor = new MetaAnnotation<A>(annotationClass);

        if (constraint.getMessage() != null) {
            annotationDescriptor.setMessage(constraint.getMessage());
        }
        annotationDescriptor
              .setGroups(getGroups(constraint.getGroups(), defaultPackage));
        annotationDescriptor
              .setPayload(getPayload(constraint.getPayload(), defaultPackage));

        for (ElementType elementType : constraint.getElement()) {
            String name = elementType.getName();
            checkNameIsValid(name);
            Class<?> returnType = getAnnotationParameterType(annotationClass, name);
            Object elementValue = getElementValue(elementType, returnType, defaultPackage);
            annotationDescriptor.putValue(name, elementValue);
        }

        A annotation = annotationDescriptor.createAnnotation();

        AccessStrategy access = null;
        if (member instanceof Method) {
            access = new MethodAccess((Method) member);
        } else if (member instanceof Field) {
            access = new FieldAccess((Field) member);
        }

        // TODO RSt - check parameters
        ConstraintValidation<A> constraintDescriptor =
              new ConstraintValidation<A>(null, null, annotation, beanClass, access, false);

        return new MetaConstraint<T, A>(beanClass, member, constraintDescriptor);
    }

    private void checkNameIsValid(String name) {
        for (String each : RESERVED_PARAMS) {
            if (each.equals(name)) {
                throw new ValidationException(each + " is a reserved parameter name.");
            }
        }
    }

    private <A extends Annotation> Class<?> getAnnotationParameterType(
          Class<A> annotationClass, String name) {
        Method m = SecureActions.getMethod(annotationClass, name);
        if (m == null) {
            throw new ValidationException("Annotation of type " + annotationClass.getName() +
                  " does not contain a parameter " + name + ".");
        }
        return m.getReturnType();
    }

    private Object getElementValue(ElementType elementType, Class<?> returnType,
                                   String defaultPackage) {
        removeEmptyContentElements(elementType);

        boolean isArray = returnType.isArray();
        if (!isArray) {
            if (elementType.getContent().size() != 1) {
                throw new ValidationException(
                      "Attempt to specify an array where single value is expected.");
            }
            return getSingleValue(elementType.getContent().get(0), returnType, defaultPackage);
        } else {
            List<Object> values = new ArrayList<Object>();
            for (Serializable s : elementType.getContent()) {
                values.add(getSingleValue(s, returnType.getComponentType(), defaultPackage));
            }
            return values.toArray(
                  (Object[]) Array.newInstance(returnType.getComponentType(), values.size()));
        }
    }

    private void removeEmptyContentElements(ElementType elementType) {
        List<Serializable> contentToDelete = new ArrayList<Serializable>();
        for (Serializable content : elementType.getContent()) {
            if (content instanceof String && ((String) content).matches("[\\n ].*")) {
                contentToDelete.add(content);
            }
        }
        elementType.getContent().removeAll(contentToDelete);
    }

    private Object getSingleValue(Serializable serializable, Class<?> returnType,
                                  String defaultPackage) {

        Object returnValue;
        if (serializable instanceof String) {
            String value = (String) serializable;
            returnValue = convertToResultType(returnType, value, defaultPackage);
        } else if (serializable instanceof JAXBElement &&
              ((JAXBElement) serializable).getDeclaredType()
                    .equals(String.class)) {
            JAXBElement<?> elem = (JAXBElement<?>) serializable;
            String value = (String) elem.getValue();
            returnValue = convertToResultType(returnType, value, defaultPackage);
        } else if (serializable instanceof JAXBElement &&
              ((JAXBElement) serializable).getDeclaredType()
                    .equals(AnnotationType.class)) {
            JAXBElement<?> elem = (JAXBElement<?>) serializable;
            AnnotationType annotationType = (AnnotationType) elem.getValue();
            try {
                Class<Annotation> annotationClass = (Class<Annotation>) returnType;
                returnValue =
                      createAnnotation(annotationType, annotationClass, defaultPackage);
            } catch (ClassCastException e) {
                throw new ValidationException("Unexpected parameter value");
            }
        } else {
            throw new ValidationException("Unexpected parameter value");
        }
        return returnValue;

    }

    private Object convertToResultType(Class<?> returnType, String value,
                                       String defaultPackage) {
        /**
         * Class is represented by the fully qualified class name of the class.
         * spec: Note that if the raw string is unqualified,
         * default package is taken into account.
         */
        if (returnType.equals(Class.class)) {
            value = toQualifiedClassName(value, defaultPackage);
        }
        return ConverterUtils.fromStringToType(value, returnType);
    }

    private <A extends Annotation> Annotation createAnnotation(AnnotationType annotationType,
                                                               Class<A> returnType,
                                                               String defaultPackage) {
        MetaAnnotation<A> metaAnnotation = new MetaAnnotation<A>(returnType);
        for (ElementType elementType : annotationType.getElement()) {
            String name = elementType.getName();
            Class<?> parameterType = getAnnotationParameterType(returnType, name);
            Object elementValue = getElementValue(elementType, parameterType, defaultPackage);
            metaAnnotation.putValue(name, elementValue);
        }
        return metaAnnotation.createAnnotation();
    }

    private Class<?>[] getGroups(GroupsType groupsType, String defaultPackage) {
        if (groupsType == null) {
            return new Class[]{};
        }

        List<Class<?>> groupList = new ArrayList<Class<?>>();
        for (JAXBElement<String> groupClass : groupsType.getValue()) {
            groupList.add(loadClass(groupClass.getValue(), defaultPackage));
        }
        return groupList.toArray(new Class[groupList.size()]);
    }


    private Class<? extends Payload>[] getPayload(PayloadType payloadType,
                                                  String defaultPackage) {
        if (payloadType == null) {
            return new Class[]{};
        }

        List<Class<? extends Payload>> payloadList = new ArrayList<Class<? extends Payload>>();
        for (JAXBElement<String> groupClass : payloadType.getValue()) {
            Class<?> payload = loadClass(groupClass.getValue(), defaultPackage);
            if (!Payload.class.isAssignableFrom(payload)) {
                throw new ValidationException("Specified payload class " + payload.getName() +
                      " does not implement javax.validation.Payload");
            } else {
                payloadList.add((Class<? extends Payload>) payload);
            }
        }
        return payloadList.toArray(new Class[payloadList.size()]);
    }

    private void addMetaConstraint(Class<?> beanClass, MetaConstraint<?, ?> metaConstraint) {
        if (constraintMap.containsKey(beanClass)) {
            constraintMap.get(beanClass).add(metaConstraint);
        } else {
            List<MetaConstraint<?, ? extends Annotation>> constraintList =
                  new ArrayList<MetaConstraint<?, ? extends Annotation>>();
            constraintList.add(metaConstraint);
            constraintMap.put(beanClass, constraintList);
        }
    }


    private List<Class<?>> createGroupSequence(GroupSequenceType groupSequenceType,
                                               String defaultPackage) {
        List<Class<?>> groupSequence = new ArrayList<Class<?>>();
        if (groupSequenceType != null) {
            for (JAXBElement<String> groupName : groupSequenceType.getValue()) {
                Class<?> group = loadClass(groupName.getValue(), defaultPackage);
                groupSequence.add(group);
            }
        }
        return groupSequence;
    }

    private void processFieldLevel(List<FieldType> fields, Class<?> beanClass,
                                   String defaultPackage) {
        /*
        If the name of the field does not correspond to a field in the given bean a ValidationException is raised.
         */

        /*
When ignore-annotations is true, field-level Bean Validation annotations on the targeted field are ignored
(including the @Valid). When ignore-annotations is false:
•    Constraints declared in XML and constraints declared in annotations are added and form the list of field-level
    declared constraints.
•    @Valid  is considered unless the valid element is explicitly used. Note that the only way to disable cascading on
    a field marked as @Valid is to use ignore-validation=true.
    */
        List<String> fieldNames = new ArrayList<String>();
        for (FieldType fieldType : fields) {
            String fieldName = fieldType.getName();
            if (fieldNames.contains(fieldName)) {
                throw new ValidationException(fieldName +
                      " is defined more than once in mapping xml for bean " +
                      beanClass.getName());
            } else {
                fieldNames.add(fieldName);
            }
            final Field field = SecureActions.getDeclaredField(beanClass, fieldName);
            if (field == null) {
                throw new ValidationException(
                      beanClass.getName() + " does not contain the fieldType  " + fieldName);
            }

            // ignore annotations
            boolean ignoreFieldAnnotation = fieldType.isIgnoreAnnotations() == null ? false :
                  fieldType.isIgnoreAnnotations();
            if (ignoreFieldAnnotation) {
                getAnnotationIgnores().setIgnoreAnnotationsOnMember(field);
            }

            // valid
            if (fieldType.getValid() != null) {
                addCascadedMember(beanClass, field);
            }

            // constraints
            for (ConstraintType constraintType : fieldType.getConstraint()) {
                MetaConstraint<?, ?> constraint =
                      createConstraint(constraintType, beanClass, field, defaultPackage);
                addMetaConstraint(beanClass, constraint);
            }
        }
    }


    private void addCascadedMember(Class<?> beanClass, Member member) {
        if (cascadedMembers.containsKey(beanClass)) {
            cascadedMembers.get(beanClass).add(member);
        } else {
            List<Member> tmpList = new ArrayList<Member>();
            tmpList.add(member);
            cascadedMembers.put(beanClass, tmpList);
        }
    }

    private void processPropertyLevel(List<GetterType> getters, Class<?> beanClass,
                                      String defaultPackage) {
        /*
        If the name of the property does not correspond to a property in the given bean a ValidationException is raised.
        */

        /*
When ignore-annotations is true, property-level Bean Validation annotations on the targeted property are ig-
nored (including the @Valid). When ignore-annotations is false:
•   Constraints declared in XML and constraints declared in annotations are added and form the list of property-
   level declared constraints.
•  @Valid is considered unless the valid element is explicitly used. Note that the only way to disable cascading on
    a property marked as @Valid is to use ignore-validation=true.
        */
        List<String> getterNames = new ArrayList<String>();
        for (GetterType getterType : getters) {
            String getterName = getterType.getName();
            if (getterNames.contains(getterName)) {
                throw new ValidationException(getterName +
                      " is defined more than once in mapping xml for bean " +
                      beanClass.getName());
            } else {
                getterNames.add(getterName);
            }
            final Method method = SecureActions.getGetter(beanClass, getterName);
            if (method == null) {
                throw new ValidationException(
                      beanClass.getName() + " does not contain the property  " + getterName);
            }

            // ignore annotations
            boolean ignoreGetterAnnotation = getterType.isIgnoreAnnotations() == null ? false :
                  getterType.isIgnoreAnnotations();
            if (ignoreGetterAnnotation) {
                getAnnotationIgnores().setIgnoreAnnotationsOnMember(method);
            }

            // valid
            if (getterType.getValid() != null) {
                addCascadedMember(beanClass, method);
            }

            // constraints
            for (ConstraintType constraintType : getterType.getConstraint()) {
                MetaConstraint<?, ?> metaConstraint =
                      createConstraint(constraintType, beanClass, method, defaultPackage);
                addMetaConstraint(beanClass, metaConstraint);
            }
        }
    }

    /*
Other custom elements of an annotation are represented by element. The name attribute is mandatory and repres-
ents the name of the element in the constraint declaration. “message”, “groups” are not permitted names, use the
message or groups elements instead. Otherwise a ValidationException is raised.
     */

    /*
If an XML constraint declaration is missing mandatory elements, or if it contains elements
not part of the constraint definition, a ValidationException is raised.
     */

    private void processConstraintDefinitions(
          List<ConstraintDefinitionType> constraintDefinitionList, String defaultPackage) {
        for (ConstraintDefinitionType constraintDefinition : constraintDefinitionList) {
            String annotationClassName = constraintDefinition.getAnnotation();

            Class<?> clazz = loadClass(annotationClassName, defaultPackage);
            if (!clazz.isAnnotation()) {
                throw new ValidationException(annotationClassName + " is not an annotation");
            }
            Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) clazz;

            ValidatedByType validatedByType = constraintDefinition.getValidatedBy();
            List<Class<? extends ConstraintValidator>> classes =
                  new ArrayList<Class<? extends ConstraintValidator>>();
            /*
             If include-existing-validator is set to false,
             ConstraintValidator defined on the constraint annotation are ignored.
              */
            if (validatedByType.isIncludeExistingValidators() != null &&
                  validatedByType.isIncludeExistingValidators()) {
                /*
                 If set to true, the list of ConstraintValidators described in XML
                 are concatenated to the list of ConstraintValidator described on the
                 annotation to form a new array of ConstraintValidator evaluated.
                 */
                classes
                      .addAll(findConstraintValidatorClasses(annotationClass));
            }
            for (JAXBElement<String> validatorClassName : validatedByType.getValue()) {
                Class<? extends ConstraintValidator<?, ?>> validatorClass;
                validatorClass = (Class<? extends ConstraintValidator<?, ?>>) SecureActions
                      .loadClass(validatorClassName.getValue(), this.getClass());


                if (!ConstraintValidator.class.isAssignableFrom(validatorClass)) {
                    throw new ValidationException(
                          validatorClass + " is not a constraint validator class");
                }

                /*
                Annotation based ConstraintValidator come before XML based
                ConstraintValidator in the array. The new list is returned
                by ConstraintDescriptor.getConstraintValidatorClasses().
                 */
                if (!classes.contains(validatorClass)) classes.add(validatorClass);
            }
            if (getConstraintsCache().containsConstraintValidator(annotationClass)) {
                throw new ValidationException("Constraint validator for " +
                      annotationClass.getName() + " already configured.");
            } else {
                getConstraintsCache().putConstraintValidator(annotationClass, classes);
            }
        }
    }

    private List<Class<? extends ConstraintValidator<? extends Annotation, ?>>> findConstraintValidatorClasses(
          Class<? extends Annotation> annotationType) {
        List<Class<? extends ConstraintValidator<? extends Annotation, ?>>> classes =
              new ArrayList<Class<? extends ConstraintValidator<? extends Annotation, ?>>>();

        Class<? extends ConstraintValidator<?, ?>>[] validator =
              getDefaultConstraints().getValidatorClasses(annotationType);
        if (validator != null) {
            classes
                  .addAll(Arrays.asList(validator));
        } else {
            Class<? extends ConstraintValidator<?, ?>>[] validatedBy = annotationType
                  .getAnnotation(Constraint.class)
                  .validatedBy();
            classes.addAll(Arrays.asList(validatedBy));
        }
        return classes;
    }

    private Class<?> loadClass(String className, String defaultPackage) {
        return SecureActions
              .loadClass(toQualifiedClassName(className, defaultPackage), this.getClass());
    }

    private String toQualifiedClassName(String className, String defaultPackage) {
        if (!isQualifiedClass(className)) {
            className = defaultPackage + "." + className;
        }
        return className;
    }

    private boolean isQualifiedClass(String clazz) {
        return clazz.contains(".");
    }

    private AnnotationIgnores getAnnotationIgnores() {
        return factory.getAnnotationIgnores();
    }

    private ConstraintDefaults getDefaultConstraints() {
        return factory.getDefaultConstraints();
    }

    private ConstraintCached getConstraintsCache() {
        return factory.getConstraintsCache();
    }
}
