package com.alphasystem.docbook;

import com.alphasystem.xml.DocumentInfo;
import org.docbook.model.Article;

import static java.lang.String.format;
import static java.lang.System.out;

/**
 * @author sali
 */
public class DocBookTest {

    public static void main(String[] args) {
        // initialize Application controller
        ApplicationController.getInstance();
        final long overallTime = System.currentTimeMillis();

        String dir = "C:\\Users\\sali\\ascii_doc";
        String fileNamePrefix = "test2";

        dir = "C:\\Users\\sali\\Arabic\\documents\\";
        fileNamePrefix = "Lesson-001-01";

        dir="C:\\tools\\asciidoc-8.6.9\\asciidoctor.org-master\\docs";
        fileNamePrefix = "asciidoc-writers-guide";
        try {
            final DocumentInfo<Article> documentInfo = DocBookUtil.getArticle(format("%s\\%s.xml", dir, fileNamePrefix));
            final long finishTime = System.currentTimeMillis();
            out.println(format("Total time taken to parse: %s.", (finishTime - overallTime)));
            DocumentBuilder documentBuilder = new DocumentBuilder();
            documentBuilder.buildDocument(documentInfo, format("%s\\%s.docx", dir, fileNamePrefix));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            final long finishTime = System.currentTimeMillis();
            out.println(format("Total time taken: %s\nDone.", (finishTime - overallTime)));
        }

    }
}
