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
        String tags = System.getProperty("karate.tags", "@non");
        Results results = Runner
                .path(path)
                .tags(tags)
                .outputCucumberJson(true)
                .outputHtmlReport(true)
                .debugMode(false)
                .systemProperty("karate.env", "sit")
                .systemProperty("karate.headless", "true")
                .customDrivers(DriverFactory.getDrivers())
                .parallel(1);

        Assertions.assertEquals(0, results.getFailCount(), results.getErrorMessages());

    }
}
