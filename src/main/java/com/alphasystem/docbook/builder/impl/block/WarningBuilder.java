package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.model.Admonition;
import org.docbook.model.Warning;

/**
 * @author sali
 */
public class WarningBuilder extends AdmonitionBuilder<Warning> {

    public WarningBuilder(Builder parent, Warning warning) {
        super(parent, warning, Admonition.WARNING);
    }

    @Override
    protected void initContent() {
        titleContent = source.getTitleContent();
        content = source.getContent();
    }
}
