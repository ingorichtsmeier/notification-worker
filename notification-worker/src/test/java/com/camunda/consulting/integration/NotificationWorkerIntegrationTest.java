package com.camunda.consulting.integration;

import static org.assertj.core.api.Assertions.*;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;

import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.camunda.consulting.NotificationWorker;
import com.camunda.consulting.testEngine.CamundaTestApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CamundaTestApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
public class NotificationWorkerIntegrationTest {
  
  @Autowired
  ProcessEngine engine;
  
  @Before
  public void setup() {
    init(engine);
  }
  
  public static final String TOPIC_TO_TEST = "notification";
  
  @Test
  public void testNotificationWorker() throws InterruptedException {
    // start a process instance
    ProcessInstance processInstance = engine.getRuntimeService().startProcessInstanceByKey("ExternalWorkerTestProcess", 
        withVariables(
            "topic", TOPIC_TO_TEST, 
            "content", "myTestContent"));
    
    // invoke the handler
    NotificationWorker notificationWorker = new NotificationWorker();
    ExternalTaskClient externalTaskClient = notificationWorker.run();
    
    externalTaskClient.stop();
    
    // assert on the results
    assertThat(processInstance).isEnded().variables().contains(entry("message", "Sorry, your tweet has been rejected: myTestContent"));
  }
  
}
