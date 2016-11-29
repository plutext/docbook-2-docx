package com.alphasystem.docbook.handler.impl.inline;

import com.alphasystem.docbook.handler.InlineHandlerFactory;
import com.alphasystem.docbook.handler.InlineHandlerService;

/**
 * @author sali
 */
public class BaseInlineInlineHandlerService extends InlineHandlerService {

    @Override
    public void initializeHandlers() {
        final BoldHandler handler = new BoldHandler();
        InlineHandlerFactory.registerHandler(InlineHandlerFactory.STRONG, handler);
        InlineHandlerFactory.registerHandler(InlineHandlerFactory.BOLD, handler);
        InlineHandlerFactory.registerHandler(InlineHandlerFactory.HYPERLINK, new HyperlinkHandler());
        InlineHandlerFactory.registerHandler(InlineHandlerFactory.ITALIC, new ItalicHandler());
        InlineHandlerFactory.registerHandler(InlineHandlerFactory.LITERAL, new LiteralHandler());
        InlineHandlerFactory.registerHandler(InlineHandlerFactory.LINE_THROUGH, new StrikeThroughHandler());
        InlineHandlerFactory.registerHandler(InlineHandlerFactory.SUBSCRIPT, new SubscriptHandler());
        InlineHandlerFactory.registerHandler(InlineHandlerFactory.SUPERSCRIPT, new SuperscriptHandler());
        InlineHandlerFactory.registerHandler(InlineHandlerFactory.UNDERLINE, new UnderlineHandler());
    }
}