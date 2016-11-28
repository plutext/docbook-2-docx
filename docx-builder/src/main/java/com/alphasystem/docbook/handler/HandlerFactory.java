package com.alphasystem.docbook.handler;

import com.alphasystem.docbook.ApplicationController;
import com.alphasystem.docbook.handler.impl.ColorHandler;
import com.alphasystem.docbook.handler.impl.StyleHandler;
import com.alphasystem.docbook.model.ColorCode;

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
    private static Map<String, InlineStyleHandler> handlers = Collections.synchronizedMap(new HashMap<>());

    /**
     * Do not let any one instantiate this class.
     */
    private HandlerFactory() {
        ServiceLoader<HandlerService> loader = ServiceLoader.load(HandlerService.class);
        final Iterator<HandlerService> iterator = loader.iterator();
        while (iterator.hasNext()){
            final HandlerService service = iterator.next();
            System.out.println(service.getClass().getName());
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
        handlers.put(key, handler);
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
