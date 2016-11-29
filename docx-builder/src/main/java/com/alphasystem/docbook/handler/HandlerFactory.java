package com.alphasystem.docbook.handler;

import com.alphasystem.docbook.ApplicationController;
import com.alphasystem.docbook.handler.impl.ColorHandler;
import com.alphasystem.docbook.handler.impl.StyleHandler;
import com.alphasystem.docbook.model.ColorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author sali
 */
public final class HandlerFactory {

    public static final String BOLD = "bold";
    public static final String STRONG = "strong";
    public static final String ITALIC = "italic";
    public static final String UNDERLINE = "underline";
    public static final String LINE_THROUGH = "line-through";
    public static final String LITERAL = "literal";
    public static final String SUBSCRIPT = "subscript";
    public static final String SUPERSCRIPT = "superscript";
    public static final String HYPERLINK = "hyperlink";

    private static HandlerFactory instance;
    private static Logger logger = LoggerFactory.getLogger(HandlerFactory.class);
    private static Map<String, InlineStyleHandler> handlers = Collections.synchronizedMap(new HashMap<>());

    /**
     * Do not let any one instantiate this class.
     */
    private HandlerFactory() {
        ServiceLoader<HandlerService> loader = ServiceLoader.load(HandlerService.class);
        final Iterator<HandlerService> iterator = loader.iterator();
        while (iterator.hasNext()) {
            final HandlerService service = iterator.next();
            service.initializeHandlers();
        }
    }

    public static synchronized HandlerFactory getInstance() {
        if (instance == null) {
            instance = new HandlerFactory();
        }
        return instance;
    }

    public static void registerHandler(String key, InlineStyleHandler handler) {
        if (handler == null || key == null) {
            return;
        }
        // service handler loads service from the resources of the client jar then it comes to docx-builder
        // we would like to honour handler(s) from the client first, so that client can override default handler(s)
        final InlineStyleHandler existingHandler = handlers.get(key);
        if (existingHandler == null) {
            logger.info("Loading handler \"{}\" as key \"{}\"", handler.getClass().getName(), key);
            handlers.put(key, handler);
        }
    }

    public InlineStyleHandler getHandler(String style) {
        InlineStyleHandler handler = handlers.get(style);
        if (handler == null) {
            final ColorCode colorCode = ColorCode.getByName(style);
            if (colorCode != null) {
                handler = new ColorHandler(colorCode);
                handlers.put(colorCode.getName(), handler);
            } else if (ApplicationController.getContext().getDocumentStyles().contains(style)) {
                handler = new StyleHandler(style);
                handlers.put(style, handler);
            }
        }
        return handler;
    }
}
