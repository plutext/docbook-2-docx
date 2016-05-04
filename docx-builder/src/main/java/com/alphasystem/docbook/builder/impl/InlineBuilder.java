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
import java.util.stream.Collectors;

import static com.alphasystem.util.AppUtil.isInstanceOf;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;

/**
 * @author sali
 */
public abstract class InlineBuilder<T> extends AbstractBuilder<T> {

    protected String styles[];

    protected InlineBuilder(Builder parent, T obj, int indexInParent) {
        super(parent, obj, indexInParent);
    }

    /**
     * Creates run properties for the given styles.
     *
     * @return run properties for the given styles
     */
    protected RPr handleStyles() {
        if (isEmpty(styles)) {
            return null;
        }
        RPrBuilder rPrBuilder;
        RPr rPr = null;
        for (String style : styles) {
            rPrBuilder = new RPrBuilder(handleStyle(style), rPr);
            rPr = rPrBuilder.getObject();
        }
        return rPr;
    }

    /**
     * Creates Run properties with given style.
     *
     * @param style name of style to apply
     * @return run properties with specified style applied
     */
    protected RPr handleStyle(String style) {
        if (style == null) {
            return null;
        }
        RPrBuilder rPrBuilder = WmlBuilderFactory.getRPrBuilder();
        String styleHandler = configurationUtils.getString(style);
        styleHandler = (styleHandler == null) ? style : styleHandler;

        Object o;

        // find whether we have color style with the given name
        final ColorCode colorCode = ColorCode.getByName(style);
        if (colorCode != null) {
            try {
                o = applicationController.handleScript("handleColor", rPrBuilder, colorCode.getCode());
                if (o != null) {
                    return ((RPrBuilder) o).getObject();
                }
            } catch (Exception e) {
                // ignore
            }
        }

        // may be we have style defined
        if (ApplicationController.getContext().getDocumentStyles().contains(style)) {
            try {
                o = applicationController.handleScript("handleStyle", rPrBuilder, style);
                if (o != null) {
                    return ((RPrBuilder) o).getObject();
                }
            } catch (Exception e) {
                // ignore
            }
        }

        // last resort we may have a function
        try {
            o = applicationController.handleScript(styleHandler, rPrBuilder);
            if (o != null) {
                return ((RPrBuilder) o).getObject();
            }
        } catch (Exception e) {
            // ignore
        }

        logger.warn("Not sure how to handle style \"{}\" in builder \"{}\".", style, getClass().getSimpleName());
        return rPrBuilder.getObject();
    }

    @SuppressWarnings({"unchecked"})
    protected List<R> processChildContent(Object childContent, RPr runProperties, int indexInParent) {
        final Builder builder = factory.getBuilder(this, childContent, indexInParent);
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
        return list.stream().map(r -> copyRun(r, null, runProperties)).collect(Collectors.toList());
    }


    protected List<R> processContent() {
        if (isNull(content) || content.isEmpty()) {
            return Collections.emptyList();
        }
        RPr rPr = handleStyles();
        List<R> resultRuns = new ArrayList<>();
        for (int i = 0; i < content.size(); i++) {
            final Object o = content.get(i);
            resultRuns.addAll(processChildContent(o, rPr, i));
        }
        return resultRuns;
    }

    private R copyRun(R src, R target, RPr rPr) {
        RBuilder rBuilder = new RBuilder(src, target);
        R result = rBuilder.getObject();
        final RPr tRPr = result.getRPr();
        rPr = (rPr == null) ? tRPr : new RPrBuilder(rPr, tRPr).getObject();
        result.setRPr(rPr);
        return result;
    }

    @Override
    public List<Object> buildContent() {
        return new ArrayList<>(processContent());
    }
}
