package com.alphasystem.docbook.handler;

import com.alphasystem.docbook.builder.Builder;

/**
 * @author sali
 */
public interface BuilderHandler<O, B extends Builder<O>> extends Handler {

    Class<O> getModelClass();
    Class<B> getBuilderClass();
}
