package se.addq.notifysales.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import se.addq.notifysales.cinode.CinodeApi;
import se.addq.notifysales.notification.model.NotificationData;
import se.addq.notifysales.slack.SlackApi;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(NotificationController.class)

public class NotificationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CinodeApi cinodeApi;

    @MockBean
    private SlackApi slackApi;

    @MockBean
    private NotificationServiceApi mockNotificationService;

    @Test
    public void callingPingWillReturnHttpStatusOKAndInfoThatServiceIsRunning() throws Exception {
        this.mvc.perform(get("/notification/ping").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Notify Service is up an running!"));
    }

    @Test
    public void requestNotifiedListReturnsHttpOKAndCorrectJsonInResponse() throws Exception {
        List<NotificationData> notificationDataList = new ArrayList<>();
        NotificationData notificationData = new NotificationData();
        notificationData.setAssignmentId(1);
        notificationData.setEndDate(LocalDateTime.now());
        notificationDataList.add(notificationData);

        String assignmentsAsJsonExpectedResult = getListOfItemsAsJson(notificationDataList);

        given(mockNotificationService.getListOfNotifiedAssignments())
                .willReturn(assignmentsAsJsonExpectedResult);

        this.mvc.perform(get("/notification/notifiedlist").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(assignmentsAsJsonExpectedResult));
    }

    private <T> String getListOfItemsAsJson(List<T> listOfItems) throws JsonProcessingException {
        return new ObjectMapper()
                .configure(SerializationFeature.INDENT_OUTPUT, true)
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .writeValueAsString(listOfItems);
    }


}
