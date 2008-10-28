package com.agimatec.validation;

import com.agimatec.validation.example.BusinessObject;
import com.agimatec.validation.example.BusinessObjectAddress;
import com.agimatec.validation.json.JSONGenerator;
import com.agimatec.validation.model.MetaBean;
import com.agimatec.validation.model.MetaProperty;
import com.agimatec.validation.xml.XMLMetaBeanURLLoader;
import freemarker.template.TemplateException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MetaBeanManager Tester.
 *
 * @author ${USER}
 * @version 1.0
 * @since <pre>07/05/2007</pre>
 * Copyright: Agimatec GmbH 2008
 */
public class MetaBeanManagerTest extends TestCase {
    MetaBeanManager mbm = new MetaBeanManager();

    public MetaBeanManagerTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        mbm.getBuilder().addLoader(new XMLMetaBeanURLLoader(
                BusinessObject.class.getResource("test-beanInfos.xml")));
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testEnrichCopies() throws Exception {
        Map<String, MetaBean> copies = mbm.enrichCopies(new XMLMetaBeanURLLoader(
                BusinessObject.class.getResource("test-beanInfos-custom.xml")).load());
        assertNotNull(copies);
        MetaBean mb = copies.get(BusinessObject.class.getName());
        assertFalse(mb.getProperty("lastName").isMandatory());
        MetaBean mb2 = mbm.findForClass(BusinessObject.class);
        assertTrue(mb2.getProperty("lastName").isMandatory());
    }

    public void testCopy() {
        MetaBean mb = mbm.findForClass(BusinessObject.class);
        MetaBean mb2 = mb.copy();
        assertTrue(mb2 != mb);
        assertTrue(mb2.getProperty("dateBirth") != mb.getProperty("dateBirth"));
    }

    public void testFindForClass() throws Exception {
        MetaBeanFinder finder = mbm;
        MetaBean info = finder.findForClass(BusinessObject.class);
        assertNotNull(info);
        assertTrue(info == info.getProperty("address").getMetaBean().getProperty("owner")
                .getMetaBean());
        assertTrue(info == info.getProperty("addresses").getMetaBean()
                .getProperty("owner").getMetaBean());
        assertTrue(info.getProperty("email").getJavaScriptValidations().length > 0);
    }

    public void testBeanInfosCustomPatchGenerated()
            throws IOException, TemplateException {
        MetaBean mbean = mbm.findForClass(BusinessObject.class);
        MetaProperty mprop = mbean.getProperty("lastName");
        assertTrue(mprop.isMandatory());

        mbm.getCache().removeFromCache(mbean);
        mbm.getBuilder().addLoader(new XMLMetaBeanURLLoader(
                BusinessObject.class.getResource("test-beanInfos-custom.xml")));
        mbean = mbm.findForClass(BusinessObject.class);
        mprop = mbean.getProperty("lastName");
        assertTrue(!mprop.isMandatory());

        JSONGenerator converter = new JSONGenerator();

        List<MetaBean> metaBeans = new ArrayList(2);
        metaBeans.add(mbean);
        MetaBean mbean2 = mbm.findForId("UnknownObject");
        metaBeans.add(mbean2);
        String json = converter.toJSON(metaBeans);
        assertNotNull(json);
        System.out.println(json);
    }

    public void testFindAll() {
        Map<String, MetaBean> all = mbm.findAll();
        assertNotNull(all);
        Map<String, MetaBean> all2 = mbm.findAll();
        assertEquals(all.size(), all2.size());
        assertTrue(all.get(BusinessObject.class.getName()) ==
                all2.get(BusinessObject.class.getName()));
        assertTrue(all.get(BusinessObject.class.getName()) !=
                null);
        MetaBean bean = all.get(BusinessObject.class.getName());
        assertTrue(bean == bean.getProperty("address").getMetaBean().getProperty("owner")
                .getMetaBean());
        assertTrue(bean == bean.getProperty("addresses").getMetaBean()
                .getProperty("owner").getMetaBean());
    }

    public void testJSON() throws Exception {
        MetaBean info = mbm.findForClass(BusinessObject.class);
        MetaBean info2 = info.getProperty("address").getMetaBean();

        // empty default bean without xml backup
        MetaBean info3 = mbm.findForClass(BusinessObjectAddress.class);
        JSONGenerator converter = new JSONGenerator();

        List<MetaBean> metaBeans = new ArrayList(2);
        metaBeans.add(info);
        metaBeans.add(info2);
        metaBeans.add(info3);
        String json = converter.toJSON(metaBeans);
        assertNotNull(json);
        System.out.println(json);
    }

    public static Test suite() {
        return new TestSuite(MetaBeanManagerTest.class);
    }
}
