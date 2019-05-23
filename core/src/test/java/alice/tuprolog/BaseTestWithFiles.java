package alice.tuprolog;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BaseTestWithFiles {

    protected File getTempDirectory() {
        File file = new File(System.getProperty("java.io.tmpdir"),
                             "2p" + File.separator + "test" + File.separator + System.currentTimeMillis());
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    protected abstract List<String> getResources();

    protected String getPaths(boolean valid) {
        Stream<String> paths = Stream.of(getTempDirectory().getAbsolutePath());
        if (valid) {
            paths = Stream.concat(
                    paths,
                    getResources().stream().map(it -> getClass().getResource(it).getPath()).map(it -> "'" + it + "'")
            );
        }
        return paths.collect(Collectors.joining(", "));
    }
}
