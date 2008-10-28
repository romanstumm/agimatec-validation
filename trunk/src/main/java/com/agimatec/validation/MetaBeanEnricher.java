package com.agimatec.validation;

import com.agimatec.validation.model.MetaBean;
import com.agimatec.validation.xml.XMLMetaBeanInfos;

import java.util.Map;

/**
 * Description: Interface to merge meta beans<br/>
 * User: roman.stumm <br/>
 * Date: 14.02.2008 <br/>
 * Time: 11:00:21 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public interface MetaBeanEnricher {

    /**
     * @param infos - the patches to apply
     * @return all MetaBeans for classes that have a xml descriptor and
     *         additional the MetaBeans loaded by the given loaders.
     *         The given loaders may also return patches for MetaBeans that have
     *         also been returned by other loaders. The beans with patches for
     *         references to patched beans will be copied.
     */
     Map<String, MetaBean> enrichCopies(XMLMetaBeanInfos... infos);
}
