package web;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;

import core.platform.web.factory.DriverFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class RunTest {

    @Test
    public void run() {

        String path = System.getProperty("karate.path", "src/test/java/web/features");
        Results results = Runner
                .path(path)
                .tags("@healing")
                .outputCucumberJson(true)
                .outputHtmlReport(true)
                .debugMode(true)
                .systemProperty("karate.env", "sit")
                .customDrivers(DriverFactory.getDrivers())
                .parallel(1);

        Assertions.assertEquals(0, results.getFailCount(), results.getErrorMessages());

    }
}
