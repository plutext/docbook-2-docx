package com.alphasystem.docbook;

import org.docbook.model.Article;
import org.docbook.model.Book;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

import static com.alphasystem.util.AppUtil.isInstanceOf;
import static com.alphasystem.xml.UnmarshallerUtil.unmarshal;

/**
 * @author sali
 */
public final class DocBookUtil {

    public static Book getBook(String systemId) throws SAXException, JAXBException,
            ParserConfigurationException, IOException {
        return unmarshal(systemId, Book.class);
    }

    public static Article getArticle(String systemId) throws SAXException, JAXBException,
            ParserConfigurationException, IOException {
        return unmarshal(systemId, Article.class);
    }

    public static Object getDocument(String systemId) {
        Object document = null;
        try {
            document = getBook(systemId);
        } catch (Exception ex) {
            try {
                document = getArticle(systemId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return document;
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T getContent(Class<T> declaredType, List<Object> content) {
        T o = null;
        if (content == null) {
            return o;
        }
        for (Object obj : content) {
            if (isInstanceOf(declaredType, obj)) {
                o = (T) obj;
                break;
            }
        }
        return o;
    }
}
