package com.alphasystem.docbook;

import com.alphasystem.xml.DocumentInfo;
import org.docbook.model.Article;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alphasystem.util.AppUtil.isInstanceOf;

/**
 * @author sali
 */
public final class DocumentContext {

    private static final Object DUMMY = new Object();

    private final Map<Long, Object> listNumbersMap = new HashMap<>();
    private final List<String> documentStyles;
    private final DocumentInfo documentInfo;
    private final boolean article;
    private NumberingDefinitionsPart numberingDefinitionsPart;

    public DocumentContext(final DocumentInfo documentInfo) {
        this.documentInfo = documentInfo;
        this.documentStyles = new ArrayList<>();
        article = isInstanceOf(Article.class, this.documentInfo.getDocument());
    }

    public List<String> getDocumentStyles() {
        return documentStyles;
    }

    public DocumentInfo getDocumentInfo() {
        return documentInfo;
    }

    public boolean isArticle() {
        return article;
    }

    public void setNumberingDefinitionsPart(NumberingDefinitionsPart numberingDefinitionsPart) {
        this.numberingDefinitionsPart = numberingDefinitionsPart;
    }

    public long getListNumber(long numberId, long level) {
        final Object o = listNumbersMap.get(numberId);
        if (o == null) {
            listNumbersMap.put(numberId, DUMMY);
        } else {
            numberId = numberingDefinitionsPart.restart(numberId, level, 1);
        }
        return numberId;
    }
}
