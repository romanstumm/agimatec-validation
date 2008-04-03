package com.agimatec.utility.validation.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.io.Serializable;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 05.07.2007 <br/>
 * Time: 15:27:18 <br/>
 *
 */
@XStreamAlias("validator-ref")
public class XMLMetaValidatorReference implements Serializable {
    @XStreamAsAttribute
    private String refId;

    public XMLMetaValidatorReference(String id) {
        this.refId = id;
    }

    public XMLMetaValidatorReference() {
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }
}
