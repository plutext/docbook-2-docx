package com.alphasystem.docbook.handler.impl;

import com.alphasystem.docbook.handler.Builder;
import org.docbook.model.Article;
import org.docbook.model.Info;
import org.docbook.model.Title;

import java.util.ArrayList;
import java.util.List;

import static com.alphasystem.docbook.DocBookUtil.getContent;
import static com.alphasystem.openxml.builder.wml.WmlAdapter.getEmptyPara;

/**
 * @author sali
 */
public class ArticleBuilder extends AbstractBuilder<Article> {

    public ArticleBuilder(Builder parent, Article article) {
        super(parent, article);
    }

    @Override
    public List<Object> buildContent() {
        List<Object> paras = new ArrayList<>();

        final List<Object> content = obj.getContent();
        final Info info = getContent(Info.class, content);
        if (info != null) {
            final Title title = getContent(Title.class, info.getContent());
            if (title != null) {
                paras.add(factory.getBuilder(this, title).buildParagraph());
                paras.add(getEmptyPara());
            }
        }
        parseContent(content, paras);
        return paras;
    }
}
