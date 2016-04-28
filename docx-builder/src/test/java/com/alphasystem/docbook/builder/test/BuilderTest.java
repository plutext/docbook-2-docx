package com.alphasystem.docbook.builder.test;

import com.alphasystem.SystemException;
import com.alphasystem.docbook.ApplicationController;
import com.alphasystem.docbook.DocumentBuilder;
import com.alphasystem.docbook.DocumentContext;
import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.BuilderFactory;
import com.alphasystem.openxml.builder.wml.PBuilder;
import com.alphasystem.openxml.builder.wml.WmlBuilderFactory;
import com.alphasystem.openxml.builder.wml.WmlPackageBuilder;
import com.alphasystem.xml.UnmarshallerTool;
import org.apache.commons.lang3.ArrayUtils;
import org.docbook.model.*;
import org.docbook.model.Choice;
import org.docbook.model.Frame;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Style;
import org.docx4j.wml.Styles;
import org.testng.annotations.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.alphasystem.docbook.ApplicationController.DEFAULT_TEMPLATE;
import static com.alphasystem.docbook.ApplicationController.STYLES_PATH;
import static com.alphasystem.docbook.builder.model.DocumentCaption.EXAMPLE;
import static com.alphasystem.docbook.builder.model.DocumentCaption.TABLE;
import static com.alphasystem.docbook.builder.test.DataFactory.*;
import static com.alphasystem.openxml.builder.wml.WmlAdapter.*;
import static com.alphasystem.util.IdGenerator.nextId;
import static java.lang.String.format;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import static org.docbook.model.Align.CENTER;
import static org.docbook.model.Align.LEFT;
import static org.docbook.model.BasicVerticalAlign.MIDDLE;
import static org.docbook.model.BasicVerticalAlign.TOP;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;
import static org.testng.Reporter.log;

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
            UnmarshallerTool unmarshallerTool = new UnmarshallerTool();
            DocumentContext documentContext = new DocumentContext(unmarshallerTool.getDocumentInfo(), new Article());
            ApplicationController.startContext(documentContext);

            final WmlPackageBuilder wmlPackageBuilder = new WmlPackageBuilder(DEFAULT_TEMPLATE)
                    .styles(STYLES_PATH).multiLevelHeading(EXAMPLE).multiLevelHeading(TABLE);

            final StyleDefinitionsPart styleDefinitionsPart = wmlPackageBuilder.getPackage().getMainDocumentPart().getStyleDefinitionsPart();
            final Styles styles = styleDefinitionsPart.getContents();
            final List<Style> list = styles.getStyle();
            list.forEach(style -> documentContext.getDocumentStyles().add(style.getStyleId()));
            wmlPackage = wmlPackageBuilder.getPackage();
            mainDocumentPart = wmlPackage.getMainDocumentPart();
            documentContext.setMainDocumentPart(mainDocumentPart);
        } catch (Exception e) {
            fail(e.getMessage(), e);
        }
    }

    @BeforeMethod
    public void startTest(Method method) {
        log("-----------------------------------------------------------------------------------------", true);
        log(format("Stating test \"%s\".", method.getName()), true);
    }

    @AfterMethod
    public void endTest(Method method) {
        log(format("Test \"%s\" end.", method.getName()), true);
        log("-----------------------------------------------------------------------------------------", true);
    }

    private P buildPara(R... runs) {
        final PBuilder pBuilder = WmlBuilderFactory.getPBuilder();
        for (R run : runs) {
            pBuilder.addContent(run);
        }
        return pBuilder.getObject();
    }

    private void addResultToDocument(String title, Object... content) {
        mainDocumentPart.addObject(getParagraphWithStyle(DEFAULT_TITLE, title));
        for (Object o : content) {
            mainDocumentPart.addObject(o);
        }
        mainDocumentPart.addObject(getHorizontalLine());
    }

    private void addResult(Builder parent, int indexInParent, int expectedSize, String title, Object... content) {
        final List<Object> childContent = buildContent(parent, indexInParent, content);
        assertEquals(childContent.size(), expectedSize);
        addResultToDocument(title, childContent.toArray());
    }

    private void addResult(String title, R... runs) {
        addResultToDocument(title, buildPara(runs));
    }

    @SuppressWarnings("unchecked")
    private List<Object> buildContent(Builder parent, int indexInParent, Object... objects) {
        List<Object> content = new ArrayList<>();
        if (!ArrayUtils.isEmpty(objects)) {
            log("**************************************************************************", true);
            for (Object o : objects) {
                final Builder builder = builderFactory.getBuilder(parent, o, indexInParent);
                final List c = builder.buildContent();
                log(format("Getting builder \"%s\" for \"%s\", number of child content are \"%s\".", builder.getClass().getName(),
                        o.getClass().getName(), c.size()));
                content.addAll(c);
            }
            log("**************************************************************************", true);
        }

        return content;
    }

    private R[] convertToRuns(List<Object> content) {
        R[] runs = new R[content.size()];
        for (int i = 0; i < content.size(); i++) {
            runs[i] = (R) content.get(i);
        }
        return runs;
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
        final List<Object> content = buildContent(null, -1, createBold("Bold Text"));
        assertEquals(content.size(), 1);
        final R r = (R) content.get(0);
        assertEquals(r.getClass().getName(), R.class.getName());
        addResult("Bold Test", r);
    }

    @Test(groups = "inlineGroup")
    public void testItalic() {
        final List<Object> content = buildContent(null, -1, createItalic("Italic Text"));
        assertEquals(content.size(), 1);
        final R r = (R) content.get(0);
        assertEquals(r.getClass().getName(), R.class.getName());
        addResult("Italic Test", r);
    }

    @Test(groups = "inlineGroup")
    public void testTermBuilder() {
        final List<Object> content = buildContent(null, -1, createTerm("Term Title"));
        assertEquals(content.size(), 1);
        final R r = (R) content.get(0);
        assertEquals(r.getClass().getName(), R.class.getName());
        addResult("Term Test", r);
    }

    @Test(groups = "inlineGroup")
    public void testSubscript() {
        final Literal literal = createLiteral(null, "H", createSubscript(null, "2"), "O");
        final Phrase phrase = createPhrase("bold", literal);
        final List<Object> content = buildContent(null, -1, "Chemical formula for water is ", phrase);
        assertEquals(content.size(), 4);
        addResult("Subscript Test", convertToRuns(content));
    }

    @Test(groups = "inlineGroup")
    public void testSuperscript() {
        final Literal literal = createLiteral(null, "E = mc", createSuperscript(null, "2"));
        final Phrase phrase = createPhrase("bold", literal);
        final List<Object> content = buildContent(null, -1, "Einstein's theory of relativity is ", phrase);
        assertEquals(content.size(), 3);
        addResult("Superscript Test", convertToRuns(content));
    }

    @Test(groups = "titleGroup", dependsOnGroups = "inlineGroup")
    public void testDocumentTitle() {
        final Builder parent = builderFactory.getBuilder(null, new Article(), -1);
        final Title title = createTitle("Document Title");
        final List<Object> content = buildContent(parent, 0, title);
        assertEquals(content.size(), 1);
        addResultToDocument("Document Title Test", content.toArray());
    }

    @Test(groups = "titleGroup", dependsOnGroups = "inlineGroup")
    public void testDocumentTitleWithCustomStyle() {
        final Builder parent = builderFactory.getBuilder(null, new Article(), -1);
        final Title title = createTitle("Document Title ", createPhrase("arabicLabel", "س ل م"));
        addResult(parent, 0, 1, "Document Title with custom style Test", title);
    }

    @Test(groups = "titleGroup", dependsOnMethods = {"testDocumentTitleWithCustomStyle"}, dependsOnGroups = "inlineGroup")
    public void testExampleTitle() {
        final Builder parent = builderFactory.getBuilder(null, new Example(), -1);
        addResult(parent, 0, 3, "Example Title Test", createExample("Example Title"));
    }

    @Test(groups = {"titleGroup"}, dependsOnGroups = {"inlineGroup"})
    public void testSectionLevel1Title() {
        final Builder parent = builderFactory.getBuilder(null, new Article(), -1);
        final Title title = createTitle("Section 1");
        addResult(parent, 0, 1, "Section Level 1 Test", createSection("section-1", title));
    }

    @Test(groups = {"titleGroup"}, dependsOnGroups = {"inlineGroup"})
    public void testSectionLevel2Title() {
        final Builder p1 = builderFactory.getBuilder(null, new Article(), -1);
        final Builder p2 = builderFactory.getBuilder(p1, new Section(), 0);
        final Title title = createTitle("Section 2");
        addResult(p2, 0, 1, "Section Level 2 Test", createSection("section-2", title));
    }

    @Test(groups = {"titleGroup"}, dependsOnGroups = {"inlineGroup"})
    public void testSectionLevel3Title() {
        final Builder p1 = builderFactory.getBuilder(null, new Article(), -1);
        final Builder p2 = builderFactory.getBuilder(p1, new Section(), 0);
        final Builder p3 = builderFactory.getBuilder(p2, new Section(), 0);
        final Title title = createTitle("Section 3");
        addResult(p3, 0, 1, "Section Level 3 Test", createSection("section-3", title));
    }

    @Test(groups = {"titleGroup"}, dependsOnGroups = {"inlineGroup"})
    public void testSectionLevel4Title() {
        final Builder p1 = builderFactory.getBuilder(null, new Article(), -1);
        final Builder p2 = builderFactory.getBuilder(p1, new Section(), 0);
        final Builder p3 = builderFactory.getBuilder(p2, new Section(), 0);
        final Builder p4 = builderFactory.getBuilder(p3, new Section(), 0);
        final Title title = createTitle("Section 4");
        addResult(p4, 0, 1, "Section Level 4 Test", createSection("section-4", title));
    }

    @Test(groups = {"titleGroup"}, dependsOnGroups = {"inlineGroup"})
    public void testSectionLevel5Title() {
        final Builder p1 = builderFactory.getBuilder(null, new Article(), -1);
        final Builder p2 = builderFactory.getBuilder(p1, new Section(), 0);
        final Builder p3 = builderFactory.getBuilder(p2, new Section(), 0);
        final Builder p4 = builderFactory.getBuilder(p3, new Section(), 0);
        final Builder p5 = builderFactory.getBuilder(p4, new Section(), 0);
        final Title title = createTitle("Section 5");
        addResult(p5, 0, 1, "Section Level 5 Test", createSection("section-5", title));
    }

    @Test(groups = {"listGroup"}, dependsOnGroups = "titleGroup")
    public void testItemizedListMoreThenOnePara() {
        final SimplePara p1 = createSimplePara(null, "First paragraph of list item, this should contain bullet mark.");
        final SimplePara p2 = createSimplePara(null, "Second paragraph of list item, this should not contain bullet mark, but should be indented properly.");
        final ListItem li1 = createListItem(null, p1, p2);
        final ListItem li2 = createListItem(null, createSimplePara(null, "Second bullet point."));
        final ItemizedList itemizedList = createItemizedList(nextId(), li1, li2);
        addResult(null, 0, 3, "Itemized list with a list item having multiple para", itemizedList);
    }

    @Test(groups = {"listGroup"}, dependsOnGroups = "titleGroup")
    public void testItemizedList() {
        addResult(null, 0, 3, "ItemizedList Test", readXml("itemizedlist", ItemizedList.class));
    }

    @Test(groups = {"listGroup"}, dependsOnGroups = "titleGroup")
    public void testNestedItemizedList() {
        addResult(null, 0, 6, "ItemizedList Test", readXml("nested-itemizedlist", ItemizedList.class));
    }

    @Test(groups = {"listGroup"}, dependsOnGroups = "titleGroup")
    public void testOrderedListMoreThenOnePara() {
        final SimplePara p1 = createSimplePara(nextId(), "First paragraph of list item, this should contain numeration.");
        final SimplePara p2 = createSimplePara(nextId(), "Second paragraph of list item, this should not contain numeration, but should be indented properly.");
        final ListItem li1 = createListItem(nextId(), p1, p2);
        final ListItem li2 = createListItem(nextId(), createSimplePara(null, "Second bullet point."));
        final OrderedList orderedList = createOrderedList(nextId(), li1, li2);
        addResult(null, 0, 3, "Ordered list with a list item having multiple para", orderedList);
    }

    @Test(groups = {"listGroup"}, dependsOnGroups = {"titleGroup"})
    public void testOrderedList() {
        addResult(null, 0, 3, "OrderedList Test", readXml("orderedlist", OrderedList.class));
    }

    @Test(groups = {"listGroup"}, dependsOnGroups = {"titleGroup"})
    public void testNestedOrderedList() {
        addResult(null, 0, 5, "OrderedList Test", readXml("nested-orderedlist", OrderedList.class));
    }

    @Test(groups = {"listGroup"}, dependsOnGroups = {"titleGroup"})
    public void testVariableListEntryBuilder() {
        final Term term = createTerm("Entry title");
        final SimplePara simplePara = createSimplePara(null, "This text is under simple para and it has to be indented using \"ListParagraph\" style without any numbering.");
        final ListItem listItem = createListItem(null, simplePara);
        addResult(null, 0, 2, "VariableListEntry Test", createVariableListEntry(listItem, term));
    }

    @Test(groups = {"listGroup"}, dependsOnGroups = {"titleGroup"}, dependsOnMethods = {"testVariableListEntryBuilder"})
    public void testVariableListBuilder() {
        addResult(null, 0, 12, "VariableList Test", readXml("variablelist", VariableList.class));
    }

    @Test(groups = {"blockGroup"}, dependsOnGroups = {"listGroup"})
    public void testCaution() {
        Caution caution = createCaution(createSimplePara(null, "If the title line is not offset by a blank line, it gets interpreted as a section title, which we&#8217;ll discuss later."));
        addResult(null, 0, 3, "Caution Test", caution);
    }

    @Test(groups = {"blockGroup"}, dependsOnGroups = {"listGroup"})
    public void testHorizontalList() {
        final Row row1 = createRow(createEntry(null, null, createSimplePara(null, "CPU")),
                createEntry(null, null, createSimplePara(null, "The brain of the computer.")));
        final Row row2 = createRow(createEntry(null, null, createSimplePara(null, "Hard drive")),
                createEntry(null, null, createSimplePara(null, "Permanent storage for operating system and/or user files.")));
        final Row row3 = createRow(createEntry(null, null, createSimplePara(null, "RAM")),
                createEntry(null, null, createSimplePara(null, "Temporarily stores information the CPU uses during operation.")));
        final TableBody tableBody = createTableBody(null, VerticalAlign.TOP, row1, row2, row3);
        final TableGroup tableGroup = createTableGroup(null, tableBody, null, 15, 85);
        final InformalTable informalTable = createInformalTable("horizontal", Frame.NONE, Choice.ZERO, Choice.ZERO, tableGroup);
        addResult(null, 0, 1, "Table With Custom Style", informalTable);
    }

    @Test(groups = {"blockGroup"}, dependsOnGroups = {"listGroup"})
    public void testImportant() {
        Important important = createImportant(createSection(null, "There should be no blank lines between the first attribute entry and the rest of the header."));
        addResult(null, 0, 3, "Important Test", important);
    }

    @Test(groups = {"blockGroup"}, dependsOnGroups = {"listGroup"})
    public void testNote() {
        Note note = createNote(createSimplePara(null, "Admonitions can also encapsulate any block content, which we&#8217;ll cover later."));
        addResult(null, 0, 3, "Note Test", note);
    }

    @Test(groups = {"blockGroup"}, dependsOnGroups = {"listGroup"})
    public void testTip() {
        Tip tip = createTip(createSimplePara(null, "A document title is an <emphasis>optional</emphasis> feature of an AsciiDoc document."));
        addResult(null, 0, 3, "Tip Test", tip);
    }

    @Test(groups = {"blockGroup"}, dependsOnGroups = {"listGroup"})
    public void testSimpleTable() {
        Entry entry1 = createEntry(LEFT, TOP, createSimplePara(null, "Cell in column 1, row 1"));
        Entry entry2 = createEntry(LEFT, TOP, createSimplePara(null, "Cell in column 2, row 1"));
        final Row row1 = createRow(entry1, entry2);

        entry1 = createEntry(LEFT, TOP, createSimplePara(null, "Cell in column 1, row 2"));
        entry2 = createEntry(LEFT, TOP, createSimplePara(null, "Cell in column 2, row 2"));
        final Row row2 = createRow(entry1, entry2);

        entry1 = createEntry(LEFT, TOP, createSimplePara(null, "Cell in column 1, row 3"));
        entry2 = createEntry(LEFT, TOP, createSimplePara(null, "Cell in column 2, row 3"));
        final Row row3 = createRow(entry1, entry2);

        final TableBody tableBody = createTableBody(null, null, row1, row2, row3);
        final TableGroup tableGroup = createTableGroup(null, tableBody, null, 192, 192);
        final Table table = createTable(null, Frame.ALL, Choice.ONE, Choice.ONE, createTitle("Result: Rendered simple table"), tableGroup);

        addResult(null, 0, 2, "Simple Table Test", table);
    }

    @Test(groups = {"blockGroup"}, dependsOnGroups = {"listGroup"})
    public void testTableVAlignMiddle() {
        final int numOfColumns = 2;
        final BasicVerticalAlign verticalAlign = MIDDLE;
        final TableBody tableBody = createTableBody(null, null, _createRow(numOfColumns, 1, verticalAlign),
                _createRow(numOfColumns, 2, verticalAlign));
        final TableGroup tableGroup = createTableGroup(null, tableBody, null, 192, 192);
        final Table table = createTable(null, Frame.ALL, Choice.ONE, Choice.ONE, null, tableGroup);

        addResult(null, 0, 1, "Table Test With Vertical Align Middle", table);
    }

    @Test(groups = {"blockGroup"}, dependsOnGroups = {"listGroup"})
    public void testTableVAlignBottom() {
        final int numOfColumns = 2;
        final BasicVerticalAlign verticalAlign = BasicVerticalAlign.BOTTOM;
        final TableBody tableBody = createTableBody(null, null, _createRow(numOfColumns, 1, verticalAlign),
                _createRow(numOfColumns, 2, verticalAlign));
        final TableGroup tableGroup = createTableGroup(null, tableBody, null, 192, 192);
        final Table table = createTable(null, Frame.ALL, Choice.ONE, Choice.ONE, null, tableGroup);
        addResult(null, 0, 1, "Table Test With Vertical Align Bottom", table);
    }

    @Test(groups = {"blockGroup"}, dependsOnGroups = {"listGroup"})
    public void testTableWithHeader() {
        final int numOfColumns = 4;
        final TableBody tableBody = createTableBody(null, null, _createRow(numOfColumns, 1));
        final TableGroup tableGroup = createTableGroup(_createHeader(numOfColumns), tableBody, null, 15, 15, 15, 55);
        final Table table = createTable(null, Frame.ALL, Choice.ONE, Choice.ONE, null, tableGroup);
        addResult(null, 0, 1, "Table With Header Test", table);
    }

    @Test(groups = {"blockGroup"}, dependsOnGroups = {"listGroup"})
    public void testTableWithFooter() {
        final int numOfColumns = 4;
        final TableBody tableBody = createTableBody(null, null, _createRow(numOfColumns, 1));
        final TableGroup tableGroup = createTableGroup(null, tableBody, _createFooter(numOfColumns), 15, 15, 15, 55);
        final Table table = createTable(null, Frame.ALL, Choice.ONE, Choice.ONE, null, tableGroup);
        addResult(null, 0, 1, "Table With Footer Test", table);
    }

    @Test(groups = {"blockGroup"}, dependsOnGroups = {"listGroup"})
    public void testTableWithHeaderAndFooter() {
        final int numOfColumns = 4;
        final TableBody tableBody = createTableBody(null, null, _createRow(numOfColumns, 1));
        final TableGroup tableGroup = createTableGroup(_createHeader(numOfColumns), tableBody,
                _createFooter(numOfColumns), 15, 15, 15, 55);
        final Table table = createTable(null, Frame.ALL, Choice.ONE, Choice.ONE, null, tableGroup);
        addResult(null, 0, 1, "Table With Header And Footer Test", table);
    }

    @Test(groups = {"blockGroup"}, dependsOnGroups = {"listGroup"})
    public void testTableColumnSpan() {
        Entry entry1 = _createEntry(LEFT, TOP, "col_1", "col_4", null, "Row 1, Column Span 1 - 4");
        final Row row1 = createRow(entry1);

        entry1 = _createEntry(LEFT, TOP, "col_1", "col_2", null, "Row 2, Column Span 1 - 2");
        Entry entry2 = _createEntry(LEFT, TOP, "col_3", "col_4", null, "Row 2, Column Span 3 - 4");
        final Row row2 = createRow(entry1, entry2);

        entry1 = _createEntry(LEFT, TOP, "Row 3, Column 1");
        entry2 = _createEntry(LEFT, TOP, "col_2", "col_4", null, "Row 3, Column Span 2 - 4");
        final Row row3 = createRow(entry1, entry2);

        entry1 = _createEntry(LEFT, TOP, "col_1", "col_2", null, "Row 4, Column Span 1 - 2");
        entry2 = _createEntry(LEFT, TOP, "Row 4, Column 3");
        Entry entry3 = _createEntry(LEFT, TOP, "Row 4, Column 4");
        final Row row4 = createRow(entry1, entry2, entry3);

        entry1 = _createEntry(LEFT, TOP, "Row 5, Column 1");
        entry2 = _createEntry(LEFT, TOP, "col_2", "col_3", null, "Row 5, Column Span 2 - 3");
        entry3 = _createEntry(LEFT, TOP, "Row 5, Column 4");
        final Row row5 = createRow(entry1, entry2, entry3);

        entry1 = _createEntry(LEFT, TOP, "Row 6, Column 1");
        entry2 = _createEntry(LEFT, TOP, "Row 6, Column 2");
        entry3 = _createEntry(LEFT, TOP, "col_3", "col_4", null, "Row 6, Column Span 3 - 4");
        final Row row6 = createRow(entry1, entry2, entry3);

        final Row row7 = _createRow(4, 7);

        final TableBody tableBody = createTableBody(null, null, row1, row2, row3, row4, row5, row6, row7);
        final TableGroup tableGroup = createTableGroup(null, tableBody, null, 25, 20, 35, 20);
        final Table table = createTable(null, Frame.ALL, Choice.ONE, Choice.ONE, null, tableGroup);
        addResult(null, 0, 1, "Table With Column Span Test", table);
    }

    @Test(groups = {"blockGroup"}, dependsOnGroups = {"listGroup"})
    public void testTableRowSpan() {
        final Row row1 = _createRow(4, 1);

        Entry entry1 = _createEntry(LEFT, TOP, null, null, "1", "Row 2, Column 1, Row Span 1");
        Entry entry2 = _createEntry(LEFT, TOP, "Row 2, Column 2");
        Entry entry3 = _createEntry(LEFT, TOP, "Row 2, Column 3");
        Entry entry4 = _createEntry(LEFT, TOP, "Row 2, Column 4");
        final Row row2 = createRow(entry1, entry2, entry3, entry4);

        entry2 = _createEntry(LEFT, TOP, "Row 3, Column 2");
        entry3 = _createEntry(LEFT, TOP, "Row 3, Column 3");
        entry4 = _createEntry(LEFT, TOP, "Row 3, Column 4");
        final Row row3 = createRow(entry2, entry3, entry4);

        final TableBody tableBody = createTableBody(null, null, row1, row2, row3);
        final TableGroup tableGroup = createTableGroup(null, tableBody, null, 25, 25, 25, 25);
        final Table table = createTable(null, Frame.ALL, Choice.ONE, Choice.ONE, null, tableGroup);
        addResult(null, 0, 1, "Table With Row Span Test", table);
    }

    @Test(groups = {"blockGroup"}, dependsOnGroups = {"listGroup"})
    public void testTableRowAndColumnSpan() {
        Entry entry1 = _createEntry(CENTER, MIDDLE, "col_1", "col_2", "2", "Row 1, Column 1 & 2");
        Entry entry2 = _createEntry(CENTER, MIDDLE, "col_2", "col_3", "2", "Row 1, Column 3 & 4");
        final Row row2 = createRow(entry1, entry2);

        final TableBody tableBody = createTableBody(null, null, row2);
        final TableGroup tableGroup = createTableGroup(null, tableBody, null, 25, 25, 25, 25);
        final Table table = createTable(null, Frame.ALL, Choice.ONE, Choice.ONE, null, tableGroup);
        addResult(null, 0, 1, "Table With Row & Column Span Test", table);
    }

    @Test(groups = {"blockGroup"}, dependsOnGroups = {"listGroup"})
    public void testTableWithFrameAll() {
        final Table table = tableBorderTest(Frame.ALL, Choice.ZERO, Choice.ZERO);
        addResult(null, 0, 1, "Table With Frame ALL Test", table);
    }

    @Test(groups = {"blockGroup"}, dependsOnGroups = {"listGroup"}, dependsOnMethods = {"testTableWithFrameAll"})
    public void testTableWithNoBorder() {
        final Table table = tableBorderTest(Frame.NONE, Choice.ZERO, Choice.ZERO);
        addResult(null, 0, 1, "Table With No Border Test", table);
    }

    @Test(groups = {"blockGroup"}, dependsOnGroups = {"listGroup"}, dependsOnMethods = {"testTableWithNoBorder"})
    public void testTableWithNoFrame() {
        final Table table = tableBorderTest(Frame.NONE, Choice.ONE, Choice.ONE);
        addResult(null, 0, 1, "Table With No Frame Test", table);
    }

    private Table tableBorderTest(Frame frame, Choice colSep, Choice rowSep) {
        final int numOfColumns = 4;
        final TableBody tableBody = createTableBody(null, null, _createRows(numOfColumns, 3));
        final TableGroup tableGroup = createTableGroup(null, tableBody, null, 25, 25, 25, 25);
        return createTable(null, frame, colSep, rowSep, null, tableGroup);
    }

    private Entry _createEntry(Align align, BasicVerticalAlign vAlign, String text) {
        return _createEntry(align, vAlign, null, null, null, text);
    }

    private Entry _createEntry(Align align, BasicVerticalAlign vAlign, String nameStart, String nameEnd, String moreRows,
                               String text) {
        return createEntry(align, vAlign, nameStart, nameEnd, moreRows, createSimplePara(nextId(), text));
    }

    private TableHeader _createHeader(int numOfColumns) {
        Entry[] entries = new Entry[numOfColumns];
        for (int i = 0; i < numOfColumns; i++) {
            entries[i] = _createEntry(LEFT, TOP, format("Header %s", (i + 1)));
        }
        return createTableHeader(null, null, createRow(entries));
    }

    private TableFooter _createFooter(int numOfColumns) {
        Entry[] entries = new Entry[numOfColumns];
        for (int i = 0; i < numOfColumns; i++) {
            entries[i] = _createEntry(LEFT, TOP, format("Footer %s", (i + 1)));
        }
        return createTableFooter(null, null, createRow(entries));
    }

    private Row[] _createRows(int numOfColumns, int numOfRows) {
        Row[] rows = new Row[numOfRows];
        for (int i = 0; i < numOfRows; i++) {
            rows[i] = _createRow(numOfColumns, (i + 1));
        }
        return rows;
    }

    private Row _createRow(int numOfColumns, int row) {
        return _createRow(numOfColumns, row, null);
    }

    private Row _createRow(int numOfColumns, int row, BasicVerticalAlign verticalAlign) {
        verticalAlign = (verticalAlign == null) ? TOP : verticalAlign;
        Entry[] entries = new Entry[numOfColumns];
        for (int i = 0; i < numOfColumns; i++) {
            entries[i] = _createEntry(LEFT, verticalAlign, format("Row %s, Column %s", row, (i + 1)));
        }
        return createRow(entries);
    }

    @Test(groups = {"blockGroup"}, dependsOnGroups = {"listGroup"})
    public void testWarning() {
        Warning warning = createWarning(createSimplePara(null, "Wolpertingers are known to nest in server racks.\n" +
                "        Enter at your own risk."));
        addResult(null, 0, 3, "Warning Test", warning);
    }

    @Test(groups = {"blockGroup"}, dependsOnMethods = {"testWarning"}, dependsOnGroups = {"listGroup"})
    public void testExample() {
        addResult(null, 0, 3, "Example Test", readXml("example", Example.class));
    }

    @Test(groups = {"blockGroup"}, dependsOnMethods = {"testExample"}, dependsOnGroups = {"listGroup"})
    public void testInformalExample() {
        addResult(null, 0, 3, "Informal Example Test", readXml("informal-example", InformalExample.class));
    }

}
