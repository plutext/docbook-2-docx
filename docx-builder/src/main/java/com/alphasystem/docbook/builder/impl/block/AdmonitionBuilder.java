package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.model.Admonition;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;

import static com.alphasystem.openxml.builder.wml.WmlBuilderFactory.getPPrBuilder;

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

    public Admonition getAdmonition() {
        return admonition;
    }

    @Override
    protected void preProcess() {
        paraProperties = getPPrBuilder().withPStyle(configurationUtils.getAdmonitionStyle(admonition)).getObject();
        listParaProperties = getPPrBuilder().withPStyle(configurationUtils.getAdmonitionListStyle(admonition)).getObject();
        tbl = applicationController.getAdmonitionTable(admonition);
        final Tr tr = (Tr) tbl.getContent().get(0);
        // content column
        this.tc = (Tc) tr.getContent().get(1);
    }
}
