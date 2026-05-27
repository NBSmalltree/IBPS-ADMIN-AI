package com.actuary.ibps.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

public final class XmlValidator {

    private static final Logger log = LoggerFactory.getLogger(XmlValidator.class);

    private XmlValidator() {
    }

    public static boolean isWellFormed(String xml) {
        if (xml == null || xml.trim().isEmpty()) {
            return false;
        }
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://xml.org/sax/features/validation", false);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.parse(new InputSource(new StringReader(xml)));
            return true;
        } catch (Exception e) {
            log.debug("XML validation failed: {}", e.getMessage());
            return false;
        }
    }
}
