package se.addq.notifysales.configuration;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ScheduledConfigurerConfig.class})
public class ScheduleConfigContextTest {

    private ScheduledConfigurerConfig scheduledConfigurerConfig;

    @Mock
    ScheduledTaskRegistrar scheduledTaskRegistrar;

    @Before
    public void setUp() {
        scheduledConfigurerConfig = new ScheduledConfigurerConfig();
    }

    @Test
    public void checkScheduledConfigurerIsSetup() {
        scheduledConfigurerConfig.configureTasks(scheduledTaskRegistrar);
    }

}
