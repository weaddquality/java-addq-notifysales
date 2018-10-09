package se.addq.notifysales.configuration;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ApplicationConfig.class})
public class ApplicationConfigContextTest {

    @Autowired
    private RestTemplate restTemplate;


    @Test
    public void checkRestTemplateIsInitiated() {
        assertThat(restTemplate).isNotNull();
    }


}
