package com.alphasystem.docbook.builder.test;

import com.alphasystem.SystemException;
import com.alphasystem.asciidoc.model.AsciiDocumentInfo;
import com.alphasystem.docbook.ApplicationController;
import com.alphasystem.docbook.DocumentBuilder;
import com.alphasystem.docbook.DocumentContext;
import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.BuilderFactory;
import com.alphasystem.openxml.builder.wml.PBuilder;
import com.alphasystem.openxml.builder.wml.WmlBuilderFactory;
import com.alphasystem.openxml.builder.wml.WmlPackageBuilder;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Style;
import org.docx4j.wml.Styles;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.alphasystem.docbook.builder.test.DataFactory.*;
import static com.alphasystem.docbook.builder.test.StaticDataFactory.VARIABLE_LIST_ENTRIES;
import static com.alphasystem.openxml.builder.wml.WmlAdapter.*;
import static java.lang.String.format;
import static java.nio.file.Paths.get;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * @author sali
 */
public class BuilderTest {

    private static final String DEFAULT_TITLE = "DefaultTitle";
    private static final String TARGET_PATH = System.getProperty("target.path");

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

    @SuppressWarnings({"unused"})
    private void build(String dir, String fileNamePrefix) {
        final Path sourcePath = get(dir, format("%s.xml", fileNamePrefix));
        try {
            final DocumentContext context = DocumentBuilder.createContext(sourcePath);
            final WordprocessingMLPackage wmlPackage = DocumentBuilder.buildDocument(context);
            save(get(TARGET_PATH, format("%s.docx", fileNamePrefix)).toFile(), wmlPackage);
        } catch (SystemException | Docx4JException e) {
            fail(e.getMessage(), e);
        }
    }

    private BuilderFactory builderFactory = BuilderFactory.getInstance();
    private WordprocessingMLPackage wmlPackage;
    private MainDocumentPart mainDocumentPart;

    @BeforeClass
    public void setup() {
        try {
            wmlPackage = new WmlPackageBuilder().getPackage();
            mainDocumentPart = wmlPackage.getMainDocumentPart();

            DocumentContext documentContext = new DocumentContext(new AsciiDocumentInfo(), createEmptyArticle());
            documentContext.setMainDocumentPart(mainDocumentPart);

            final StyleDefinitionsPart styleDefinitionsPart = mainDocumentPart.getStyleDefinitionsPart();
            final Styles styles = styleDefinitionsPart.getContents();
            final List<Style> list = styles.getStyle();
            list.forEach(style -> documentContext.getDocumentStyles().add(style.getStyleId()));

            ApplicationController.startContext(documentContext);
        } catch (Exception e) {
            fail(e.getMessage(), e);
        }
    }

    private P buildPara(R... runs) {
        final PBuilder pBuilder = WmlBuilderFactory.getPBuilder();
        for (R run : runs) {
            pBuilder.addContent(run);
        }
        return pBuilder.getObject();
    }

    private void addResult(String title, Object... content) {
        mainDocumentPart.addObject(getParagraphWithStyle(DEFAULT_TITLE, title));
        for (Object o : content) {
            mainDocumentPart.addObject(o);
        }
        mainDocumentPart.addObject(getHorizontalLine());
    }

    private void addResult(String title, R... runs) {
        addResult(title, buildPara(runs));
    }

    @SuppressWarnings("unchecked")
    private List<Object> buildContent(Builder parent, Object o) {
        final Builder builder = builderFactory.getBuilder(parent, o);
        return builder.buildContent();
    }

    private List<Object> buildContent(Object o) {
        return buildContent(null, o);
    }

    @AfterClass
    public void tearDown() {
        ApplicationController.endContext();
        try {
            final File file = get(TARGET_PATH, "builder.docx").toFile();
            save(file, wmlPackage);
            Desktop.getDesktop().open(file);
        } catch (Docx4JException e) {
            fail(e.getMessage(), e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBold() {
        final List<Object> content = buildContent(createBold("Bold Text"));
        assertEquals(content.size(), 1);
        final R r = (R) content.get(0);
        assertEquals(r.getClass().getName(), R.class.getName());
        addResult("Bold Test", r);
    }

    @Test
    public void testItalic() {
        final List<Object> content = buildContent(createItalic("Italic Text"));
        assertEquals(content.size(), 1);
        final R r = (R) content.get(0);
        assertEquals(r.getClass().getName(), R.class.getName());
        addResult("Italic Test", r);
    }

    @Test(dependsOnMethods = {"testItalic"})
    public void testTermBuilder() {
        final List<Object> content = buildContent(createTerm("Term Title"));
        assertEquals(content.size(), 1);
        final R r = (R) content.get(0);
        assertEquals(r.getClass().getName(), R.class.getName());
        addResult("Term Test", r);
    }

    @Test(dependsOnMethods = {"testTermBuilder"})
    public void testVariableListEntryBuilder() {
        final List<Object> content = buildContent(null, VARIABLE_LIST_ENTRIES[0]);
        assertEquals(content.size(), 2);
        addResult("VariableListEntry Test", content.toArray());
    }

    @Test
    public void testVariableListEntryBuilderWithSimplePara() {
        final List<Object> content = buildContent(null, VARIABLE_LIST_ENTRIES[1]);
        assertEquals(content.size(), 2);
        addResult("VariableListEntry with SimplePara Test", content.toArray());
    }

    @Test(dependsOnMethods = {"testVariableListEntryBuilderWithSimplePara"})
    public void testVariableList() {
        final List<Object> content = buildContent(null, createVariableList(null, null, VARIABLE_LIST_ENTRIES));
        assertEquals(content.size(), VARIABLE_LIST_ENTRIES.length * 2);
        addResult("VariableList Test", content.toArray());
    }

}
