package com.alphasystem.docbook.builder.test;

import org.docbook.model.VariableListEntry;

import static com.alphasystem.docbook.builder.test.DataFactory.*;

/**
 * @author sali
 */
public final class StaticDataFactory {

    public static final VariableListEntry[] VARIABLE_LIST_ENTRIES = {
            createVariableListEntry(createListItem(null, "The brain of the computer."), createTerm("CPU")),
            createVariableListEntry(createListItem(createSimplePara(null, "Permanent storage for operating system and/or user files.")), createTerm("Hard drive")),
            createVariableListEntry(createListItem(createSimplePara(null, "Temporarily stores information the CPU uses during operation.")), createTerm("RAM")),
            createVariableListEntry(createListItem(createSimplePara(null, "Used to enter text or control items on the screen.")), createTerm("Keyboard")),
            createVariableListEntry(createListItem(createSimplePara(null, "Used to point to and select items on your computer screen.")), createTerm("Mouse")),
            createVariableListEntry(createListItem(createSimplePara(null, "Displays information in visual form using text and graphics.")), createTerm("Monitor"))};
}
