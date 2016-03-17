package com.alphasystem.docbook.builder.test;

import org.docbook.model.*;

/**
 * @author sali
 */
public final class DataFactory {

    private static ObjectFactory objectFactory = new ObjectFactory();

    public static Article createEmptyArticle() {
        return objectFactory.createArticle();
    }

    public static Emphasis createBold(Object... content) {
        return createEmphasis("strong", content);
    }

    public static Emphasis createEmphasis(String role, Object... content) {
        return objectFactory.createEmphasis().withRole(role).withContent(content);
    }

    public static Emphasis createItalic(Object... content) {
        return createEmphasis(null, content);
    }

    public static ListItem createListItem(Object... content) {
        return createListItem(null, content);
    }

    public static ListItem createListItem(String id, Object... content) {
        return objectFactory.createListItem().withId(id).withContent(content);
    }

    public static SimplePara createSimplePara(Object... content) {
        return createSimplePara(null, content);
    }

    public static SimplePara createSimplePara(String id, Object... content) {
        return objectFactory.createSimplePara().withId(id).withContent(content);
    }

    public static Term createTerm(Object... content) {
        return objectFactory.createTerm().withContent(content);
    }

    public static VariableList createVariableList(String id, Object[] content, VariableListEntry... entries) {
        return objectFactory.createVariableList().withId(id).withContent(content).withVariableListEntry(entries);
    }

    public static VariableListEntry createVariableListEntry(ListItem listItem, Term... terms) {
        return objectFactory.createVariableListEntry().withTerm(terms).withListItem(listItem);
    }
}
