package com.zstu.utils;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileLockInterruptionException;

/**
 * @auther Stiles-JKY
 * @date 2021/2/24-21:25
 */
public abstract class XMLUtils {

    private final static SAXBuilder builder = new SAXBuilder();

    public static Element getRoot(String path) throws IOException , JDOMException {
        if (path != null) {
            return getRoot(new File(path));
        }
        throw new FileNotFoundException("path is null");
    }

    public static Element getRoot(File xml) throws IOException, JDOMException {
        if (!xml.canRead() || !xml.exists()) {
            throw new FileLockInterruptionException();
        }

        Document xmlDoc = builder.build(xml);
        return xmlDoc.getRootElement();
    }

}
