/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.pivotal.pa.tweetfetcher.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;

import com.solacesystems.jcsmp.DeliveryMode;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jcsmp.Topic;
import com.solacesystems.jcsmp.XMLMessageProducer;

public class TwitterDataService {

  private long tsLastMeasure;
  private long numberOfEventsReceived;

  private final Topic topic = JCSMPFactory.onlyInstance().createTopic("rm/tweets");

  //private SpringJCSMPFactory solaceFactory;
  private JCSMPSession session;
  private XMLMessageProducer prod;

  public TwitterDataService(String url) {
    try {
      System.out.println("Solace URL: " + url);
      final JCSMPProperties properties = new JCSMPProperties();
      properties.setProperty(JCSMPProperties.HOST, url);
      properties.setProperty(JCSMPProperties.USERNAME, "adam");
      properties.setProperty(JCSMPProperties.PASSWORD, "VMware1!");
      properties.setProperty(JCSMPProperties.VPN_NAME, "default");
      session = JCSMPFactory.onlyInstance().createSession(properties);

      session.connect();

      //session = solaceFactory.createSession();

      prod = session.getMessageProducer(new PubEventHandler());
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }

  }

  public void dataReceived(Tweet tweet) {
    logDataReceived();

    try {

      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
      String json = objectMapper.writeValueAsString(tweet);

      TextMessage jcsmpMsg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
      jcsmpMsg.setText(json);
      jcsmpMsg.setDeliveryMode(DeliveryMode.PERSISTENT);

      //logger.info("============= Sending " + json);
      prod.send(jcsmpMsg, topic);

      System.out.println(" [x] Sent '" + json + "'");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void logDataReceived() {
    System.out.print(".");
    numberOfEventsReceived++;
    if (numberOfEventsReceived >= 50) {
      double diffSeconds = (System.currentTimeMillis() - tsLastMeasure) / 1000;
      if (diffSeconds > 0) {
        System.out.printf(" -> %.1f tweets per second.\r\n", (numberOfEventsReceived / diffSeconds));
      }
      tsLastMeasure = System.currentTimeMillis();
      numberOfEventsReceived = 0;
    }
  }

}
