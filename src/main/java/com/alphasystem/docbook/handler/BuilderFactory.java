package com.alphasystem.docbook.handler;

import com.alphasystem.docbook.ApplicationController;
import com.alphasystem.docbook.DocumentContext;
import com.alphasystem.docbook.handler.impl.RoleBuilder;
import com.alphasystem.docbook.handler.impl.TextBuilder;
import com.alphasystem.xml.DocumentInfo;
import org.docbook.model.Article;

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

    /**
     * Do not let any one instantiate this class.
     */
    private BuilderFactory() {
    }

    public Builder getBuilder(Builder parent, Object o) {
        if (o == null) {
            return null;
        }
        if (isInstanceOf(String.class, o)) {
            return new TextBuilder(parent, (String) o);
        } else {
            String builderFqn = format("com.alphasystem.docbook.handler.impl.%sBuilder", o.getClass().getSimpleName());
            Class<?> builderClass;
            try {
                builderClass = Class.forName(builderFqn);
                final Constructor<?> constructor = builderClass.getConstructor(Builder.class, o.getClass());
                return (Builder) constructor.newInstance(parent, o);
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                    InstantiationException | InvocationTargetException e) {
                return null;
            }
        }
    }

    public RoleBuilder getRoleBuilder(Builder parent, String role) {
        return new RoleBuilder(parent, role);
    }

    public List<Object> buildDocument() {
        DocumentContext documentContext = ApplicationController.getContext();
        final DocumentInfo documentInfo = documentContext.getDocumentInfo();
        if (documentContext.isArticle()) {
            return handleArticle((Article) documentInfo.getDocument());
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
