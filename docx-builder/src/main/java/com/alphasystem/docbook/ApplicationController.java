package com.alphasystem.docbook;

import com.alphasystem.asciidoc.model.AsciiDocumentInfo;
import com.alphasystem.docbook.builder.model.Admonition;
import com.alphasystem.docbook.util.ConfigurationUtils;
import org.docx4j.wml.Tbl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Path;

import static com.alphasystem.util.nio.NIOFileUtils.USER_DIR;
import static java.lang.String.format;
import static java.nio.file.Files.newBufferedReader;
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

    protected ConfigurationUtils configurationUtils;
    protected final Invocable engine;

    /**
     * Do not let anyone instantiate this class
     */
    private ApplicationController() {
        configurationUtils = ConfigurationUtils.getInstance();
        engine = (Invocable) initScriptEngine();
    }

    public Object handleScript(String functionName, Object... args) throws ScriptException, NoSuchMethodException {
        return engine.invokeFunction(functionName, args);
    }

    public Tbl getExampleTable() {
        return getTable("handleExample");
    }

    public Tbl getInformalExampleTable() {
        return getTable("handleInformalExample");
    }

    public Tbl getSideBarTable() {
        return getTable("handleSideBar");
    }

    public Tbl getAdmonitionTable(Admonition admonition) {
        final AsciiDocumentInfo documentInfo = ApplicationController.getContext().getDocumentInfo();
        final String handler = configurationUtils.getString(format("%s.handler", admonition.name()));
        final String captionText = getAdmonitionCaption(admonition, documentInfo);
        return getTable(handler, captionText);
    }

    public String getAdmonitionCaption(Admonition admonition, AsciiDocumentInfo documentInfo) {
        String title = null;
        switch (admonition) {
            case CAUTION:
                title = documentInfo.getCautionCaption();
                break;
            case IMPORTANT:
                title = documentInfo.getImportantCaption();
                break;
            case NOTE:
                title = documentInfo.getNoteCaption();
                break;
            case TIP:
                title = documentInfo.getTipCaption();
                break;
            case WARNING:
                title = documentInfo.getWarningCaption();
                break;
        }
        return title;
    }

    private Tbl getTable(String functionName, Object... args) {
        try {
            return (Tbl) handleScript(functionName, args);
        } catch (Exception e) {
            LOGGER.warn("Unable to get table for \"{}\"", functionName);
        }
        return null;
    }

    private ScriptEngine initScriptEngine() {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

        Path[] paths = new Path[1];
        paths[0] = get(CONF_PATH_VALUE, "styles.js");
        for (Path path : paths) {
            try (Reader reader = newBufferedReader(path)) {
                engine.eval(reader);
            } catch (IOException e) {
                throw new UncheckedIOException(e.getMessage(), e);
            } catch (ScriptException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return engine;
    }
}
