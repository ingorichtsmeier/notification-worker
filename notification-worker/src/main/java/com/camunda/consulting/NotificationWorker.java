package com.camunda.consulting;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.camunda.bpm.client.topic.TopicSubscriptionBuilder;

public class NotificationWorker {
  public static void main(String[] args) {
    // bootstrap the client
    NotificationWorker notificationWorker = new NotificationWorker();
    notificationWorker.run();
  }

  public ExternalTaskClient run() {
    ExternalTaskClient client = createClient();
    createSubscription(client);
    return client;
  }

  public ExternalTaskClient createClient() {
    ExternalTaskClient client = ExternalTaskClient.create()
        .baseUrl("http://localhost:8080/rest")
        .asyncResponseTimeout(20000)
        .disableBackoffStrategy()
        .lockDuration(10000)
        .maxTasks(1)
        .build();
    return client;
  }
  
  public void createSubscription(ExternalTaskClient client) {
    // subscribe to the topic
    TopicSubscriptionBuilder subscriptionBuilder = client
      .subscribe("notification");
    
    // handle job
    subscriptionBuilder.handler((externalTask, externalTaskService) -> {
      execute(externalTask, externalTaskService);
    });
    subscriptionBuilder.open();
  }

  public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
    String content = externalTask.getVariable("content");
    String message = "Sorry, your tweet has been rejected: " + content;
    System.out.println(message);
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("notificationTimestamp", new Date());
    variables.put("message", message);
    externalTaskService.complete(externalTask, variables);
  }

}
