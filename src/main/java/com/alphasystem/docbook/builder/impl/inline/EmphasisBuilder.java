package com.alphasystem.docbook.builder.impl.inline;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.InlineBuilder;
import org.docbook.model.Emphasis;

import static com.alphasystem.docbook.builder.DocumentBuilderHelper.ITALIC;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * @author sali
 */
public class EmphasisBuilder extends InlineBuilder<Emphasis> {

    public EmphasisBuilder(Builder parent, Emphasis emphasis) {
        super(parent, emphasis);
    }

    @Override
    protected void initContent() {
        String role = source.getRole();
        style = isBlank(role) ? ITALIC : role;
        content = source.getContent();
    }

}
