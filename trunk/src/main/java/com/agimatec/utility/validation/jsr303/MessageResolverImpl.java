package com.agimatec.utility.validation.jsr303;

import org.apache.commons.lang.ObjectUtils;

import javax.validation.MessageResolver;
import javax.validation.ConstraintDescriptor;
import java.util.*;

/**
 * Description: This message resolver shall resolve message descriptors
 * into human-readable messages.
 * This class is threadsafe.<br/>
 * User: roman.stumm <br/>
 * Date: 02.04.2008 <br/>
 * Time: 17:21:51 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public class MessageResolverImpl implements MessageResolver {
    private static final String DEFAULT_BUNDLE_NAME =
            "com/agimatec/utility/validation/jsr303/messageResolver";
    private static final String DEFAULT_MESSAGE_BUNDLE_NAME = "ValidationMessages";

    private ResourceBundle defaultBundle;
    private ResourceBundle messageBundle;
    private Locale locale;
    private String messageBundleName;

    public MessageResolverImpl() {
        locale = Locale.getDefault();
        messageBundleName = DEFAULT_MESSAGE_BUNDLE_NAME;
    }

    /** Generate a display the message based on the constraint and its parameters. */
    public String interpolate(String message, ConstraintDescriptor constraintDescriptor,
                              Object value) {
        /**
         * The default message resolver begins by retrieving the message descriptor from the constraint.
         * Message parameters are then replaced by repeatedly executing the steps listed below
         * until no replacements are performed.
         * 1. A message parameter that matches the name of a property of the constraint declaration is
         * replaced by the value of that property.
         * <p/>
         * 2. The message parameter is used as a key to search the ResourceBundle name ValidationMessage
         * (often materialized as the property file /ValidationMessages.properties and
         * it's locale variations). If a property is found, the message parameter
         * is replaced with the property.
         * <p/>
         * 3. The message parameter is used as a key to search the default message resolver's built-in
         * message properties. If a property is found, the message parameter is replaced with the property.
         */
        return replace(message, constraintDescriptor);
    }

    /*
     * implementation based on
     * org.hibernate.validator.interpolator.DefaultMessageInterpolator#replace()
     */
    private String replace(String message, ConstraintDescriptor constraintDescriptor) {
        StringTokenizer tokens = new StringTokenizer(message, "#{}", true);
        StringBuilder buf = new StringBuilder(30);
        boolean escaped = false;
        boolean el = false;
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            if (!escaped && "#".equals(token)) {
                el = true;
            }
            if (!el && "{".equals(token)) {
                escaped = true;
            } else if (escaped && "}".equals(token)) {
                escaped = false;
            } else if (!escaped) {
                if ("{".equals(token)) el = false;
                buf.append(token);
            } else {
                Object variable = constraintDescriptor.getParameters().get(token);
                if (variable != null) {
                    buf.append(variable);
                } else {
                    String string = null;
                    try {
                        string = getMessageBundle() != null ? messageBundle.getString(token) : null;
                    } catch (MissingResourceException e) {
                        //give a second chance with the default resource bundle
                    }
                    if (string == null) {
                        try {
                            string = getDefaultBundle().getString(token);
                        } catch (MissingResourceException e) {
                            //return the unchanged string
                            buf.append('{').append(token).append('}');
                        }
                    }
                    if (string != null) buf.append(replace(string, constraintDescriptor));
                }
            }
        }
        return buf.toString();
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale aLocale) {
        if (!ObjectUtils.equals(aLocale, locale)) {
            messageBundle = null; // reset, new lookup on access (lazy)
            defaultBundle = null; // reset, new lookup on access (lazy)
        }
        this.locale = aLocale;
    }

    public String getMessageBundleName() {
        return messageBundleName;
    }

    public void setMessageBundleName(String aBundleName) {
        if (!messageBundleName.equals(aBundleName)) {
            messageBundle = null; // reset, new lookup on access (lazy)
        }
        this.messageBundleName = aBundleName;
    }

    /**
     * lazy lookup now
     *
     * @return default bundle
     */
    public ResourceBundle getDefaultBundle() {
        if (defaultBundle == null) {
            defaultBundle = ResourceBundle.getBundle(DEFAULT_BUNDLE_NAME, locale);
        }
        return defaultBundle;
    }

    /** @return the validationMessage-bundle, lazy lookup now */
    public ResourceBundle getMessageBundle() {
        if (messageBundle == null && messageBundleName != null) {
            try {
                messageBundle = ResourceBundle.getBundle(messageBundleName, locale);
            } catch (MissingResourceException ex) {
                messageBundle = new ListResourceBundle() {
                    protected Object[][] getContents() {
                        return new Object[0][];
                    }
                };
            }
        }
        return messageBundle;
    }
}
