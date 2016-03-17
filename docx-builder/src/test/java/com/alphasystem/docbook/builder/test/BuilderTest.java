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
import com.alphasystem.xml.UnmarshallerTool;
import org.docbook.model.*;
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
import static com.alphasystem.openxml.builder.wml.WmlAdapter.*;
import static java.lang.String.format;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * @author sali
 */
public class BuilderTest {

    private static final String DEFAULT_TITLE = "DefaultTitle";
    private static final String DATA_PATH = System.getProperty("data.path");
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
    private static void build(String dir, String fileNamePrefix) {
        final Path sourcePath = get(dir, format("%s.xml", fileNamePrefix));
        try {
            final DocumentContext context = DocumentBuilder.createContext(sourcePath);
            final WordprocessingMLPackage wmlPackage = DocumentBuilder.buildDocument(context);
            save(get(TARGET_PATH, format("%s.docx", fileNamePrefix)).toFile(), wmlPackage);
        } catch (SystemException | Docx4JException e) {
            fail(e.getMessage(), e);
        }
    }

    private static Object readXml(String name, Class<?> declaredType) {
        UnmarshallerTool unmarshallerTool = new UnmarshallerTool();
        final Path sourcePath = get(DATA_PATH, format("%s.xml", name));
        try {
            final String source = new String(readAllBytes(sourcePath));
            return unmarshallerTool.unmarshal(source, declaredType);
        } catch (Exception e) {
            fail(e.getMessage(), e);
        }
        return null;
    }

    private BuilderFactory builderFactory = BuilderFactory.getInstance();
    private WordprocessingMLPackage wmlPackage;
    private MainDocumentPart mainDocumentPart;

    @BeforeClass
    public void setup() {
        try {
            wmlPackage = new WmlPackageBuilder().getPackage();
            mainDocumentPart = wmlPackage.getMainDocumentPart();

            DocumentContext documentContext = new DocumentContext(new AsciiDocumentInfo(), new Article());
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

    private void addResult(Builder parent, Object o, int expectedSize, String title) {
        final List<Object> content = buildContent(parent, o);
        assertEquals(content.size(), expectedSize);
        addResult(title, content.toArray());
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

    @Test(groups = "inlineGroup")
    public void testBold() {
        final List<Object> content = buildContent(createBold("Bold Text"));
        assertEquals(content.size(), 1);
        final R r = (R) content.get(0);
        assertEquals(r.getClass().getName(), R.class.getName());
        addResult("Bold Test", r);
    }

    @Test(groups = "inlineGroup")
    public void testItalic() {
        final List<Object> content = buildContent(createItalic("Italic Text"));
        assertEquals(content.size(), 1);
        final R r = (R) content.get(0);
        assertEquals(r.getClass().getName(), R.class.getName());
        addResult("Italic Test", r);
    }

    @Test(groups = "inlineGroup")
    public void testTermBuilder() {
        final List<Object> content = buildContent(createTerm("Term Title"));
        assertEquals(content.size(), 1);
        final R r = (R) content.get(0);
        assertEquals(r.getClass().getName(), R.class.getName());
        addResult("Term Test", r);
    }

    @Test(groups = "titleGroup", dependsOnGroups = "inlineGroup")
    public void testDocumentTitle() {
        final Builder parent = builderFactory.getBuilder(null, new Article());
        final Title title = createTitle("Document Title");
        final List<Object> content = buildContent(parent, title);
        assertEquals(content.size(), 1);
        addResult("Document Title Test", content.toArray());
    }

    @Test(groups = "titleGroup", dependsOnGroups = "inlineGroup")
    public void testDocumentTitleWithCustomStyle() {
        final Builder parent = builderFactory.getBuilder(null, new Article());
        final Title title = createTitle("Document Title ", createPhrase("arabicLabel", "س ل م"));
        addResult(parent, title, 1, "Document Title with custom style Test");
    }

    @Test(groups = {"titleGroup"}, dependsOnGroups = {"inlineGroup"})
    public void testSectionLevel1Title() {
        final Builder parent = builderFactory.getBuilder(null, new Article());
        final Title title = createTitle("Section 1");
        addResult(parent, createSection("section-1", title), 1, "Section Level 1 Test");
    }

    @Test(groups = {"titleGroup"}, dependsOnGroups = {"inlineGroup"})
    public void testSectionLevel2Title() {
        final Builder p1 = builderFactory.getBuilder(null, new Article());
        final Builder p2 = builderFactory.getBuilder(p1, new Section());
        final Title title = createTitle("Section 2");
        addResult(p2, createSection("section-2", title), 1, "Section Level 2 Test");
    }

    @Test(groups = {"titleGroup"}, dependsOnGroups = {"inlineGroup"})
    public void testSectionLevel3Title() {
        final Builder p1 = builderFactory.getBuilder(null, new Article());
        final Builder p2 = builderFactory.getBuilder(p1, new Section());
        final Builder p3 = builderFactory.getBuilder(p2, new Section());
        final Title title = createTitle("Section 3");
        addResult(p3, createSection("section-3", title), 1, "Section Level 3 Test");
    }

    @Test(groups = {"titleGroup"}, dependsOnGroups = {"inlineGroup"})
    public void testSectionLevel4Title() {
        final Builder p1 = builderFactory.getBuilder(null, new Article());
        final Builder p2 = builderFactory.getBuilder(p1, new Section());
        final Builder p3 = builderFactory.getBuilder(p2, new Section());
        final Builder p4 = builderFactory.getBuilder(p3, new Section());
        final Title title = createTitle("Section 4");
        addResult(p4, createSection("section-4", title), 1, "Section Level 4 Test");
    }

    @Test(groups = {"titleGroup"}, dependsOnGroups = {"inlineGroup"})
    public void testSectionLevel5Title() {
        final Builder p1 = builderFactory.getBuilder(null, new Article());
        final Builder p2 = builderFactory.getBuilder(p1, new Section());
        final Builder p3 = builderFactory.getBuilder(p2, new Section());
        final Builder p4 = builderFactory.getBuilder(p3, new Section());
        final Builder p5 = builderFactory.getBuilder(p4, new Section());
        final Title title = createTitle("Section 5");
        addResult(p5, createSection("section-5", title), 1, "Section Level 5 Test");
    }

    @Test(dependsOnGroups = "titleGroup")
    public void testVariableListEntryBuilder() {
        final Term term = createTerm("Entry title");
        final SimplePara simplePara = createSimplePara(null, "This text is under simple para and it has to be indented using \"ListParagraph\" style without any numbering.");
        final ListItem listItem = createListItem(null, simplePara);
        addResult(null, createVariableListEntry(listItem, term), 2, "VariableListEntry Test");
    }

    @Test(dependsOnMethods = {"testVariableListEntryBuilder"})
    public void testVariableListBuilder() {
        final Object o = readXml("variablelist", VariableList.class);
        addResult(null, o, 12, "VariableList Test");
    }

}
