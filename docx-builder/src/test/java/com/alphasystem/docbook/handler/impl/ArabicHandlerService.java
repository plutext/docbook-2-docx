package com.alphasystem.docbook.handler.impl;

import com.alphasystem.docbook.handler.HandlerFactory;
import com.alphasystem.docbook.handler.HandlerService;

/**
 * @author sali
 */
public class ArabicHandlerService extends HandlerService {

    @Override
    public void initializeHandlers() {
        HandlerFactory.registerHandler(ArabicHandler.ARABIC_HEADING_1, new ArabicHeading1Handler());
        HandlerFactory.registerHandler(ArabicHandler.ARABIC_NORMAL, new ArabicNormalHandler());
        HandlerFactory.registerHandler(ArabicHandler.ARABIC_TABLE_CAPTION, new ArabicTableCaptionHandler());
    }
}
