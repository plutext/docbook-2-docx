package com.alphasystem.docbook.handler.impl;

import com.alphasystem.docbook.handler.Builder;
import org.docbook.model.SideBar;
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
public class SideBarBuilder extends AbstractBuilder<SideBar> {

    public SideBarBuilder(Builder parent, SideBar obj) {
        super(parent, obj);
    }

    @Override
    public List<Object> buildContent() {
        List<Object> result = new ArrayList<>();

        final Tbl tbl = applicationController.getSideBarTable();
        final Tr tr = (Tr) tbl.getContent().get(0);
        final JAXBElement e = (JAXBElement) tr.getContent().get(0);
        final Tc tc = (Tc) e.getValue();
        tc.getContent().clear();

        final List<Object> childContent = new ArrayList<>();
        parseContent(obj.getContent(), childContent);

        final List<Object> titleContent = obj.getTitleContent();
        if (nonNull(titleContent) && !titleContent.isEmpty()) {
            titleContent.forEach(o -> {
                final Builder builder = factory.getBuilder(this, o);
                if (builder == null) {
                    logUnhandledContentWarning(o);
                } else {
                    tc.getContent().add(builder.buildParagraph());
                }
            });
        }

        childContent.forEach(o -> tc.getContent().add(o));
        tc.getContent().add(getEmptyPara());
        result.add(tbl);
        result.add(getEmptyPara());
        return result;
    }
}
