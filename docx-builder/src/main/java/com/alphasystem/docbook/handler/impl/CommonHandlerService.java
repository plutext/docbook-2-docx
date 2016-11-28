package com.alphasystem.docbook.handler.impl;

import com.alphasystem.docbook.handler.HandlerFactory;
import com.alphasystem.docbook.handler.HandlerService;

/**
 * @author sali
 */
public class CommonHandlerService extends HandlerService {

    @Override
    public void initializeHandlers() {
        final BoldHandler handler = new BoldHandler();
        HandlerFactory.registerHandler(HandlerFactory.STRONG, handler);
        HandlerFactory.registerHandler(HandlerFactory.BOLD, handler);
        HandlerFactory.registerHandler(HandlerFactory.HYPERLINK, new HyperlinkHandler());
        HandlerFactory.registerHandler(HandlerFactory.ITALIC, new ItalicHandler());
        HandlerFactory.registerHandler(HandlerFactory.LITERAL, new LiteralHandler());
        HandlerFactory.registerHandler(HandlerFactory.LINE_THROUGH, new StrikeThroughHandler());
        HandlerFactory.registerHandler(HandlerFactory.SUBSCRIPT, new SubscriptHandler());
        HandlerFactory.registerHandler(HandlerFactory.SUPERSCRIPT, new SuperscriptHandler());
        HandlerFactory.registerHandler(HandlerFactory.UNDERLINE, new UnderlineHandler());
    }
}
