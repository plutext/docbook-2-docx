package com.alphasystem.docbook.builder.impl;

import com.alphasystem.docbook.builder.Builder;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author sali
 */
public abstract class BlockBuilder<T> extends AbstractBuilder<T> {

    protected BlockBuilder(Builder parent, T obj) {
        super(parent, obj);
    }

    @Override
    public List<Object> buildContent() {
        preProcess();

        // process title first, if applicable
        final List<Object> processedTitleContent = processTitleContent();

        // process child content
        final List<Object> processedChildContent = doProcess();

        // do post processing here
        return postProcess(processedTitleContent, processedChildContent);
    }

    /**
     * Any pre processing before start parsing content.
     */
    protected void preProcess() {
    }

    /**
     * Process any title content.
     *
     * @return processed list of title content.
     */
    protected List<Object> processTitleContent() {
        List<Object> result = new ArrayList<>();
        parseContent(titleContent, result);
        return result;
    }

    /**
     * Process any child content.
     *
     * @return processed list of content.
     */
    protected List<Object> doProcess() {
        List<Object> result = new ArrayList<>();
        parseContent(content, result);
        return result;
    }

    /**
     * Post process of result. Combines results of processing of title contents and child content in a fashion
     * appropriate to current builder.
     *
     * @param processedTitleContent list of processed title content, optional
     * @param processedChildContent list of processed child content
     * @return final list of processed child content which will go in the out put
     */
    protected List<Object> postProcess(List<Object> processedTitleContent, List<Object> processedChildContent) {
        List<Object> result = new ArrayList<>();
        result.addAll(processedTitleContent);
        result.addAll(processedChildContent);
        return result;
    }

    protected void parseContent(List<Object> content, List<Object> target) {
        if (isNull(content) || content.isEmpty()) {
            return;
        }
        for (Object o : content) {
            final Builder builder = factory.getBuilder(this, o);
            if (builder == null) {
                logUnhandledContentWarning(o);
                continue;
            }
            final List buildContent = builder.buildContent();
            if (nonNull(buildContent) && !buildContent.isEmpty()) {
                target.addAll(buildContent);
            }
        }
    }
}
