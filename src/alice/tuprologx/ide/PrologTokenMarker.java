package alice.tuprologx.ide;
import alice.util.jedit.*;
import javax.swing.text.Segment;

public class PrologTokenMarker extends TokenMarker
{
	public PrologTokenMarker()
	{
		this(true,getKeywords());
	}

	public PrologTokenMarker(boolean cpp,KeywordMap keywords)
	{
		this.cpp = cpp;
		this.keywords = keywords;
	}

	public byte markTokensImpl(byte token, Segment line, int lineIndex)
	{
		char[] array = line.array;
		int offset = line.offset;
		lastOffset = offset;
		lastKeyword = offset;
		int length = line.count + offset;
		boolean backslash = false;

loop:		for(int i = offset; i < length; i++)
		{
			int i1 = (i+1);

			char c = array[i];
			if(c == '\\')
			{
				backslash = !backslash;
				continue;
			}

			switch(token)
			{
			case Token.NULL:
				switch(c)
				{
				case '%':
                                    if (backslash)
					backslash = false;
			            doKeyword(line,i,c);
				    addToken(i - lastOffset,token);
                                    addToken(length - i,Token.COMMENT1);
                                    lastOffset = lastKeyword = length;
                                    break loop;
                                /*
				case '#':
					if(backslash)
						backslash = false;
					else {
						if(doKeyword(line,i,c))
							break;
						addToken(i - lastOffset,token);
						addToken(length - i,Token.LABEL);
						lastOffset = lastKeyword = length;
						break loop;
					}
					break;

				case '"':
					doKeyword(line,i,c);
					if(backslash)
						backslash = false;
					else
					{
						addToken(i - lastOffset,token);
						token = Token.LITERAL1;
						lastOffset = lastKeyword = i;
					}
					break;

				case '\'':
					doKeyword(line,i,c);
					if(backslash)
						backslash = false;
					else
					{
						addToken(i - lastOffset,token);
						token = Token.LITERAL2;
						lastOffset = lastKeyword = i;
					}
					break;
				case ':':
					if(lastKeyword == offset)
					{
						if(doKeyword(line,i,c))
							break;
						backslash = false;
						addToken(i1 - lastOffset,Token.LABEL);
						lastOffset = lastKeyword = i1;
					}
					else if(doKeyword(line,i,c))
						break;
					break;
				case '/':
					backslash = false;
					doKeyword(line,i,c);
					if(length - i > 1)
					{
						switch(array[i1])
						{
						case '*':
							addToken(i - lastOffset,token);
							lastOffset = lastKeyword = i;
							if(length - i > 2 && array[i+2] == '*')
								token = Token.COMMENT2;
							else
								token = Token.COMMENT1;
							break;
						case '/':
							addToken(i - lastOffset,token);
							addToken(length - i,Token.COMMENT1);
							lastOffset = lastKeyword = length;
							break loop;
						}
					}
					break;
                                */
				default:
					backslash = false;
					if(!Character.isLetterOrDigit(c)
						&& c != '_')
						doKeyword(line,i,c);
					break;
				}
				break;
			case Token.COMMENT1:
                        /*
			case Token.COMMENT2:
				backslash = false;
				if(c == '*' && length - i > 1)
				{
					if(array[i1] == '/')
					{
						i++;
						addToken((i+1) - lastOffset,token);
						token = Token.NULL;
						lastOffset = lastKeyword = i+1;
					}
				}
				break;
                        */
			case Token.LITERAL1:
				if(backslash)
					backslash = false;
				else if(c == '"')
				{
					addToken(i1 - lastOffset,token);
					token = Token.NULL;
					lastOffset = lastKeyword = i1;
				}
				break;
			case Token.LITERAL2:
				if(backslash)
					backslash = false;
				else if(c == '\'')
				{
					addToken(i1 - lastOffset,Token.LITERAL1);
					token = Token.NULL;
					lastOffset = lastKeyword = i1;
				}
				break;
			default:
				throw new InternalError("Invalid state: "
					+ token);
			}
		}

		if(token == Token.NULL)
			doKeyword(line,length,'\0');

		switch(token)
		{
		case Token.LITERAL1:
		case Token.LITERAL2:
			addToken(length - lastOffset,Token.INVALID);
			token = Token.NULL;
			break;
		case Token.KEYWORD2:
			addToken(length - lastOffset,token);
			if(!backslash)
				token = Token.NULL;
		default:
			addToken(length - lastOffset,token);
			break;
		}

		return token;
	}

	public static KeywordMap getKeywords()
	{
		if(respectKeywords == null)
		{
			respectKeywords = new KeywordMap(false);
			//respectKeywords.add("reaction",Token.KEYWORD1);
			//respectKeywords.add("out",Token.KEYWORD3);
			//respectKeywords.add("in",Token.KEYWORD3);
			//respectKeywords.add("rd",Token.KEYWORD3);
			//respectKeywords.add("inp",Token.KEYWORD3);
			//respectKeywords.add("rdp",Token.KEYWORD3);
			//respectKeywords.add("out_r",Token.KEYWORD2);
			//respectKeywords.add("in_r",Token.KEYWORD2);
			//respectKeywords.add("no_r",Token.KEYWORD2);
			//respectKeywords.add("rd_r",Token.KEYWORD2);
			//respectKeywords.add("current_op",Token.KEYWORD2);
			//respectKeywords.add("current_agent",Token.KEYWORD2);
			//respectKeywords.add("current_tc",Token.KEYWORD2);
			//respectKeywords.add("pre",Token.KEYWORD2);
			//respectKeywords.add("post",Token.KEYWORD2);
			//respectKeywords.add("success",Token.KEYWORD2);
			//respectKeywords.add("failure",Token.KEYWORD2);
			// extensions
			//respectKeywords.add("current_time",Token.KEYWORD2);
			//respectKeywords.add("is",Token.KEYWORD2);
			//respectKeywords.add("spawn",Token.KEYWORD2);
			//respectKeywords.add("java",Token.LITERAL1);
			//respectKeywords.add("prolog",Token.LITERAL1);
			//respectKeywords.add("out_tc",Token.KEYWORD2);
			//respectKeywords.add("include",Token.LABEL);
		}
		return respectKeywords;
	}

	// private members
	private static KeywordMap respectKeywords;

	private boolean cpp;
	private KeywordMap keywords;
	private int lastOffset;
	private int lastKeyword;

	private boolean doKeyword(Segment line, int i, char c)
	{
		int i1 = i+1;

		int len = i - lastKeyword;
		byte id = keywords.lookup(line,lastKeyword,len);
		if(id != Token.NULL)
		{
			if(lastKeyword != lastOffset)
				addToken(lastKeyword - lastOffset,Token.NULL);
			addToken(len,id);
			lastOffset = i;
		}
		lastKeyword = i1;
		return false;
	}
}
