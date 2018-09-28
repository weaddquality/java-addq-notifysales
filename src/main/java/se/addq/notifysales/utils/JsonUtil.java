package se.addq.notifysales.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class JsonUtil {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static String getJsonFromObject(Object object) {
        String json = null;
        try {
            json =
                    new ObjectMapper().writeValueAsString(object);
            log.debug("body {}", json);
        } catch (JsonProcessingException e) {
            log.error("Could not serialize to json", e);
        }
        if (json != null) {
            return json;
        }
        return "";
    }

}
