package com.alphasystem.docbook;

import java.awt.*;
import java.nio.file.Path;

import static java.nio.file.Paths.get;

/**
 * @author sali
 */
public class DocBookTest {

    public static void main(String[] args) {

        String path = "C:\\Users\\sali\\ascii_doc\\test2.xml";

        path = "C:\\Users\\sali\\Arabic\\documents\\Lesson-001-01.adoc";
        path = "C:\\tools\\asciidoc-8.6.9\\asciidoctor.org-master\\docs\\asciidoc-writers-guide.adoc";

        try {
            final Path destPath = DocumentBuilder.buildDocument(get(path));
            Desktop.getDesktop().open(destPath.toFile());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
