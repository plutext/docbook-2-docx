package com.alphasystem.docbook;

import com.alphasystem.openxml.builder.wml.RPrBuilder;
import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.ArrayUtils;
import org.docx4j.Docx4J;
import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Tbl;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.file.Path;
import java.util.List;

import static com.alphasystem.util.nio.NIOFileUtils.USER_DIR;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.Paths.get;

/**
 * @author sali
 */
public final class ApplicationController {

    public static final String CONF = "conf";
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
        final File file = get(USER_DIR, CONF, "system-defaults.properties").toFile();
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

    public RPrBuilder applyStyle(RPrBuilder rPrBuilder, String styleHandler, String styleName) throws ScriptException,
            NoSuchMethodException {
        return (RPrBuilder) engine.invokeFunction(styleHandler, rPrBuilder, styleName);
    }

    public Tbl getExampleTable() {
        return getTable(1);
    }

    public Tbl getSideBarTable() {
        return getTable(2);
    }

    private Tbl getTable(int index) {
        try (InputStream inputStream = newInputStream(get(USER_DIR, CONF, "template.docx"))) {
            final WordprocessingMLPackage wordprocessingMLPackage = Docx4J.load(inputStream);
            final MainDocumentPart mainDocumentPart = wordprocessingMLPackage.getMainDocumentPart();
            String xpath = String.format("//w:tbl[%s]", index);
            final List<Object> objects = mainDocumentPart.getJAXBNodesViaXPath(xpath, false);
            return (Tbl) XmlUtils.unwrap(objects.get(0));
        } catch (IOException ex) {
            throw new UncheckedIOException(ex.getMessage(), ex);
        } catch (Docx4JException | JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ScriptEngine initScriptEngine() {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

        Path[] paths = new Path[1];
        paths[0] = get(USER_DIR, CONF, "styles.js");
        final String customPath = getConfiguration().getString("custom-style-path");
        if (customPath != null) {
            paths = ArrayUtils.add(paths, get(customPath));
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
