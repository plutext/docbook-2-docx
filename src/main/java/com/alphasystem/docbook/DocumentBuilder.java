package com.alphasystem.docbook;

import com.alphasystem.SystemException;
import com.alphasystem.asciidoc.model.AsciiDocumentInfo;
import com.alphasystem.docbook.builder.BuilderFactory;
import com.alphasystem.docbook.util.FileUtil;
import com.alphasystem.openxml.builder.wml.NumberingBuilder;
import com.alphasystem.openxml.builder.wml.WmlAdapter;
import com.alphasystem.openxml.builder.wml.WmlBuilderFactory;
import com.alphasystem.openxml.builder.wml.WmlPackageBuilder;
import com.alphasystem.util.nio.NIOFileUtils;
import com.alphasystem.xml.UnmarshallerTool;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.ast.StructuredDocument;
import org.docbook.model.Article;
import org.docbook.model.Book;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.DocumentSettingsPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart;
import org.docx4j.wml.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

import static com.alphasystem.asciidoc.model.Backend.DOC_BOOK;
import static com.alphasystem.docbook.builder.model.DocumentCaption.EXAMPLE;
import static com.alphasystem.openxml.builder.wml.NumberingHelper.populate;
import static com.alphasystem.openxml.builder.wml.WmlAdapter.save;
import static java.nio.file.Files.exists;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * @author sali
 */
public class DocumentBuilder {

    private final static Asciidoctor asciiDoctor = Asciidoctor.Factory.create();

    static {
        // initialize Application controller
        ApplicationController.getInstance();
    }

    public static Path buildDocument(String content, AsciiDocumentInfo documentInfo) throws SystemException {
        if (isBlank(content) || documentInfo == null) {
            throw new SystemException("No content or document info");
        }
        String docBookContent = convertToDocBook(content);
        UnmarshallerTool unmarshallerTool = new UnmarshallerTool();

        final Path docxPath = FileUtil.getDocxFile(documentInfo.getSrcFile().toPath());
        buildDocument(docxPath, documentInfo, getDocument(docBookContent, unmarshallerTool));
        return docxPath;
    }

    public static Path buildDocument(Path srcPath) throws SystemException {
        final File srcFile = srcPath.toFile();
        if (!exists(srcPath)) {
            throw new NullPointerException("Source file does not exists.");
        }
        final Path fileNamePath = srcPath.getFileName();
        final String fileName = fileNamePath.toString();
        final String extension = getExtension(fileName);
        String docBookContent = null;
        AsciiDocumentInfo documentInfo = null;
        if ("adoc".endsWith(extension)) {
            // load ascii document info
            documentInfo = new AsciiDocumentInfo();
            documentInfo.setSrcFile(srcFile);
            StructuredDocument structuredDocument = asciiDoctor.readDocumentStructure(srcFile, new HashMap<>());
            documentInfo.populateAttributes(structuredDocument.getHeader().getAttributes());
            docBookContent = convertToDocBook(documentInfo);
        } else if ("xml".endsWith(extension)) {
            try {
                try (InputStream inputStream = Files.newInputStream(srcPath);
                     ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    NIOFileUtils.fastCopy(inputStream, outputStream);
                    docBookContent = new String(outputStream.toByteArray());
                }
            } catch (IOException e) {
                throw new SystemException(e.getMessage(), e);
            }
        }

        UnmarshallerTool unmarshallerTool = new UnmarshallerTool();
        Object document = getDocument(docBookContent, unmarshallerTool);
        if (documentInfo == null) {
            documentInfo = unmarshallerTool.getDocumentInfo();
        }

        final Path docxPath = FileUtil.getDocxFile(srcPath);
        buildDocument(docxPath, documentInfo, document);
        return docxPath;
    }

    public static void buildDocument(Path docxPath, final AsciiDocumentInfo documentInfo, final Object document) throws SystemException {
        DocumentContext documentContext = new DocumentContext(documentInfo, document);
        ApplicationController.startContext(documentContext);

        try {
            WmlPackageBuilder wmlPackageBuilder = new WmlPackageBuilder();
            WordprocessingMLPackage wordprocessingMLPackage = wmlPackageBuilder.getPackage();
            MainDocumentPart mainDocumentPart = wordprocessingMLPackage.getMainDocumentPart();
            final StyleDefinitionsPart styleDefinitionsPart = mainDocumentPart.getStyleDefinitionsPart();
            final Styles styles = styleDefinitionsPart.getContents();
            final List<Style> list = styles.getStyle();
            list.forEach(style -> documentContext.getDocumentStyles().add(style.getStyleId()));

            if (documentInfo.isSectionNumbers()) {
                wmlPackageBuilder.multiLevelHeading();
            }
            if (documentInfo.getExampleCaption() == null) {
                wmlPackageBuilder.styles("example-no-caption.xml");
            } else {
                final NumberingBuilder numberingBuilder = WmlBuilderFactory.getNumberingBuilder();
                populate(numberingBuilder, EXAMPLE);
                wmlPackageBuilder.styles("example-with-caption.xml").numbering(numberingBuilder.getObject());
            }
            documentContext.setMainDocumentPart(mainDocumentPart);

            final List<Object> content = BuilderFactory.getInstance().buildDocument();
            if (content == null || content.isEmpty()) {
                ApplicationController.endContext();
                return;
            }
            //Adding Print View and Setting Update Field to true
            DocumentSettingsPart dsp = mainDocumentPart.getDocumentSettingsPart();
            if (dsp == null) {
                CTSettings ct = new CTSettings();
                dsp = new DocumentSettingsPart();
                CTView ctView = Context.getWmlObjectFactory().createCTView();
                ctView.setVal(STView.PRINT);
                ct.setView(ctView);
                ct.setUpdateFields(WmlBuilderFactory.BOOLEAN_DEFAULT_TRUE_TRUE);
                dsp.setJaxbElement(ct);
                mainDocumentPart.addTargetPart(dsp);
            }

            if (documentInfo.isToc()) {
                final List<P> paras = WmlAdapter.addTableOfContent(documentInfo.getTocTitle(),
                        " TOC \\o \"1-3\" \\h \\z \\u \\h ");
                paras.forEach(mainDocumentPart::addObject);
            }
            content.forEach(mainDocumentPart::addObject);
            save(docxPath.toFile(), wordprocessingMLPackage);
        } catch (Docx4JException e) {
            throw new SystemException(e.getMessage(), e);
        } finally {
            ApplicationController.endContext();
        }
    }

    private static Object getDocument(String docBookContent, UnmarshallerTool unmarshallerTool) throws SystemException {
        Object document;
        try {
            document = unmarshallerTool.unmarshal(docBookContent, Article.class);
        } catch (SystemException e) {
            document = unmarshallerTool.unmarshal(docBookContent, Book.class);
        }
        return document;
    }

    private static String convertToDocBook(AsciiDocumentInfo asciiDocumentInfo) throws SystemException {
        String docBookContent;
        AsciiDocumentInfo docBook = new AsciiDocumentInfo(asciiDocumentInfo);
        docBook.setBackend(DOC_BOOK.getValue());
        OptionsBuilder optionsBuilder = docBook.getOptionsBuilder().headerFooter(true);
        try {
            try (Reader reader = Files.newBufferedReader(asciiDocumentInfo.getSrcFile().toPath());
                 StringWriter writer = new StringWriter()) {
                asciiDoctor.convert(reader, writer, optionsBuilder);
                docBookContent = writer.toString();
            }
        } catch (IOException e) {
            throw new SystemException(e.getMessage(), e);
        }
        return docBookContent;
    }

    private static String convertToDocBook(String content) throws SystemException {
        OptionsBuilder optionsBuilder = OptionsBuilder.options().backend(DOC_BOOK.getValue()).headerFooter(true);
        return asciiDoctor.convert(content, optionsBuilder);
    }
}
