package com.agimatec.validation.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 06.07.2007 <br/>
 * Time: 09:17:30 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public class XMLMetaBeanURLLoader implements XMLMetaBeanLoader {
    private final URL url;

    public XMLMetaBeanURLLoader(URL url) {
        if (url == null) throw new NullPointerException("URL required");
        this.url = url;
    }

    public XMLMetaBeanInfos load() throws IOException {
        InputStream stream = url.openStream();
        try {
            XMLMetaBeanInfos beanInfos = (XMLMetaBeanInfos) XMLMapper.getInstance()
                    .getXStream().fromXML(stream);
            beanInfos.setId(url.toExternalForm());
            return beanInfos;
        } finally {
            stream.close();
        }
    }
}
