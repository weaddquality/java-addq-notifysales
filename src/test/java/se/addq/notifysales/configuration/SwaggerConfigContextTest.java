package se.addq.notifysales.configuration;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import springfox.documentation.spring.web.plugins.Docket;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SwaggerConfig.class})
public class SwaggerConfigContextTest {


    @Autowired
    private Docket docket;

    @Test
    public void checkDocketIsInitiated() {
        assertThat(docket).isNotNull();
    }

}
