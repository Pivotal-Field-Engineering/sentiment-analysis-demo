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
package io.pivotal.pa.tweetsentiment;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jcsmp.XMLMessageListener;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.SpringJCSMPFactory;

@Component
public class TweetConsumer implements XMLMessageListener {

  private CountDownLatch latch = new CountDownLatch(1);
  private static final Logger logger = LoggerFactory.getLogger(TweetConsumer.class);

  // Producer code
  private final Topic topic = JCSMPFactory.onlyInstance().createTopic("rm/sentiments");
  @Autowired
  private SpringJCSMPFactory solaceFactory;

  // private SpringJCSMPFactory solaceFactory;
  private JCSMPSession session;
  private XMLMessageProducer prod;

  ObjectMapper objectMapper = new ObjectMapper();

  public void onReceive(BytesXMLMessage msg) {
    if (msg instanceof TextMessage) {
      logger.info("============= TextMessage received: " + ((TextMessage) msg).getText());

      String jsonString = ((TextMessage)msg).getText();
      try {
        // Parse JSON
        objectMapper.readValue(jsonString, Tweet.class);

        // Determine sentiment
        TweetSentiment sentiment = new TweetSentiment("1","good");
        // TODO fill this out with magical analysis

        session = solaceFactory.createSession();
        prod = session.getMessageProducer(new PubEventHandler());

        // Produce new message of Sentiment on to topic

          ObjectMapper objectMapper = new ObjectMapper();
          objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
          String json = objectMapper.writeValueAsString(sentiment);

          TextMessage jcsmpMsg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
          jcsmpMsg.setText(json);
          jcsmpMsg.setDeliveryMode(DeliveryMode.PERSISTENT);

          // logger.info("============= Sending " + json);
          prod.send(jcsmpMsg, topic);

          System.out.println(" [x] Sent '" + json + "'");

      } catch (Exception e) {
        e.printStackTrace();
      }

    } else {
      logger.info("============= Message received.");
    }
    latch.countDown(); // unblock main thread
  }

  public void onException(JCSMPException e) {
    logger.info("Consumer received exception:", e);
    latch.countDown(); // unblock main thread
  }

  public CountDownLatch getLatch() {
    return latch;
  }

}