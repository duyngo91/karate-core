package core.platform.web.wait;

import java.io.IOException;

@FunctionalInterface
public interface    WaitCondition {
    boolean apply() throws IOException;
}


