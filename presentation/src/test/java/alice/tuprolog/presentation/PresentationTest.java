package alice.tuprolog.presentation;

import alice.tuprolog.Term;
import alice.util.Tools;
import com.fasterxml.jackson.databind.JsonNode;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.function.Function;
import java.util.stream.Stream;

import static alice.tuprolog.Term.Comparison.NUMBERS_BY_VALUE;
import static alice.tuprolog.Term.Comparison.VARIABLES_BY_NAME;
import static alice.tuprolog.presentation.MIMEType.APPLICATION_JSON;
import static alice.tuprolog.presentation.MIMEType.APPLICATION_YAML;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnitParamsRunner.class)
public class PresentationTest {

    public Object[][] getTerms() {

        final MIMEType[] types = {APPLICATION_JSON, APPLICATION_YAML};
        
        return Stream.of(
                tests("atom", types),
                tests("Variable", types),
                tests("_", types),
                tests("_", types),
                tests("1", types),
                tests("1.2", types),
                tests(Integer.toString(Integer.MAX_VALUE), types),
                tests(Integer.toString(Integer.MIN_VALUE), types),
                tests(Long.toString(Long.MAX_VALUE), types),
                tests(Long.toString(Long.MIN_VALUE), types),
                tests("f(a, B, c(1, 2.3))", types),
                tests("[]", types),
                tests("[a]", types),
                tests("[a | B]", types),
                tests("[a, B, 3]", types),
                tests("[a, B, 3, 4.5]", types),
                tests("[a, B, 3, 4.5, f(g)]", types),
                tests("[a, B, 3 | c]", types),
                tests("{}", types),
                tests("{a}", types),
                tests("{a, B}", types),
                tests("{a, B, 3}", types),
                tests("{a, B, 3, 4.5, _}", types),
                tests("(a)", types),
                tests("(a, B)", types),
                tests("(a, B, 3)", types),
                tests("(a, B, 3, 4.5, _)", types)
            ).flatMap(Function.identity()).map(it -> it.toArray(Object[]::new)).toArray(Object[][]::new);
    }

    private Stream<Stream<Object>> tests(String term, MIMEType... types) {
        return Stream.of(types).map(t -> test(term, t));
    }

    private Stream<Object> test(String term, MIMEType type) {
        return Stream.of(
            term,
            type.toString(),
            loadFile(getFileName(term, type))
        );
    }

    private static String loadFile(String path) {
        try {
            final InputStream stream = PresentationTest.class.getResourceAsStream(path);
            if (stream == null) throw new FileNotFoundException(path);
            return Tools.loadText(stream);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String getFileName(String filename, MIMEType type) {
        try {
            return URLEncoder.encode(filename, "UTF-8") + type.getFileExtension();
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    @Parameters(method = "getTerms")
    public void testTermSerializationAsString(String termString, String type, String expectedRepresentation) throws IOException {
        final MIMEType mimeType = MIMEType.parse(type);
        final Term term = Term.createTerm(termString);
        final String serialised = Serializer.of(Term.class, mimeType).toString(term);

        final JsonNode expected = Presentation.getObjectMapper(mimeType).readTree(expectedRepresentation);
        final JsonNode actual = Presentation.getObjectMapper(mimeType).readTree(serialised);

        assertEquals(expected, actual);
    }

    @Test
    @Parameters(method = "getTerms")
    public void testTermDeserializationAsString(String expectedTerm, String type, String representation) throws IOException {
        final MIMEType mimeType = MIMEType.parse(type);
        final Term expected = Term.createTerm(expectedTerm);
        final Term actual = Deserializer.of(Term.class, mimeType).fromString(representation);

        assertTrue(expected.equals(actual, VARIABLES_BY_NAME, NUMBERS_BY_VALUE));
    }
}
