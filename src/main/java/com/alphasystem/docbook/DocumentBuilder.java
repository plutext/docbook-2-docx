package com.alphasystem.docbook;

import com.alphasystem.SystemException;
import com.alphasystem.asciidoc.model.AsciiDocumentInfo;
import com.alphasystem.docbook.handler.BuilderFactory;
import com.alphasystem.docbook.util.FileUtil;
import com.alphasystem.openxml.builder.wml.NumberingBuilder;
import com.alphasystem.openxml.builder.wml.WmlAdapter;
import com.alphasystem.openxml.builder.wml.WmlBuilderFactory;
import com.alphasystem.openxml.builder.wml.WmlPackageBuilder;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.ast.StructuredDocument;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.DocumentSettingsPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart;
import org.docx4j.wml.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

import static com.alphasystem.asciidoc.model.Backend.DOC_BOOK;
import static com.alphasystem.docbook.DocBookUtil.getDocument;
import static com.alphasystem.docbook.handler.model.DocumentCaption.EXAMPLE;
import static com.alphasystem.docbook.util.FileUtil.checkFileModified;
import static com.alphasystem.openxml.builder.wml.NumberingHelper.populate;
import static com.alphasystem.openxml.builder.wml.WmlAdapter.save;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.readAllBytes;
import static org.apache.commons.io.FilenameUtils.getExtension;

/**
 * @author sali
 */
public class DocumentBuilder {

    private final static Asciidoctor asciiDoctor = Asciidoctor.Factory.create();

    static {
        // initialize Application controller
        ApplicationController.getInstance();
    }

    public static Path buildDocument(Path srcPath) throws SystemException {
        final File srcFile = srcPath.toFile();
        if (!exists(srcPath)) {
            throw new NullPointerException("Source file does not exists.");
        }
        final Path fileNamePath = srcPath.getFileName();
        final String fileName = fileNamePath.toString();
        final String extension = getExtension(fileName);
        AsciiDocumentInfo documentInfo = null;
        Path docBookPath = null;
        if ("adoc".endsWith(extension)) {
            // load ascii document info
            documentInfo = new AsciiDocumentInfo();
            documentInfo.setSrcFile(srcFile);
            StructuredDocument structuredDocument = asciiDoctor.readDocumentStructure(srcFile, new HashMap<>());
            documentInfo.populateAttributes(structuredDocument.getHeader().getAttributes());

            // input file is ascii doc file
            docBookPath = FileUtil.getDocBookFile(srcPath);
            final File file = docBookPath.toFile();
            boolean modified = checkFileModified(srcPath, docBookPath);
            if (modified) {
                System.out.println("Generating DocBook XML.");
                // if  doc modified after generating doc book file, re-generate doc book file
                // make a copy for converting to doc book
                documentInfo = new AsciiDocumentInfo(documentInfo);
                documentInfo.setPreviewFile(file);
                documentInfo.setBackend(DOC_BOOK.getValue());

                try {
                    asciiDoctor.convert(new String(readAllBytes(srcPath)), documentInfo.getOptionsBuilder());
                } catch (IOException e) {
                    throw new SystemException(e.getMessage(), e);
                }
            }
        } else if ("xml".endsWith(extension)) {
            // doc book file`
            docBookPath = srcPath;
            // TODO:
        }

        final Path docxPath = FileUtil.getDocxFile(srcPath);
        buildDocument(docxPath, documentInfo, getDocument(docBookPath.toString()));
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
            documentContext.setNumberingDefinitionsPart(mainDocumentPart.getNumberingDefinitionsPart());

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


}
