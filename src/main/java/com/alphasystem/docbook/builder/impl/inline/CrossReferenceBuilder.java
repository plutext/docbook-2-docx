package com.alphasystem.docbook.builder.impl.inline;

import com.alphasystem.docbook.builder.Builder;
import org.docbook.model.CrossReference;

import java.util.Collections;

/**
 * @author sali
 */
public class CrossReferenceBuilder extends LinkSupportBuilder<CrossReference> {

    public CrossReferenceBuilder(Builder parent, CrossReference xref) {
        super(parent, xref);
    }

    @Override
    protected void initContent() {
        super.initContent();
        external = false;
        linkEnd = source.getLinkend();
        href = getId(linkEnd);
        Object link = getLink();
        link = (link == null) ? "Error!" : link;
        content = Collections.singletonList(link);
    }

}
