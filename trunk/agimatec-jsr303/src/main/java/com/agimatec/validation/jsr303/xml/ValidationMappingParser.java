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

import com.agimatec.validation.jsr303.AnnotationMetaBeanFactory;
import com.agimatec.validation.jsr303.util.SecureActions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ValidationException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import java.io.InputStream;
import java.lang.annotation.Annotation;
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

    private final AnnotationIgnores annotationIgnores;
    private final Set<Class> processedClasses;
    private final Map<Class<?>, List<Class<?>>> defaultSequences;
    private final Map<Class<?>, List<Member>> cascadedMembers;

    public ValidationMappingParser() {
        annotationIgnores = new AnnotationIgnores();
        processedClasses = new HashSet<Class>();
        defaultSequences = new HashMap<Class<?>, List<Class<?>>>();
        cascadedMembers = new HashMap<Class<?>, List<Member>>();
    }

    /** @param xmlStreams One or more contraints.xml file streams to parse */
    public void processMappingConfig(Set<InputStream> xmlStreams) throws ValidationException {
        Iterator<InputStream> streams = xmlStreams.iterator();
        while (streams.hasNext()) {
            InputStream in = streams.next();
            ConstraintMappingsType mapping = parseMappingStream(in);
            streams.remove(); // remove all that are done!

            String defaultPackage = mapping.getDefaultPackage();
            processConstraintDefinitions(mapping.getConstraintDefinition(), defaultPackage);
            for (BeanType bean : mapping.getBean()) {
                Class<?> beanClass = loadClass(bean.getClazz(), defaultPackage);
                if (processedClasses.add(beanClass)) {

                } else {
                    // spec: A given class must not be described more than once amongst all
                    //  the XML mapping descriptors.
                    throw new ValidationException(
                          beanClass.getName() + " has already be configured in xml.");
                }
                annotationIgnores
                      .setDefaultIgnoreAnnotation(beanClass, bean.isIgnoreAnnotations());
                processClassLevel(bean.getClassType(), beanClass, defaultPackage);
                processFieldLevel(bean.getField(), beanClass, defaultPackage);
                processPropertyLevel(bean.getGetter(), beanClass, defaultPackage);
                processedClasses.add(beanClass);
            }

        }
    }

    /** @param in XML stream to parse using the validation-mapping-1.0.xsd */
    private ConstraintMappingsType parseMappingStream(InputStream in) {
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
            annotationIgnores
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
//			Constraint<?, ?> cnstraint = createConstraint( constraint, beanClass, null, defaultPackage );
//			addConstraint( beanClass, metaConstraint );
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
                      " is defined more than once in mapping xml for bean " + beanClass.getName());
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
                annotationIgnores.setIgnoreAnnotationsOnMember(field);
            }

            // valid
            if (fieldType.getValid() != null) {
                addCascadedMember(beanClass, field);
            }

            // constraints
            for (ConstraintType constraint : fieldType.getConstraint()) {
//                Constraint<?, ?> constraint =
//                      createConstraint(constraint, beanClass, field, defaultPackage);
//                addConstraint(beanClass, metaConstraint);
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
                      " is defined more than once in mapping xml for bean " + beanClass.getName());
            } else {
                getterNames.add(getterName);
            }
            final Method method = SecureActions.getMethod(beanClass, getterName);
            if (method == null) {
                throw new ValidationException(
                      beanClass.getName() + " does not contain the property  " + getterName);
            }

            // ignore annotations
            boolean ignoreGetterAnnotation = getterType.isIgnoreAnnotations() == null ? false :
                  getterType.isIgnoreAnnotations();
            if (ignoreGetterAnnotation) {
                annotationIgnores.setIgnoreAnnotationsOnMember(method);
            }

            // valid
            if (getterType.getValid() != null) {
                addCascadedMember(beanClass, method);
            }

            // constraints
            for (ConstraintType constraint : getterType.getConstraint()) {
//                Constraint<?, ?> metaConstraint =
//                      createConstraint(constraint, beanClass, method, defaultPackage);
//                addConstraint(beanClass, metaConstraint);
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

    /*
If include-existing-validator is set to false, ConstraintValidator defined on the constraint annotation are ig-
nored. If set to true, the list of ConstraintValidators described in XML are concatenated to the list of Con-
straintValidator described on the annotation to form a new array of ConstraintValidator evaluated. Annota-
tion based ConstraintValidator come before XML based ConstraintValidatot in the array. The new list is re-
turned by ConstraintDescriptor.getConstraintValidatorClasses().
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
            List<Class<? extends ConstraintValidator<? extends Annotation, ?>>> classes =
                  new ArrayList<Class<? extends ConstraintValidator<? extends Annotation, ?>>>();
            if (validatedByType.isIncludeExistingValidators() != null &&
                  validatedByType.isIncludeExistingValidators()) {
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

                classes.add(validatorClass);
            }
            /*constraintHelper.addConstraintValidatorDefinition(annotationClass,
                  classes);*/
        }
    }

    private List<Class<? extends ConstraintValidator<? extends Annotation, ?>>> findConstraintValidatorClasses(
          Class<? extends Annotation> annotationType) {
        List<Class<? extends ConstraintValidator<? extends Annotation, ?>>> classes =
              new ArrayList<Class<? extends ConstraintValidator<? extends Annotation, ?>>>();

        Class<? extends ConstraintValidator<?, ?>>[] validator = AnnotationMetaBeanFactory
              .getDefaultConstraints().getValidatorClasses(annotationType);
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
        if (!isQualifiedClass(className)) {
            className = defaultPackage + "." + className;
        }
        return SecureActions.loadClass(className, this.getClass());
    }

    private boolean isQualifiedClass(String clazz) {
        return clazz.contains(".");
    }

}
