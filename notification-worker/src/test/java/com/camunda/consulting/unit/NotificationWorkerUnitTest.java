package com.camunda.consulting.unit;

import static org.mockito.Mockito.*;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.camunda.consulting.NotificationWorker;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { /*DataReadyEventExecutorImpl.class, CamundaClientConfiguration.class, CamundaClient.class,
        FeignClientBuilder.class*/ NotificationWorker.class })
public class NotificationWorkerUnitTest {

    private static final String ACTIVITY_ID = "ThrowEvent_InterestRatesReady";
    private static final String ACTIVITY_GROUP = "ASSUM01";
    private static final String BUSINESS_KEY = "TEST";
    private static final String ITERATION_ACTIVITYGROUP_VAR = "IterationActivityGroup";

    @Autowired
    NotificationWorker notificationHandler;

//    @InjectMocks
//    CamundaClientConfiguration camundaConfiguration;
//    @MockBean
//    CamundaClient camundaClient;
//    @MockBean
//    EmailSenderServiceInterface emailSenderService;

    @Test
    public void completeNotification() {

        // create mocked objects
        ExternalTask externalTask = mock(ExternalTask.class);
        ExternalTaskService externalTaskService = mock(ExternalTaskService.class);

        // mock behaviour
        when(externalTask.getVariable("content")).thenReturn("testcontent");

        // execute real method with mocked data
        notificationHandler.execute(externalTask, externalTaskService);

        // verify behaviour
        verify(externalTaskService).complete(externalTask, withVariables(
            "message", "Sorry, your tweet has been rejected: testcontent",
            "notificationTimestamp", new Date()));
        verify(externalTaskService, never()).handleFailure(any(ExternalTask.class), anyString(), anyString(), anyInt(),
                anyLong());
    }

    @Ignore
    @Test
    public void handleFailureNullActivityId() {

        // create mocked objects
        ExternalTask externalTask = mock(ExternalTask.class);
        ExternalTaskService externalTaskService = mock(ExternalTaskService.class);

        // mock behaviour
        when(externalTask.getActivityId()).thenReturn(null);

        // execute real method with mocked data
        notificationHandler.execute(externalTask, externalTaskService);

        // verify behaviour
        verify(externalTaskService).handleFailure(any(ExternalTask.class), anyString(), any(), anyInt(), anyLong());
        verify(externalTaskService, never()).complete(externalTask);
//        verify(emailSenderService, never()).sendTemplateEmail(any(EmailBodyDto.class));
    }

    @Ignore
    @Test
    public void handleFailureNullActivityGroup() {

        // create mocked objects
        ExternalTask externalTask = mock(ExternalTask.class);
        ExternalTaskService externalTaskService = mock(ExternalTaskService.class);

        // mock behaviour
        when(externalTask.getActivityId()).thenReturn(ACTIVITY_ID);
        when(externalTask.getVariable(ITERATION_ACTIVITYGROUP_VAR)).thenReturn(null);

        // execute real method with mocked data
        notificationHandler.execute(externalTask, externalTaskService);

        // verify behaviour
//        verify(emailSenderService).sendTemplateEmail(any(EmailBodyDto.class));
        verify(externalTaskService).complete(externalTask);
        verify(externalTaskService, never()).handleFailure(any(ExternalTask.class), anyString(), anyString(), anyInt(),
                anyLong());
    }

    @Ignore
    @Test
    public void handleFailureNullBusinessKey() {

        // create mocked objects
        ExternalTask externalTask = mock(ExternalTask.class);
        ExternalTaskService externalTaskService = mock(ExternalTaskService.class);

        // mock behaviour
        when(externalTask.getActivityId()).thenReturn(ACTIVITY_ID);
        when(externalTask.getVariable(ITERATION_ACTIVITYGROUP_VAR)).thenReturn(ACTIVITY_GROUP);
        when(externalTask.getBusinessKey()).thenReturn(null);

        // execute real method with mocked data
        notificationHandler.execute(externalTask, externalTaskService);

        // verify behaviour
//        verify(emailSenderService).sendTemplateEmail(any(EmailBodyDto.class));
        verify(externalTaskService).complete(externalTask);
        verify(externalTaskService, never()).handleFailure(any(ExternalTask.class), anyString(), anyString(), anyInt(),
                anyLong());
    }
}
