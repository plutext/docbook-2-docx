package com.alphasystem.docbook.handler.impl;


import com.alphasystem.docbook.ApplicationController;
import com.alphasystem.docbook.handler.Builder;
import com.alphasystem.docbook.handler.BuilderFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author sali
 */
public abstract class AbstractBuilder<T> implements Builder<T> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final ApplicationController applicationController = ApplicationController.getInstance();
    protected final BuilderFactory factory = BuilderFactory.getInstance();
    protected final Builder parent;
    protected T obj;

    protected AbstractBuilder(Builder parent, T obj) {
        this.parent = parent;
        this.obj = obj;
    }

    @Override
    public R build(R r) {
        return r;
    }

    @Override
    public List<R> buildRuns() {
        return emptyList();
    }

    @Override
    public P buildParagraph() {
        return null;
    }

    @Override
    public List<Object> buildContent() {
        return emptyList();
    }

    @Override
    public Builder getParent() {
        return parent;
    }

    protected R parseContent(R r, List<Object> content) {
        if (content == null || content.isEmpty()) {
            return r;
        }
        for (Object o : content) {
            final Builder builder = factory.getBuilder(this, o);
            if (builder == null) {
                logUnhandledContentWarning(o);
            } else {
                r = builder.build(r);
            }
        }
        return r;
    }

    protected void parseContent(List<Object> content, List<Object> target) {
        if (isNull(content) || content.isEmpty()) {
            return;
        }
        for (Object o : content) {
            final Builder builder = factory.getBuilder(this, o);
            if (builder == null) {
                logUnhandledContentWarning(o);
            } else {
                final P p = builder.buildParagraph();
                if (nonNull(p)) {
                    target.add(p);
                }
                final List buildContent = builder.buildContent();
                if (nonNull(buildContent) && !buildContent.isEmpty()) {
                    target.addAll(buildContent);
                }
            }
        }
    }

    protected void logUnhandledContentWarning(Object o) {
        logger.warn("Unhandled type \"{}\" in builder \"{}\"", o.getClass().getName(), getClass().getName());
    }


}
