package se.addq.notifysales.slack;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class SlackWebHookImplTest {

    @Mock
    RestTemplate restTemplateMock;

    private SlackApi slackApi;

    @Before
    public void setup() {

        Mockito.when(restTemplateMock.postForObject(
                Mockito.eq("http://dummy"),
                Mockito.any(),
                Mockito.eq(String.class))).thenReturn("ok");

        Mockito.when(restTemplateMock.postForObject(
                Mockito.eq("http://wrong"),
                Mockito.any(),
                Mockito.eq(String.class))).thenReturn(null);

        slackApi = new SlackWebHookImpl(restTemplateMock);
    }


    @Test
    public void sendNotificationAndResponseIsOk() {
        ReflectionTestUtils.setField(slackApi, "slackWebhookUrl", "http://dummy");
        boolean isSent = slackApi.sendNotification("Hej");
        assertThat(isSent).isTrue();
    }

    @Test
    public void sendNotificationAndResponseIsNotOk() {
        ReflectionTestUtils.setField(slackApi, "slackWebhookUrl", "http://wrong");
        boolean isSent = slackApi.sendNotification("Hej");
        assertThat(isSent).isFalse();
    }
}