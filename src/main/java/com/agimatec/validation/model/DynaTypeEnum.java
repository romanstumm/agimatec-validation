package com.agimatec.validation.model;

/**
 * Description: ("artificial" enum with custom values)<br/>
 * User: roman <br/>
 * Date: 12.02.2009 <br/>
 * Time: 16:56:31 <br/>
 * Copyright: Agimatec GmbH
 */
public class DynaTypeEnum implements DynaType {
    private final Class enumClass;
    private Value[] enumConstants;

    public DynaTypeEnum(Class enumClass) {
        this.enumClass = enumClass;
    }

    public DynaTypeEnum(Class enumClass, String... names) {
        this.enumClass = enumClass;
        setEnumNames(names);
    }

    public void setEnumNames(String[] names) {
        enumConstants = new Value[names.length];
        int i=0;
        for(String each : names) {
            enumConstants[i++] = new Value(each);
        }
    }

    public String getName() {
        return enumClass.getName();
    }

    public Class getRawType() {
        return enumClass;
    }

    /**
     * used by freemarker-template "bean-infos-json.ftl"
     */
    public boolean isEnum() {
        return enumClass.isEnum();
    }

    /**
     * used by freemarker-template "bean-infos-json.ftl"
     */
    public Value[] getEnumConstants() {
        return enumConstants;
    }

    public boolean isAssignableFrom(Class cls) {
        return enumClass.isAssignableFrom(cls);
    }

    public static final class Value {
        final String name;

        Value(String name) {
            this.name = name;
        }

        public String name() {
            return name;
        }

    }
}
