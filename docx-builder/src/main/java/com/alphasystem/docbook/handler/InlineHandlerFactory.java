package com.alphasystem.docbook.handler;

import com.alphasystem.docbook.ApplicationController;
import com.alphasystem.docbook.handler.impl.inline.ColorHandler;
import com.alphasystem.docbook.handler.impl.inline.StyleHandler;
import com.alphasystem.docbook.model.ColorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author sali
 */
public final class InlineHandlerFactory {

    public static final String BOLD = "bold";
    public static final String STRONG = "strong";
    public static final String ITALIC = "italic";
    public static final String UNDERLINE = "underline";
    public static final String LINE_THROUGH = "line-through";
    public static final String LITERAL = "literal";
    public static final String SUBSCRIPT = "subscript";
    public static final String SUPERSCRIPT = "superscript";
    public static final String HYPERLINK = "hyperlink";

    private static InlineHandlerFactory instance;
    private static Logger logger = LoggerFactory.getLogger(InlineHandlerFactory.class);
    private static Map<String, InlineStyleHandler> handlers = Collections.synchronizedMap(new HashMap<>());

    /**
     * Do not let any one instantiate this class.
     */
    private InlineHandlerFactory() {
        ServiceLoader<HandlerService> loader = ServiceLoader.load(HandlerService.class);
        for (HandlerService service : loader) {
            service.initializeHandlers();
        }
    }

    public static synchronized InlineHandlerFactory getInstance() {
        if (instance == null) {
            instance = new InlineHandlerFactory();
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
