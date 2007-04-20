package alice.tuprolog;

import fit.ColumnFixture;
import fit.Counts;
import fit.Fixture;
import fit.Parse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class IsoAcceptanceFixture extends ColumnFixture {

	public String section;

    protected String input;
    protected Parse tables;
    protected Fixture fixture;
    protected Counts runCounts = new Counts();
    protected String footnote = null;

    protected void run() throws Exception {
        input = read(new File(file(section)));
        fixture = new Fixture();
        tables = new Parse(input, new String[] {"table", "tr", "td"});
        fixture.doTables(tables);
        runCounts.tally(fixture.counts);
        summary.put("counts run", runCounts);
    }

    protected String file(String title) {
		String words = title.substring(title.indexOf(" ")).toLowerCase();
		return "src/test/java/doc/" + Fixture.camel(words) + ".html";
	}

    public int right() throws Exception {
        run();
        return fixture.counts.right;
    }

    public int wrong() {
        return fixture.counts.wrong;
    }

    public int ignores() {
        return fixture.counts.ignores;
    }

    public int exceptions() {
        return fixture.counts.exceptions;
    }

    protected String read(File input) {
		footnote = null; // reset footnotes for the new file
        char chars[] = new char[(int)(input.length())];
        try {
            FileReader in = new FileReader(input);
            in.read(chars);
            in.close();
            return new String(chars);
        } catch (IOException e) {
        	e.printStackTrace();
            return "";
        }
    }


    // Footnote /////////////////////////////////

    Parse fileCell;

    public void doRow(Parse row) {
        fileCell = row.leaf();
        super.doRow(row);
    }

    public void wrong(Parse cell) {
        super.wrong(cell);
        if (footnote == null) {
            // footnote = tables.footnote();
            footnote = footnote(tables);
            fileCell.addToBody(footnote);
        }
    }

    public static int footnoteFiles = 0;
    private static String reportDir = "src/test/java/report/";

    public String footnote(Parse tables) {
        if (footnoteFiles >= 25) {
            return "[-]";
        } else {
			int thisFootnote = ++footnoteFiles;
			String footnoteDir = "footnotes/";
            String html = reportDir + footnoteDir + thisFootnote + ".html";
            try {
				File dir = new File(reportDir + footnoteDir);
				dir.mkdir();
                File file = new File(html);
                file.delete();
                PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(file)));
                tables.print(output);
                output.close();
                return " <a href=\"" + footnoteDir + thisFootnote + ".html" + "\">[" + thisFootnote + "]</a>";
            } catch (IOException e) {
				System.out.println(e.getMessage());
                return "[!]";
            }
        }
    }

}