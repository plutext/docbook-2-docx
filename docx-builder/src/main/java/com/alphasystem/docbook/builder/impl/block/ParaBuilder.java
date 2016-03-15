package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import org.docbook.model.Para;
import org.docx4j.wml.P;

import java.util.ArrayList;
import java.util.List;

import static com.alphasystem.util.AppUtil.isInstanceOf;

/**
 * @author sali
 */
public class ParaBuilder extends AbstractParaBuilder<Para> {

    public ParaBuilder(Builder parent, Para obj) {
        super(parent, obj);
    }

    @Override
    protected void initContent() {
        content = source.getContent();
    }

    @Override
    protected List<Object> postProcess(List<Object> processedTitleContent, List<Object> processedChildContent) {
        List<Object> result = new ArrayList<>();
        processedChildContent.forEach(o -> {
            if (isInstanceOf(P.class, o)) {
                result.add(addParaProperties((P) o));
            } else {
                result.add(o);
            }
        });
        return result;
    }
}
