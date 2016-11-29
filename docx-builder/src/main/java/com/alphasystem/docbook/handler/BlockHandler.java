package com.alphasystem.docbook.handler;

import com.alphasystem.docbook.builder.model.Admonition;
import org.docbook.model.Example;
import org.docbook.model.SideBar;
import org.docx4j.wml.P;
import org.docx4j.wml.Tbl;

/**
 * Interface for handlers for block fragments, like {@link Admonition}, {@link Example}, and {@link SideBar}.
 *
 * @author sali
 */
public interface BlockHandler<T> {

    /**
     * Construct this block.
     *
     * @return An object containing the structure of this block, this could be a {@link Tbl}, {@link P}, or any other
     * valid open xml object.
     */
    T handleBlock();
}
