package com.alphasystem.docbook.handler.impl;

import com.alphasystem.docbook.handler.Builder;
import com.alphasystem.openxml.builder.wml.RBuilder;
import org.docx4j.wml.P;
import org.docx4j.wml.R;

import java.util.List;

import static com.alphasystem.openxml.builder.wml.WmlBuilderFactory.getRBuilder;
import static com.alphasystem.openxml.builder.wml.WmlBuilderFactory.getRPrBuilder;
import static java.lang.String.format;

/**
 * @author sali
 */
public class RunBuilder<T> extends AbstractBuilder<T> {

    protected RunBuilder(Builder parent, T obj) {
        super(parent, obj);
    }

    protected R buildRun(R r, String role, List<Object> content) {
        final RBuilder rBuilder = getRBuilder(r);
        rBuilder.withRPr(getRPrBuilder(rBuilder.getObject().getRPr()).getObject());
        final RoleBuilder roleBuilder = factory.getRoleBuilder(this, role);
        r = roleBuilder.build(rBuilder.getObject());
        r = parseContent(r, content);
        return r;
    }

    @Override
    public List<R> buildRuns() {
        throw new UnsupportedOperationException(format("Method \"buildRuns\" is not supported in class \"%s\"", getClass().getName()));
    }

    @Override
    public P buildParagraph() {
        throw new UnsupportedOperationException(format("Method \"buildParagraph\" is not supported in class \"%s\"", getClass().getName()));
    }

    @Override
    public List<Object> buildContent() {
        throw new UnsupportedOperationException(format("Method \"buildContent\" is not supported in class \"%s\"", getClass().getName()));
    }
}
