package com.alphasystem.docbook.handler.impl.block;

import com.alphasystem.docbook.handler.BlockHandlerFactory;
import com.alphasystem.docbook.handler.BlockHandlerService;

import static com.alphasystem.docbook.builder.model.Admonition.*;
import static com.alphasystem.docbook.handler.BlockHandlerFactory.*;

/**
 * @author sali
 */
public class BaseBlockHandlerService extends BlockHandlerService {

    @Override
    public void initializeHandlers() {
        BlockHandlerFactory.registerHandler(CAUTION.name(), new CautionHandler());
        BlockHandlerFactory.registerHandler(IMPORTANT.name(), new ImportantHandler());
        BlockHandlerFactory.registerHandler(NOTE.name(), new NoteHandler());
        BlockHandlerFactory.registerHandler(TIP.name(), new TipHandler());
        BlockHandlerFactory.registerHandler(WARNING.name(), new WarningHandler());
        BlockHandlerFactory.registerHandler(EXAMPLE_KEY, new ExampleHandler());
        BlockHandlerFactory.registerHandler(INFORMAL_EXAMPLE_KEY, new InformalExampleHandler());
        BlockHandlerFactory.registerHandler(SIDE_BAR_KEY, new SideBarHandler());
    }
}
