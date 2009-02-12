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
    private Object[] enumConstants;

    public DynaTypeEnum(Class enumClass) {
        this.enumClass = enumClass;
    }

    public DynaTypeEnum(Class enumClass, Object... enumConstants) {
        this.enumClass = enumClass;
        this.enumConstants = enumConstants;
    }

    public void setEnumConstants(Object[] enumConstants) {
        this.enumConstants = enumConstants;
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
    public Object[] getEnumConstants() {
        if(enumConstants != null) {
            return enumConstants;
        } else {
            return enumClass.getEnumConstants();
        }
    }

    public boolean isAssignableFrom(Class cls) {
        return enumClass.isAssignableFrom(cls);
    }
}
