package com.alphasystem.docbook.handler.impl;

import com.alphasystem.docbook.handler.Builder;
import org.docbook.model.Emphasis;
import org.docx4j.wml.R;

import static com.alphasystem.docbook.handler.DocumentHandlerHelper.ITALIC;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * @author sali
 */
public class EmphasisBuilder extends RunBuilder<Emphasis> {

    public EmphasisBuilder(Builder parent, Emphasis emphasis) {
        super(parent, emphasis);
    }

    @Override
    public R build(R r) {
        String role = obj.getRole();
        role = isBlank(role) ? ITALIC : role;
        return buildRun(r, role, obj.getContent());
    }
}
