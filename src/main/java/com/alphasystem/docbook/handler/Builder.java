package com.alphasystem.docbook.handler;

import org.docx4j.wml.P;
import org.docx4j.wml.R;

import java.util.List;

/**
 * @author sali
 */
public interface Builder<T> {

    R build(R r);

    List<R> buildRuns();

    P buildParagraph();

    List<Object> buildContent();

    Builder getParent();

}
