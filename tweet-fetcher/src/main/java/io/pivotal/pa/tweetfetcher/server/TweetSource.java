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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TweetSource {

  private static final Logger LOG = LoggerFactory.getLogger(TweetSource.class);
  private static final long FETCH_DATA_INTERVAL = 5000L;

  private final TwitterClient client;
  private final TwitterDataService dataService;
  private Thread worker;
  private boolean stop;

  public TweetSource(String screenName, String base64BearerToken, TwitterDataService sentimentDataService) {
    this.client = new TwitterClient(screenName,base64BearerToken);
    this.dataService = sentimentDataService;
  }

  public void start() {
    worker = new TweetWorker();
    worker.start();
  }

  private class TweetWorker extends Thread {
    public void run() {
      LOG.info("Thread for tweet fetching starting.");
      while (!stop) {
        List<Tweet> tweets = client.fetchRecentTweets();
        for (Tweet tweet : tweets) {
          dataService.dataReceived(tweet);
        }
        waitFor(FETCH_DATA_INTERVAL);
      }

      LOG.info("Thread for twitter has finished.");
    }
  }

  public void stop() {
    if (worker != null) {
      if (worker.isAlive()) {
        stop = true;
        try {
          worker.join();
        } catch (InterruptedException ex) {
          LOG.warn("Error while stopping twitter worker", ex);
        }
      }
      worker = null;
    }
  }

  private void waitFor(long duration) {
    try {
      Thread.sleep(duration);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
