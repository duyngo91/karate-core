package core.platform.web.wait;

import java.util.Date;

public class Wait {

    private Wait() {
        // Private constructor to prevent instantiation
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    private static Date today(){
        return new Date();
    }


    public static boolean until(int timeOutMilliSeconds, int retryTimeMilliSeconds, WaitCondition condition){
        long end = today().getTime() + timeOutMilliSeconds;
        while (today().getTime() <= end){
            try {
                if(condition.apply()) return true;
            } catch (Exception e) {
                // Ignoring exception as we want to continue retrying until timeout
            }
            waitAction(retryTimeMilliSeconds);
        }
        return false;
    }

    private static void waitAction(long milisecond) {
        try {
            Thread.sleep(milisecond);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
