import com.intuit.karate.Results;
import com.core.platform.web.factory.DriverFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class Runner {
    
    @Test
    public void runner(){
       Results results = com.intuit.karate.Runner.path(
               "src/test/java/web/features",
                       "src/test/java/mobile/features",
                       "src/test/java/api/features")
               .outputCucumberJson(true)
               .outputHtmlReport(true)
               .karateEnv("sit")
               .customDrivers(DriverFactory.getDrivers())
               .parallel(1);
       Assertions.assertEquals(0, results.getFailCount(), results.getErrorMessages());
   }

}
