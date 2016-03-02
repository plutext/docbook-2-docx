package com.alphasystem.docbook;

import com.alphasystem.SystemException;
import com.alphasystem.docbook.handler.BuilderFactory;
import com.alphasystem.openxml.builder.wml.*;
import com.alphasystem.xml.DocumentInfo;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.DocumentSettingsPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart;
import org.docx4j.wml.*;

import java.nio.file.Paths;
import java.util.List;

import static com.alphasystem.docbook.handler.model.DocumentCaption.EXAMPLE;
import static com.alphasystem.openxml.builder.wml.NumberingHelper.populate;
import static com.alphasystem.openxml.builder.wml.WmlAdapter.*;

/**
 * @author sali
 */
public class DocumentBuilder {

    /**
     * @param documentInfo document to convert
     * @param destPath     target file path
     * @throws NullPointerException
     * @throws SystemException
     */
    public void buildDocument(DocumentInfo documentInfo, String destPath) throws NullPointerException, SystemException {
        if (documentInfo == null || documentInfo.getDocument() == null) {
            throw new NullPointerException("DocumentInfo cannot be null.");
        }

        DocumentContext documentContext = new DocumentContext(documentInfo);
        ApplicationController.startContext(documentContext);

        try {
            WmlPackageBuilder wmlPackageBuilder = new WmlPackageBuilder();
            WordprocessingMLPackage wordprocessingMLPackage = wmlPackageBuilder.getPackage();
            MainDocumentPart mainDocumentPart = wordprocessingMLPackage.getMainDocumentPart();
            final StyleDefinitionsPart styleDefinitionsPart = mainDocumentPart.getStyleDefinitionsPart();
            final Styles styles = styleDefinitionsPart.getContents();
            final List<Style> list = styles.getStyle();
            list.forEach(style -> documentContext.getDocumentStyles().add(style.getStyleId()));

            if (documentInfo.isNumbered()) {
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
                final PPrBuilder pPrBuilder = WmlBuilderFactory.getPPrBuilder();
                final PPrBase.NumPr numPr = pPrBuilder.getNumPrBuilder().withIlvl(0L).getObject();
                final PPr pPr = pPrBuilder.withPStyle("TOCHeading").withNumPr(numPr).getObject();
                final Text text = getText(documentInfo.getTableOfContentCaption());
                final R r = WmlBuilderFactory.getRBuilder().addContent(text).getObject();
                final PBuilder pBuilder = WmlBuilderFactory.getPBuilder().withPPr(pPr).addContent(r);
                mainDocumentPart.addObject(pBuilder.getObject());
                mainDocumentPart.addObject(addTableOfContent(" TOC \\o \"1-3\" \\h \\z \\u \\h "));
                mainDocumentPart.addObject(getPageBreak());
            }
            content.forEach(mainDocumentPart::addObject);
            save(Paths.get(destPath).toFile(), wordprocessingMLPackage);
        } catch (Docx4JException e) {
            throw new SystemException(e.getMessage(), e);
        } finally {
            ApplicationController.endContext();
        }
    }

}
