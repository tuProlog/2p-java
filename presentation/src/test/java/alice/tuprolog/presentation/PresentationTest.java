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

import static alice.tuprolog.presentation.MIMEType.*;
import static org.junit.Assert.assertEquals;

@RunWith(JUnitParamsRunner.class)
public class PresentationTest {


    public Object[][] getTerms() {
        return Stream.of(
                tests("atom", APPLICATION_JSON, APPLICATION_YAML),
                tests("Variable", APPLICATION_JSON, APPLICATION_YAML),
                tests("_", APPLICATION_JSON, APPLICATION_YAML),
                tests("1", APPLICATION_JSON, APPLICATION_YAML),
                tests("1.2", APPLICATION_JSON, APPLICATION_YAML),
                tests(Integer.toString(Integer.MAX_VALUE), APPLICATION_JSON, APPLICATION_YAML),
                tests(Integer.toString(Integer.MIN_VALUE), APPLICATION_JSON, APPLICATION_YAML),
                tests(Long.toString(Long.MAX_VALUE), APPLICATION_JSON, APPLICATION_YAML),
                tests(Long.toString(Long.MIN_VALUE), APPLICATION_JSON, APPLICATION_YAML),
                tests("f(a, B, c(1, 2.3))", APPLICATION_JSON, APPLICATION_YAML)
            ).flatMap(Function.identity()).map(it -> it.toArray(Object[]::new)).toArray(Object[][]::new);
    }

    private Stream<Stream<Object>> tests(String term, MIMEType type, MIMEType... types) {
        return Stream.concat(
                Stream.of(type),
                Stream.of(types)
        ).map(t -> test(term, t));
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
}
