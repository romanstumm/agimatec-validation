package com.agimatec.validation.json;

import com.agimatec.validation.model.MetaBean;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

/**
 * Description: Generate a JSON String for a collection of {@link MetaBean}s.
 * This implementation uses a freemarker template to generate the output.<br/>
 * User: roman.stumm <br/>
 * Date: 09.07.2007 <br/>
 * Time: 17:14:12 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public class JSONGenerator {
    private final Template template;

    public JSONGenerator() throws IOException {
        this("bean-infos-json.ftl");
    }

    public JSONGenerator(String templateName) throws IOException {
        Configuration freemarker = new Configuration();
        freemarker.setNumberFormat("0.######");  // prevent locale-sensitive number format
        freemarker.setClassForTemplateLoading(getClass(), "");
        template = freemarker.getTemplate(templateName);
    }

    public JSONGenerator(Template template) {
        this.template = template;
    }

    public String toJSON(MetaBean metaBean) throws IOException, TemplateException {
        List<MetaBean> metaBeans = new ArrayList(1);
        metaBeans.add(metaBean);
        return toJSON(metaBeans);
    }

    public String toJSON(Collection<MetaBean> metaBeans)
            throws IOException, TemplateException {
        final StringWriter out = new StringWriter();
        toJSON(metaBeans, out);
        return out.toString();
    }

    public void toJSON(Collection<MetaBean> metaBeans, Writer out)
            throws IOException, TemplateException {
        Map rootMap = new HashMap();
        rootMap.put("metaBeans", metaBeans);
        rootMap.put("generator", this);
        template.process(rootMap, out);
    }
}
