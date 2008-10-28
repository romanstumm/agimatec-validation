package com.agimatec.validation.xml;

/**
 * Description: Interface of the object that holds all XMLMetaBeanLoaders <br/>
 * User: roman.stumm <br/>
 * Date: 06.07.2007 <br/>
 * Time: 09:21:38 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public interface XMLMetaBeanRegistry {
    /**
     * add a loader for xml bean infos.
     * the registry should use the loader in the sequence they have been added. 
     * @param loader
     */
    void addLoader(XMLMetaBeanLoader loader);

    /**
     * convenience method to add a loader for a xml file in the classpath
     * @param resource - path of xml file in classpath
     */
    void addResourceLoader(String resource);
}
