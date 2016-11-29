package com.alphasystem.docbook.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author sali
 */
public class BlockHandlerFactory {

    public static final String EXAMPLE_KEY = "example";
    public static final String INFORMAL_EXAMPLE_KEY = "informal_example";
    public static final String SIDE_BAR_KEY = "side_bar";

    private static BlockHandlerFactory instance;
    private static Logger logger = LoggerFactory.getLogger(BlockHandlerFactory.class);
    private static Map<String, BlockHandler> handlers = Collections.synchronizedMap(new HashMap<>());

    private BlockHandlerFactory() {
        ServiceLoader<BlockHandlerService> loader = ServiceLoader.load(BlockHandlerService.class);
        loader.forEach(BlockHandlerService::initializeHandlers);
    }

    public synchronized static BlockHandlerFactory getInstance() {
        if (instance == null) {
            instance = new BlockHandlerFactory();
        }
        return instance;
    }

    public static void registerHandler(String key, BlockHandler handler) {
        if (handler == null || key == null) {
            return;
        }
        // service handler loads service from the resources of the client jar then it comes to docx-builder
        // we would like to honour handler(s) from the client first, so that client can override default handler(s)
        final BlockHandler existingHandler = handlers.get(key);
        if (existingHandler == null) {
            logger.info("Loading handler \"{}\" as key \"{}\"", handler.getClass().getName(), key);
            handlers.put(key, handler);
        }
    }

    public BlockHandler getHandler(String kety) {
        return handlers.get(kety);
    }
}
