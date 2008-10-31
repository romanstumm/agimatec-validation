package com.agimatec.utility.validation.xml;

import java.io.IOException;

/**
 * Description: XMLMetaBeanLoader are used to know "locations" where to get BeanInfos from.<br/>
 * User: roman.stumm <br/>
 * Date: 05.07.2007 <br/>
 * Time: 16:21:51 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public interface XMLMetaBeanLoader {
    XMLMetaBeanInfos load() throws IOException;
}
