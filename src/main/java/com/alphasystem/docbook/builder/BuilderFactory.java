package com.alphasystem.docbook.builder;

import com.alphasystem.docbook.ApplicationController;
import com.alphasystem.docbook.DocumentContext;
import com.alphasystem.docbook.builder.impl.AbstractBuilder;
import com.alphasystem.docbook.builder.impl.BlockBuilder;
import com.alphasystem.docbook.builder.impl.InlineBuilder;
import org.docbook.model.Article;
import org.docbook.model.Title;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.alphasystem.util.AppUtil.isInstanceOf;
import static java.lang.String.format;

/**
 * @author sali
 */
public class BuilderFactory {

    private static BuilderFactory instance;

    public static synchronized BuilderFactory getInstance() {
        if (instance == null) {
            instance = new BuilderFactory();
        }
        return instance;
    }

    private ApplicationController applicationController = ApplicationController.getInstance();

    /**
     * Do not let any one instantiate this class.
     */
    private BuilderFactory() {
    }

    public Builder getBuilder(Builder parent, Object o) {
        if (o == null) {
            return null;
        }

        String sourceName = o.getClass().getName();
        if (isInstanceOf(Title.class, o)) {
            String parentName = ((parent == null) || isInstanceOf(BlockBuilder.class, parent)) ?
                    BlockBuilder.class.getSimpleName() : InlineBuilder.class.getSimpleName();
            sourceName = format("%s.%s", parentName, sourceName);
        }
        String builderFqn = applicationController.getConfiguration().getString(sourceName);
        if (builderFqn == null) {
            return null;
        }
        AbstractBuilder builder = null;
        try {
            Class<?> builderClass = Class.forName(builderFqn);
            final Constructor<?> constructor = builderClass.getConstructor(Builder.class, o.getClass());
            builder = (AbstractBuilder) constructor.newInstance(parent, o);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                InstantiationException | InvocationTargetException e) {
            // ignore
        }
        return builder;
    }

    public List<Object> buildDocument() {
        DocumentContext documentContext = ApplicationController.getContext();
        if (documentContext.isArticle()) {
            return handleArticle((Article) documentContext.getDocument());
        }
        return Collections.emptyList();
    }

    private List<Object> handleArticle(Article article) {
        List<Object> paras = new ArrayList<>();

        final List<Object> content = article.getContent();
        if (content == null || content.isEmpty()) {
            return paras;
        }

        paras.addAll(getBuilder(null, article).buildContent());
        return paras;
    }

}
