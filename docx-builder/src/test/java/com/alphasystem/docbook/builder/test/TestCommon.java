package com.alphasystem.docbook.builder.test;

import com.alphasystem.SystemException;
import com.alphasystem.docbook.DocumentBuilder;
import com.alphasystem.docbook.DocumentContext;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.alphasystem.openxml.builder.wml.WmlAdapter.save;
import static java.lang.String.format;
import static java.nio.file.Paths.get;
import static org.testng.Assert.fail;

/**
 * @author sali
 */
public abstract class TestCommon {

    protected static final String TARGET_PATH = System.getProperty("target.path");
    protected static final String DATA_PATH = System.getProperty("data.path");

    static {
        final Path path = get(TARGET_PATH);
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path).toString();
            } catch (IOException e) {
                fail(e.getMessage(), e);
            }
        }
    }

    protected void build() {
        final Path sourcePath = get(DATA_PATH, format("%s.xml", getClass().getSimpleName()));
        try {
            final DocumentContext context = DocumentBuilder.createContext(sourcePath);
            final WordprocessingMLPackage wmlPackage = DocumentBuilder.buildDocument(context);
            save(get(TARGET_PATH, format("%s.docx", getClass().getSimpleName())).toFile(), wmlPackage);
        } catch (SystemException | Docx4JException e) {
            fail(e.getMessage(), e);
        }
    }

}
