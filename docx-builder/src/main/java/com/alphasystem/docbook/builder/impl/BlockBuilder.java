package com.alphasystem.docbook.builder.impl;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.openxml.builder.wml.PBuilder;
import com.alphasystem.openxml.builder.wml.WmlBuilderFactory;
import org.docx4j.wml.PPr;

import java.util.ArrayList;
import java.util.List;

import static com.alphasystem.util.AppUtil.isInstanceOf;
import static java.util.Objects.isNull;

/**
 * @author sali
 */
public abstract class BlockBuilder<T> extends AbstractBuilder<T> {

    protected PPr paraProperties;
    protected PPr listParaProperties;

    protected BlockBuilder(Builder parent, T obj) {
        super(parent, obj);
    }

    public PPr getParaProperties() {
        return paraProperties;
    }

    public PPr getListParaProperties() {
        return listParaProperties;
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
        postProcessTitleContent(processedTitleContent, result);
        postProcessContent(processedChildContent, result);
        return result;
    }

    protected void postProcessTitleContent(List<Object> processedTitleContent, List<Object> result) {
        result.addAll(processedTitleContent);
    }

    protected void postProcessContent(List<Object> processedChildContent, List<Object> result) {
        result.addAll(processedChildContent);
    }

    /**
     * Iterates through the child content of source object calls {@link #buildContent()} on each builder and adds the
     * processed content into the result.
     *
     * @param content the child content of the source object.
     * @param target  list of all processed child content
     */
    protected void parseContent(List<Object> content, List<Object> target) {
        if (isNull(content) || content.isEmpty()) {
            return;
        }
        PBuilder pBuilder = null;
        for (int i = 0; i < content.size(); i++) {
            final Object o = content.get(i);
            final Builder builder = factory.getBuilder(this, o);
            if (builder == null) {
                logUnhandledContentWarning(o);
                continue;
            }
            final List<Object> childContent = buildChildContent(builder, i);
            // take all consecutive inline items and create a para
            if (isInstanceOf(InlineBuilder.class, builder)) {
                if (pBuilder == null) {
                    pBuilder = WmlBuilderFactory.getPBuilder();
                }
                pBuilder.addContent(childContent.toArray());
            } else {
                // we found a BlockBuilder, we might have been updating pBuilder with the running text, now it is a
                // good time to add that in the result
                if (pBuilder != null) {
                    target.add(pBuilder.getObject());
                }

                // add content of block as well
                target.addAll(childContent);
            }
        } // end of for loop

        // at the end of all this me might not have added content of pBuilder into result, add it now
        if (pBuilder != null) {
            target.add(pBuilder.getObject());
        }
    }

    @SuppressWarnings({"unchecked"})
    protected List<Object> buildChildContent(Builder builder, int iteration) {
        // TODO: need to revisit later
        return builder.buildContent();
    }
}
