package web;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import core.healing.HealingInitializer;
import core.healing.runtime.HealingRuntime;
import core.platform.web.factory.DriverFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class RunTest {


    @Test
    public void run() {
        // Auto-initialize healing if enabled
        HealingInitializer.autoInit();
        HealingRuntime.start();

        String path = System.getProperty("karate.path", "src/test/java/web/features");
        String tags = System.getProperty("karate.tags", "@test-1");
        Results results = Runner
                .path(path)
                .tags(tags)
                .outputCucumberJson(true)
                .outputHtmlReport(true)
                .debugMode(false)
                .systemProperty("karate.env", "sit")
                .systemProperty("karate.headless", "false")
                .customDrivers(DriverFactory.getDrivers())
                .parallel(1);

        HealingRuntime.end();
        Assertions.assertEquals(0, results.getFailCount(), results.getErrorMessages());

    }
}
