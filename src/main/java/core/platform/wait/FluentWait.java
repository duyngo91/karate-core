package core.platform.wait;

import core.platform.common.Configuration;
import core.platform.exceptions.TimeoutException;
import core.platform.utils.Logger;

import java.util.function.Supplier;

public class FluentWait {
    private int timeout = Configuration.getDefaultTimeout();
    private int retry = Configuration.getDefaultRetry();
    private String description = "condition";
    private boolean ignoreExceptions = true;
    
    public static FluentWait waitFor() {
        return new FluentWait();
    }
    
    public FluentWait timeout(int timeoutMs) {
        this.timeout = timeoutMs;
        return this;
    }
    
    public FluentWait retry(int retryMs) {
        this.retry = retryMs;
        return this;
    }
    
    public FluentWait description(String desc) {
        this.description = desc;
        return this;
    }
    
    public FluentWait throwOnFailure() {
        this.ignoreExceptions = false;
        return this;
    }
    
    public boolean until(Supplier<Boolean> condition) {
        Logger.debug("Waiting for: %s (timeout: %dms, retry: %dms)", description, timeout, retry);
        
        long endTime = System.currentTimeMillis() + timeout;
        
        while (System.currentTimeMillis() <= endTime) {
            try {
                if (condition.get()) {
                    Logger.debug("Condition met: %s", description);
                    return true;
                }
            } catch (Exception e) {
                if (!ignoreExceptions) {
                    throw new TimeoutException(description, timeout, e);
                }
                Logger.debug("Exception during wait (ignored): %s", e.getMessage());
            }
            
            try {
                Thread.sleep(retry);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        
        Logger.warn("Timeout waiting for: %s", description);
        if (!ignoreExceptions) {
            throw new TimeoutException(description, timeout);
        }
        return false;
    }
}