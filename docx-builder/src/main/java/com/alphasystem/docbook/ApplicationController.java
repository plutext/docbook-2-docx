package com.alphasystem.docbook;

import com.alphasystem.docbook.builder.model.Admonition;
import com.alphasystem.docbook.handler.BlockHandlerFactory;
import com.alphasystem.docbook.handler.InlineHandlerFactory;
import com.alphasystem.docbook.util.ConfigurationUtils;
import org.docx4j.wml.Tbl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

import static com.alphasystem.docbook.handler.BlockHandlerFactory.*;
import static com.alphasystem.util.nio.NIOFileUtils.USER_DIR;
import static java.nio.file.Paths.get;

/**
 * @author sali
 */
public final class ApplicationController {

    private static final String CONF = "conf";
    private static final String CONF_DIR = System.getProperty("conf.path", USER_DIR);
    private static final Path CONF_PATH = get(CONF_DIR, CONF);
    public static final String CONF_PATH_VALUE = CONF_PATH.toString();
    private static final ThreadLocal<DocumentContext> CONTEXT = new ThreadLocal<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationController.class);
    private static ApplicationController instance;

    public static void startContext(DocumentContext documentContext) {
        CONTEXT.set(documentContext);
    }

    public static DocumentContext getContext() {
        return CONTEXT.get();
    }

    public static void endContext() {
        CONTEXT.remove();
    }

    public static synchronized ApplicationController getInstance() {
        if (instance == null) {
            instance = new ApplicationController();
        }
        return instance;
    }

    private BlockHandlerFactory blockHandlerFactory;

    /**
     * Do not let anyone instantiate this class
     */
    private ApplicationController() {
        // initialize singletons
        ConfigurationUtils.getInstance();
        blockHandlerFactory = BlockHandlerFactory.getInstance();
        InlineHandlerFactory.getInstance();
    }

    public Tbl getExampleTable() {
        return (Tbl) blockHandlerFactory.getHandler(EXAMPLE_KEY).handleBlock();
    }

    public Tbl getInformalExampleTable() {
        return (Tbl) blockHandlerFactory.getHandler(INFORMAL_EXAMPLE_KEY).handleBlock();
    }

    public Tbl getSideBarTable() {
        return (Tbl) blockHandlerFactory.getHandler(SIDE_BAR_KEY).handleBlock();
    }

    public Tbl getAdmonitionTable(Admonition admonition) {
        return (Tbl) blockHandlerFactory.getHandler(admonition.name()).handleBlock();
    }

}
