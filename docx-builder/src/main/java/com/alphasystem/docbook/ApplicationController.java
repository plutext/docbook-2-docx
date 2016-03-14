package com.alphasystem.docbook;

import com.alphasystem.asciidoc.model.AsciiDocumentInfo;
import com.alphasystem.docbook.builder.model.Admonition;
import com.alphasystem.docbook.util.ConfigurationUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.docx4j.wml.Tbl;

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

    public static final String CONF = "conf";
    public static final String CONF_PATH = get(System.getProperty("conf.path", USER_DIR), CONF).toString();
    private static final ThreadLocal<DocumentContext> CONTEXT = new ThreadLocal<>();
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

    public Object handleScript(String functionName, Object... args) {
        Object result = null;
        try {
            result = engine.invokeFunction(functionName, args);
        } catch (ScriptException | NoSuchMethodException e) {
            // ignore
        }
        return result;
    }

    public Tbl getExampleTable() {
        return getTable("handleExample");
    }

    public Tbl getSideBarTable() {
        return getTable("handleSideBar");
    }

    public Tbl getAdmonitionTable(Admonition admonition) {
        final AsciiDocumentInfo documentInfo = ApplicationController.getContext().getDocumentInfo();
        final String handler = configurationUtils.getString(format("%s.handler", admonition.name()));
        final String admonitionCaptionStyle = configurationUtils.getAdmonitionCaptionStyle(admonition);
        final String captionText = getAdmonitionCaption(admonition, documentInfo);
        return getTable(handler, admonitionCaptionStyle, captionText);
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
        return (Tbl) handleScript(functionName, args);
    }

    private ScriptEngine initScriptEngine() {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

        Path[] paths = new Path[1];
        paths[0] = get(CONF_PATH, "styles.js");
        final String customStyleName = configurationUtils.getString("custom.style.name");
        if (customStyleName != null) {
            paths = ArrayUtils.add(paths, get(CONF_PATH, "custom", format("%s.js", customStyleName)));
        }
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
