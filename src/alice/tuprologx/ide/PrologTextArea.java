/*
 * TextAreaDefaults.java - Encapsulates default values for various settings
 * Copyright (C) 1999 Slava Pestov
 *
 * You may use and modify this package for any purpose. Redistribution is
 * permitted, in both source and binary form, provided that this notice
 * remains intact in all source distributions of this package.
 */
package alice.tuprologx.ide;

import alice.util.jedit.*;
import javax.swing.JPopupMenu;
import java.awt.Color;

public class PrologTextArea extends TextAreaDefaults
{
	public PrologTextArea()
	{
	    inputHandler = new DefaultInputHandler();
	    inputHandler.addDefaultKeyBindings();
	    document = new SyntaxDocument();
	    editable = true;

	    caretVisible = true;
	    caretBlinks = true;
	    electricScroll = 3;

	    cols = 80;
	    rows = 25;
	    styles = getSyntaxStyles();
	    caretColor = Color.red;
            selectionColor = new Color(0xccccff);
	    lineHighlightColor = new Color(0xe0e0e0);
	    lineHighlight = true;
	    bracketHighlightColor = Color.black;
	    bracketHighlight = true;
	    eolMarkerColor = new Color(0x009999);
	    eolMarkers = false;
	    paintInvalid = true;
	}

	private SyntaxStyle[] getSyntaxStyles()
	{
		SyntaxStyle[] styles = new SyntaxStyle[Token.ID_COUNT];
		styles[Token.COMMENT1] = new SyntaxStyle(new Color(0x808080),true,false);
		styles[Token.COMMENT2] = new SyntaxStyle(new Color(0x990033),true,false);
		styles[Token.KEYWORD1] = new SyntaxStyle(Color.blue,false,true);
		styles[Token.KEYWORD2] = new SyntaxStyle(Color.black,false,true);
		styles[Token.KEYWORD3] = new SyntaxStyle(Color.red,false,true);
		styles[Token.LITERAL1] = new SyntaxStyle(new Color(0x650099),false,false);
		styles[Token.LITERAL2] = new SyntaxStyle(new Color(0x650099),false,true);
		styles[Token.LABEL] = new SyntaxStyle(new Color(0x008000),false,false);
		styles[Token.OPERATOR] = new SyntaxStyle(Color.black,false,true);
		styles[Token.INVALID] = new SyntaxStyle(Color.red,false,true);

		return styles;
	}

}
