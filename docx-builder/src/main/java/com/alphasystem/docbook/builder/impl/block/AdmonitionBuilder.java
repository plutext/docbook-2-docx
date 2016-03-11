package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.model.Admonition;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;

/**
 * Common class for admonitions.
 *
 * @author sali
 * @see CautionBuilder
 * @see ImportantBuilder
 * @see NoteBuilder
 * @see TipBuilder
 * @see WarningBuilder
 */
public abstract class AdmonitionBuilder<T> extends TableBasedBlockBuilder<T> {

    private final Admonition admonition;

    protected AdmonitionBuilder(Builder parent, T obj, Admonition admonition) {
        super(parent, obj);
        this.admonition = admonition;
    }

    @Override
    protected void preProcess() {
        tbl = applicationController.getAdmonitionTable(admonition);
        final Tr tr = (Tr) tbl.getContent().get(0);
        // content column
        this.tc = (Tc) tr.getContent().get(1);
    }
}
