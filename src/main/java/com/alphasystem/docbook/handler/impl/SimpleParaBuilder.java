package com.alphasystem.docbook.handler.impl;

import com.alphasystem.docbook.handler.Builder;
import com.alphasystem.openxml.builder.wml.PBuilder;
import org.docbook.model.SimplePara;
import org.docx4j.wml.P;
import org.docx4j.wml.R;

import java.util.ArrayList;
import java.util.List;

import static com.alphasystem.openxml.builder.wml.WmlBuilderFactory.getPBuilder;
import static com.alphasystem.util.IdGenerator.nextId;

/**
 * @author sali
 */
public class SimpleParaBuilder extends AbstractBuilder<SimplePara> {

    public SimpleParaBuilder(Builder parent, SimplePara simpara) {
        super(parent, simpara);
    }

    @Override
    public List<R> buildRuns() {
        List<R> runs = new ArrayList<>();
        obj.getContent().forEach(o -> {
            final Builder builder = factory.getBuilder(this, o);
            if (builder == null) {
                logUnhandledContentWarning(o);
            } else {
                runs.add(builder.build(null));
            }
        });
        return runs;
    }

    @Override
    public P buildParagraph() {
        String rsidP = nextId();
        String rsidR = nextId();
        final PBuilder pBuilder = getPBuilder().withRsidP(rsidP).withRsidRDefault(rsidR).withRsidR(nextId());
        pBuilder.addContent(buildRuns().toArray());
        return pBuilder.getObject();
    }

}
