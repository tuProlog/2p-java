/*
 * TextAreaDefaults.java - Encapsulates default values for various settings
 * Copyright (C) 1999 Slava Pestov
 *
 * You may use and modify this package for any purpose. Redistribution is
 * permitted, in both source and binary form, provided that this notice
 * remains intact in all source distributions of this package.
 */
package alice.util.jedit;

import javax.swing.JPopupMenu;
import java.awt.Color;

/**
 * Encapsulates default settings for a text area. This can be passed
 * to the constructor once the necessary fields have been filled out.
 * The advantage of doing this over calling lots of set() methods after
 * creating the text area is that this method is faster.
 */
public class TextAreaDefaults
{
    private static TextAreaDefaults DEFAULTS;

    /**
	 * @uml.property  name="inputHandler"
	 * @uml.associationEnd  
	 */
    public InputHandler inputHandler;
    /**
	 * @uml.property  name="document"
	 * @uml.associationEnd  
	 */
    public SyntaxDocument document;
    /**
	 * @uml.property  name="editable"
	 */
    public boolean editable;

    /**
	 * @uml.property  name="caretVisible"
	 */
    public boolean caretVisible;
    /**
	 * @uml.property  name="caretBlinks"
	 */
    public boolean caretBlinks;
    /**
	 * @uml.property  name="blockCaret"
	 */
    public boolean blockCaret;
    /**
	 * @uml.property  name="electricScroll"
	 */
    public int electricScroll;

    /**
	 * @uml.property  name="cols"
	 */
    public int cols;
    /**
	 * @uml.property  name="rows"
	 */
    public int rows;
    /**
	 * @uml.property  name="styles"
	 * @uml.associationEnd  multiplicity="(0 -1)"
	 */
    public SyntaxStyle[] styles;
    /**
	 * @uml.property  name="caretColor"
	 */
    public Color caretColor;
    /**
	 * @uml.property  name="selectionColor"
	 */
    public Color selectionColor;
    /**
	 * @uml.property  name="lineHighlightColor"
	 */
    public Color lineHighlightColor;
    /**
	 * @uml.property  name="lineHighlight"
	 */
    public boolean lineHighlight;
    /**
	 * @uml.property  name="bracketHighlightColor"
	 */
    public Color bracketHighlightColor;
    /**
	 * @uml.property  name="bracketHighlight"
	 */
    public boolean bracketHighlight;
    /**
	 * @uml.property  name="eolMarkerColor"
	 */
    public Color eolMarkerColor;
    /**
	 * @uml.property  name="eolMarkers"
	 */
    public boolean eolMarkers;
    /**
	 * @uml.property  name="paintInvalid"
	 */
    public boolean paintInvalid;

    /**
	 * @uml.property  name="popup"
	 * @uml.associationEnd  readOnly="true"
	 */
    public JPopupMenu popup;

    /**
     * Returns a new TextAreaDefaults object with the default values filled
     * in.
     */
    public static TextAreaDefaults getDefaults()
    {
        if(DEFAULTS == null)
        {
            DEFAULTS = new TextAreaDefaults();

            DEFAULTS.inputHandler = new DefaultInputHandler();
            DEFAULTS.inputHandler.addDefaultKeyBindings();
            DEFAULTS.document = new SyntaxDocument();
            DEFAULTS.editable = true;

            DEFAULTS.caretVisible = true;
            DEFAULTS.caretBlinks = true;
            DEFAULTS.electricScroll = 3;

            DEFAULTS.cols = 80;
            DEFAULTS.rows = 25;
            DEFAULTS.styles = SyntaxUtilities.getDefaultSyntaxStyles();
            DEFAULTS.caretColor = Color.red;
            DEFAULTS.selectionColor = new Color(0xccccff);
            DEFAULTS.lineHighlightColor = new Color(0xe0e0e0);
            DEFAULTS.lineHighlight = true;
            DEFAULTS.bracketHighlightColor = Color.black;
            DEFAULTS.bracketHighlight = true;
            DEFAULTS.eolMarkerColor = new Color(0x009999);
            DEFAULTS.eolMarkers = true;
            DEFAULTS.paintInvalid = true;
        }

        return DEFAULTS;
    }
}
