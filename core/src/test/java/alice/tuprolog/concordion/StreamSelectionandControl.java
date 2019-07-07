package alice.tuprolog.concordion;

import alice.tuprolog.Term;
import alice.tuprolog.exceptions.PrologException;
import alice.tuprolog.lib.ISOIOLibrary;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.api.extension.Extension;
import org.concordion.ext.EmbedExtension;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/* Run this class as a JUnit test. */

@RunWith(ConcordionRunner.class)
public class StreamSelectionandControl {

    private static final Path BASE_DIR;

    static {
        try {
            BASE_DIR = Paths.get(StreamSelectionandControl.class.getResource("/binFile.bin").toURI()).getParent();
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    private static final String[] ISO_IO_LIB = new String[] {
            ISOIOLibrary.class.getName()
    };

    private String ensureWithin(String input, String prefix, String suffix) {
        return ensureStartsWith(ensureEndsWith(input, suffix), prefix);
    }

    private String ensureStartsWith(String input, String prefix) {
        if (!input.startsWith(prefix)) {
            return prefix + input;
        } else {
            return input;
        }
    }

    private String ensureEndsWith(String input, String suffix) {
        if (!input.endsWith(suffix)) {
            return input + suffix;
        } else {
            return input;
        }
    }

    private String fixSlashes(String raw) {
        return raw.replaceAll("\\\\", "/");
    }

    private String updatePaths(String raw) {
        return raw
                .replaceAll("'\\./", ensureWithin(fixSlashes(BASE_DIR.toString()), "'", "/"))
                .replaceAll("'/temp/", ensureWithin(fixSlashes(System.getProperty("java.io.tmpdir")), "'", "/"));
    }

    @Extension
    public ConcordionExtension extension = new EmbedExtension().withNamespace(
            "myns", "http://com.myco/myns");

    public boolean success(String goal, String theory) throws Exception {

        return ConcordionSingleton.getInstance().success(updatePaths(goal), theory, ISO_IO_LIB);

    }

    public Term value(String evaluable) throws Exception {

        return ConcordionSingleton.getInstance().value(evaluable);

    }

    public boolean successWithException(String goal, String theory)
            throws PrologException {

        return ConcordionSingleton.getInstance().successWithException(goal, theory, ISO_IO_LIB);

    }

    public String successWithExceptionAndText(String goal, String theory)
            throws PrologException {

        return ConcordionSingleton.getInstance().successWithExceptionAndText(goal, theory, ISO_IO_LIB);

    }

    public String successAndResult(String goal, String theory, String variable) throws Exception {

        return ConcordionSingleton.getInstance().successAndResult(goal, theory, variable, ISO_IO_LIB);
    }

    public String successAndResultWithoutReplace(String goal, String theory, String variable) throws Exception {

        return ConcordionSingleton.getInstance().successAndResultWithoutReplace(goal, theory, variable, ISO_IO_LIB);
    }

    public boolean successAndResultsWithLimit(String goal, String theory,
                                              String variable, String solution, int maxSolutions)
            throws Exception {

        return ConcordionSingleton.getInstance().successAndResultsWithLimit(
                goal, theory, variable, solution, maxSolutions, ISO_IO_LIB);

    }

    public boolean successAndResultsWithLimitWithoutReplace(String goal,
                                                            String theory, String variable, String solution, int maxSolutions)
            throws Exception {

        return ConcordionSingleton.getInstance()
                                  .successAndResultsWithLimitWithoutReplace(goal, theory,
                                                                            variable, solution, maxSolutions, ISO_IO_LIB);

    }

    public boolean successAndResults(String goal, String theory,
                                     String variable, String solution) throws Exception {

        return ConcordionSingleton.getInstance().successAndResults(goal,
                                                                   theory, variable, solution, ISO_IO_LIB);

    }

    public boolean successAndResultsWithoutReplace(String goal, String theory,
                                                   String variable, String solution) throws Exception {

        return ConcordionSingleton.getInstance()
                                  .successAndResultsWithoutReplace(goal, theory, variable,
                                                                   solution, ISO_IO_LIB);

    }

}
