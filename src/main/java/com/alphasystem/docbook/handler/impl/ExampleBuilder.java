package com.alphasystem.docbook.handler.impl;

import com.alphasystem.docbook.handler.Builder;
import org.docbook.model.Example;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;

import static com.alphasystem.openxml.builder.wml.WmlAdapter.getEmptyPara;
import static java.util.Objects.nonNull;

/**
 * @author sali
 */
public class ExampleBuilder extends AbstractBuilder<Example> {

    public ExampleBuilder(Builder parent, Example example) {
        super(parent, example);
    }

    @Override
    public List<Object> buildContent() {
        List<Object> content = new ArrayList<>();

        final List<Object> titleContent = obj.getTitleContent();
        if (nonNull(titleContent) && !titleContent.isEmpty()) {
            titleContent.forEach(o -> {
                final Builder titleBuilder = factory.getBuilder(this, o);
                if (titleBuilder == null) {
                    logUnhandledContentWarning(o);
                } else {
                    content.add(titleBuilder.buildParagraph());
                }
            });
        }

        final Tbl tbl = applicationController.getExampleTable();
        final Tr tr = (Tr) tbl.getContent().get(0);
        final JAXBElement e = (JAXBElement) tr.getContent().get(0);
        final Tc tc = (Tc) e.getValue();

        final List<Object> childContent = new ArrayList<>();
        parseContent(obj.getContent(), childContent);
        childContent.forEach(o -> tc.getContent().add(o));
        tc.getContent().add(getEmptyPara());
        content.add(tbl);
        content.add(getEmptyPara());
        return content;
    }
}
