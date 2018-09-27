package se.addq.notifysales.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;

public class SleepUtil {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void sleepMilliSeconds(int milliSeconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliSeconds);
        } catch (InterruptedException e) {
            log.error("Failed to sleep", e);
        }
    }
}
