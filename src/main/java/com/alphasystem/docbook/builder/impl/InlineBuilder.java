package com.alphasystem.docbook.builder.impl;

import com.alphasystem.docbook.ApplicationController;
import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.openxml.builder.wml.RBuilder;
import com.alphasystem.openxml.builder.wml.RPrBuilder;
import com.alphasystem.openxml.builder.wml.WmlBuilderFactory;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;

import javax.script.ScriptException;
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
        boolean styleHandled = true;
        try {
            rPrBuilder = (RPrBuilder) applicationController.handleScript(styleHandler, rPrBuilder);
        } catch (ScriptException | NoSuchMethodException ex) {
            styleHandled = false;
        }
        if (!styleHandled && ApplicationController.getContext().getDocumentStyles().contains(style)) {
            try {
                rPrBuilder = (RPrBuilder) applicationController.handleScript("handleStyle", rPrBuilder, style);
            } catch (ScriptException | NoSuchMethodException ex) {
                styleHandled = false;
            }
        }
        if (!styleHandled) {
            logger.warn("Not sure how to handle style \"{}\" in builder \"{}\".", style, builderName);
        }
        return rPrBuilder.getObject();
    }

    protected R handleChildContent() {
        if (isNull(content) || content.isEmpty()) {
            return null;
        }
        R run = null;
        RPr rPr = handleStyle(style);
        for (Object o : content) {
            final Builder builder = factory.getBuilder(this, o);
            if (builder == null) {
                logUnhandledContentWarning(o);
                continue;
            }
            if (!isInstanceOf(InlineBuilder.class, builder)) {
                throw new RuntimeException(format("\"%s\" does not implement InlineBuilder in builder \"%s\"",
                        o.getClass().getSimpleName(), getClass().getSimpleName()));
            }
            InlineBuilder inlineBuilder = (InlineBuilder) builder;
            final R src = inlineBuilder.handleChildContent();
            RBuilder rBuilder = new RBuilder(src, run);
            run = rBuilder.getObject();
            RPrBuilder rPrBuilder = new RPrBuilder(run.getRPr(), rPr);
            rPr = rPrBuilder.getObject();
            run.setRPr(rPr);
        }
        return run;
    }

    @Override
    public List<Object> buildContent() {
        return Collections.singletonList(handleChildContent());
    }
}
