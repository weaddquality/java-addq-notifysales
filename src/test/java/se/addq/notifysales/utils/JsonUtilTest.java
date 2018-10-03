package se.addq.notifysales.utils;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import se.addq.notifysales.slack.model.SlackNotification;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class JsonUtilTest {

    @Test
    public void serializeToNullStringWhenNullObject() {
        String response = JsonUtil.getJsonFromObject(null, false);
        assertThat(response).isEqualTo("null");
    }


    @Test
    public void serializeToJsonStringWhenCorrectObject() {
        SlackNotification slackNotification = new SlackNotification();
        slackNotification.setText("hej");
        String response = JsonUtil.getJsonFromObject(slackNotification, false);
        assertThat(response).isEqualTo("{\"text\":\"hej\"}");
    }


    @Test
    public void returnEmptyStringWhenSerializationError() {
        NoSerializableClass noSerializableClass = new NoSerializableClass("test");
        String response = JsonUtil.getJsonFromObject(noSerializableClass, false);
        assertThat(response).isEqualTo("");
    }

}