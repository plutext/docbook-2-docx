package com.alphasystem.docbook;

import com.alphasystem.asciidoc.model.AsciiDocumentInfo;
import com.alphasystem.docbook.builder.model.Admonition;
import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.ArrayUtils;
import org.docx4j.wml.Tbl;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
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
    private static final String CONF_PATH = get(System.getProperty("conf.path", USER_DIR), CONF).toString();
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
            try {
                instance = new ApplicationController();
            } catch (ConfigurationException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return instance;
    }

    protected final Invocable engine;
    private final CompositeConfiguration configuration;

    /**
     * Do not let anyone instantiate this class
     */
    private ApplicationController() throws ConfigurationException {
        Parameters parameters = new Parameters();
        final File file = get(CONF_PATH, "system-defaults.properties").toFile();
        FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<>(
                PropertiesConfiguration.class).configure(parameters.fileBased().setFile(file));

        configuration = new CompositeConfiguration();
        configuration.addConfiguration(new SystemConfiguration());
        configuration.addConfiguration(builder.getConfiguration());

        engine = (Invocable) initScriptEngine();
    }

    public CompositeConfiguration getConfiguration() {
        return configuration;
    }

    public Object handleScript(String functionName, Object... args) throws ScriptException, NoSuchMethodException {
        return engine.invokeFunction(functionName, args);
    }

    public Tbl getExampleTable() {
        return getTable("handleExample");
    }

    public Tbl getSideBarTable() {
        return getTable("handleSideBar");
    }

    public Tbl getAdmonitionTable(Admonition admonition) {
        final AsciiDocumentInfo documentInfo = ApplicationController.getContext().getDocumentInfo();
        final String captionText = getAdmonitionCaption(admonition, documentInfo);
        return getTable("handleAdmonition", admonition.name(), captionText);
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
        Tbl tbl;
        try {
            tbl = (Tbl) handleScript(functionName, args);
        } catch (ScriptException | NoSuchMethodException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return tbl;
    }

    private ScriptEngine initScriptEngine() {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

        Path[] paths = new Path[1];
        paths[0] = get(CONF_PATH, "styles.js");
        final String customStyleName = getConfiguration().getString("custom.style.name");
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
