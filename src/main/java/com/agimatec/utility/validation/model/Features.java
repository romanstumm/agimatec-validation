package com.agimatec.utility.validation.model;

/**
 * Description: Contains key of common feature keys used by standard validators etc.
 * This DOES NOT MEAN that the list of property- or bean-features is closed. You can
 * put anything into the metabean as a feature and use it in your custom validators
 * and other classes that access your metabeans.<br/>
 * User: roman.stumm <br/>
 * Date: 05.07.2007 <br/>
 * Time: 17:03:47 <br/>
 * Copyright: Agimatec GmbH 2008
 * @see FeaturesCapable
 */
public interface Features {
    /** Features of {@link MetaBean} */
    public interface Bean {
        /** INFO: String, Name der Property, die Primary Key ist */
        String MAIN_KEY = "mainKey";
        /** INFO: category/domain to which the metaBean belongs to */
//        String DOMAIN = "domain";

//        String DISPLAY_NAME = "displayName";
        String UNIQUE_KEY = "uniqueKey";
    }

    /** Features of {@link MetaProperty} */
    public interface Property {
        /** INFO: possible Enum values */
        String ENUM = "enum";
        /** INFO: Boolean, TRUE falls Property ein Unique Key ist */
        String UNIQUE_KEY = "uniqueKey";
        /** VALIDATION: Boolean, Pflichtfeld? */
        String MANDATORY = "mandatory";
        /** VALIDATION: Integer, max. Anzahl Zeichen / Max. Kardinalitaet der Relation */
        String MAX_LENGTH = "maxLen";
        /** VALIDATION: Comparable (e.g. a subclass of Number), max Wert */
        String MAX_VALUE = "maxValue";
        /** VALIDATION: Integer, min. Anzahl Zeichen / Min. Kardinalitaet der Relation */
        String MIN_LENGTH = "minLen";
        /** VALIDATION: Comparable (e.g. a subclass of Number), min Wert */
        String MIN_VALUE = "minValue";
        /** INFO: String-Darstellung des Defaultvalues */
        String DEFAULT_VALUE = "defValue";
        /** SECURITY, INFO: Boolean, Wert oder Relation readonly? */
        String READONLY = "readonly";
        /**
         * SECURITY, INFO: Boolean, Feld erreichbar/erlaubt?
         * Wenn nein, dann darf das Feld weder angezeigt, abgefragt noch geaendert werden.
         */
        String DENIED = "denied";
        /** VALIDATION: String, Regulaerer Ausdruck zur Formatpruefung */
        String REG_EXP = "regExp";
        /**
         * VALIDATION: String, Constraint fuer Zeitangabe bei Date-feld:
         * {@link com.agimatec.utility.validation.xml.XMLMetaValue#TIMELAG_Past}
         * oder
         * {@link com.agimatec.utility.validation.xml.XMLMetaValue#TIMELAG_Future}
         */
        String TIME_LAG = "timeLag";

        /**
         * INFO: Boolean, Feld sichtbar?
         *
         * @see java.beans.PropertyDescriptor#isHidden()
         */
        String HIDDEN = "hidden";
        /**
         * INFO: Boolean
         *
         * @see java.beans.PropertyDescriptor#isPreferred()
         */
        String PREFERRED = "preferred";

        /** INFO: relationship's target metaBean.id * */
        String REF_BEAN_ID = "refBeanId";
        
        /**
         * INFO: Class<br>
         * Relationship's target metaBean.beanClass.
         * In case of to-many relationships, this feature
         * hold the Bean-type not the Collection-type.
         */
        String REF_BEAN_TYPE = "refBeanType";

        /**
         * INFO: Boolean<br>
         * true when validation should cascade into relationship target beans<br>
         * false when validation should NOT cascade into relationship target beans<br>
         *
         * Default: true, when MetaProperty.metaBean is != null
         */
        String REF_CASCADE = "refCascade";

        /** INFO: an array with the string names of custom java script validation functions */
        String JAVASCRIPT_VALIDATION_FUNCTIONS = "jsFunctions";
    }
}
