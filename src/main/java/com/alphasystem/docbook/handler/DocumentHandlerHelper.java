package com.alphasystem.docbook.handler;

import org.docx4j.wml.CTBookmark;
import org.docx4j.wml.CTMarkupRange;
import org.docx4j.wml.P;

import javax.xml.bind.JAXBElement;
import java.util.concurrent.atomic.AtomicInteger;

import static com.alphasystem.openxml.builder.wml.WmlAdapter.createCTMarkupRange;
import static com.alphasystem.openxml.builder.wml.WmlBuilderFactory.getCTBookmarkBuilder;
import static com.alphasystem.openxml.builder.wml.WmlBuilderFactory.getCTBookmarkRangeBuilder;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * @author sali
 */
public final class DocumentHandlerHelper {

    public static final String ITALIC = "italic";
    public static final String LITERAL = "literal";
    public static final String SUBSCRIPT = "subscript";
    public static final String SUPERSCRIPT = "superscript";

    private static final AtomicInteger bookmarkCount = new AtomicInteger(0);

    /**
     * Do not let any one instantiate this class.
     */
    private DocumentHandlerHelper() {
    }

    public static void addBookMark(P p, String id) {
        if (isBlank(id)) {
            return;
        }
        String bookmarkId = String.valueOf(bookmarkCount.incrementAndGet());
        CTBookmark bookmarkStart = getCTBookmarkBuilder().withId(bookmarkId).withName(id).getObject();
        JAXBElement<CTMarkupRange> bookmarkEnd = createCTMarkupRange(getCTBookmarkRangeBuilder().withId(bookmarkId).getObject());
        p.getContent().add(0, bookmarkStart);
        p.getContent().add(bookmarkEnd);
    }

}
