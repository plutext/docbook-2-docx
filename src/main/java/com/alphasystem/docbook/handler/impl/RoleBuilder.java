package com.alphasystem.docbook.handler.impl;

import com.alphasystem.docbook.ApplicationController;
import com.alphasystem.docbook.handler.Builder;
import com.alphasystem.openxml.builder.wml.RPrBuilder;
import org.docx4j.wml.R;

import javax.script.ScriptException;

import static com.alphasystem.openxml.builder.wml.WmlBuilderFactory.getRPrBuilder;
import static java.lang.String.format;

/**
 * @author sali
 */
public class RoleBuilder extends AbstractBuilder<String> {

    public RoleBuilder(Builder parent, String role) {
        super(parent, role);
    }

    @Override
    public R build(R r) {
        RPrBuilder rPrBuilder = getRPrBuilder(r.getRPr());
        String styleHandler = applicationController.getConfiguration().getString(obj);
        String styleName = null;
        logger.info(format("%s: %s", obj, styleHandler));
        if (styleHandler == null) {
            if (ApplicationController.getContext().getDocumentStyles().contains(obj)) {
                styleHandler = "handleStyle";
                styleName = obj;
            } else {
                styleHandler = obj;
            }
        }
        try {
            rPrBuilder = applicationController.applyStyle(rPrBuilder, styleHandler, styleName);
        } catch (ScriptException | NoSuchMethodException e) {
            logger.warn("Not sure how to handle style \"%s\"", obj);
        }
        r.setRPr(rPrBuilder.getObject());
        return r;
    }

}
