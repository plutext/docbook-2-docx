package com.alphasystem.docbook.builder.impl;

import com.alphasystem.docbook.ApplicationController;
import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.model.ColorCode;
import com.alphasystem.openxml.builder.wml.RBuilder;
import com.alphasystem.openxml.builder.wml.RPrBuilder;
import com.alphasystem.openxml.builder.wml.WmlBuilderFactory;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.alphasystem.util.AppUtil.isInstanceOf;
import static java.lang.String.format;
import static java.util.Objects.isNull;

/**
 * @author sali
 */
public abstract class InlineBuilder<T> extends AbstractBuilder<T> {

    protected String style;

    protected InlineBuilder(Builder parent, T obj) {
        super(parent, obj);
    }

    /**
     * @param style name of style to apply
     * @return run properties with specified style applied
     */
    protected RPr handleStyle(String style) {
        final String builderName = getClass().getSimpleName();
        if (style == null) {
            return null;
        }
        RPrBuilder rPrBuilder = WmlBuilderFactory.getRPrBuilder();
        String styleHandler = applicationController.getConfiguration().getString(style);
        styleHandler = (styleHandler == null) ? style : styleHandler;

        // assume we have a function
        Object o = applicationController.handleScript(styleHandler, rPrBuilder);
        if (o != null) {
            return ((RPrBuilder) o).getObject();
        }

        final ColorCode colorCode = ColorCode.getByName(style);
        if (colorCode != null) {
            o = applicationController.handleScript("handleColor", rPrBuilder, colorCode.getCode());
            if (o != null) {
                return ((RPrBuilder) o).getObject();
            }
        }

        if (ApplicationController.getContext().getDocumentStyles().contains(style)) {
            o = applicationController.handleScript("handleStyle", rPrBuilder, style);
            if (o != null) {
                return ((RPrBuilder) o).getObject();
            }
        }
        logger.warn("Not sure how to handle style \"{}\" in builder \"{}\".", style, builderName);
        return rPrBuilder.getObject();
    }

    protected List<R> processChildContent(Object childContent, RPr runProperties) {
        final Builder builder = factory.getBuilder(this, childContent);
        if (builder == null) {
            logUnhandledContentWarning(childContent);
            return Collections.emptyList();
        }
        if (!isInstanceOf(InlineBuilder.class, builder)) {
            throw new RuntimeException(format("\"%s\" does not implement InlineBuilder in builder \"%s\"",
                    childContent.getClass().getSimpleName(), getClass().getSimpleName()));
        }
        InlineBuilder inlineBuilder = (InlineBuilder) builder;
        final List<R> list = inlineBuilder.processContent();
        List<R> content = new ArrayList<>();
        for (R r : list) {
            content.add(copyRun(r, null, runProperties));
        }
        return content;
    }


    protected List<R> processContent() {
        if (isNull(content) || content.isEmpty()) {
            return Collections.emptyList();
        }
        RPr rPr = handleStyle(style);
        List<R> resultRuns = new ArrayList<>();
        for (Object o : content) {
            resultRuns.addAll(processChildContent(o, rPr));
        }
        return resultRuns;
    }

    private R copyRun(R src, R target, RPr rPr) {
        RBuilder rBuilder = new RBuilder(src, target);
        R result = rBuilder.getObject();
        try {
            rPr = new RPrBuilder(rPr, result.getRPr()).getObject();
        } catch (NullPointerException e) {
            // ignore
        }

        result.setRPr(rPr);
        return result;
    }

    @Override
    public List<Object> buildContent() {
        return new ArrayList<>(processContent());
    }
}
