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

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TwitterClient {

  private static final Logger LOG = LoggerFactory.getLogger(TwitterClient.class);

  private static final String HTTP_STATUS_OK = "HTTP/1.1 200";
  private final CloseableHttpClient httpClient = HttpClients.createDefault();
  private final HttpGet httpGet;

  public TwitterClient(String screenName, String base64BearerTokenValue) {
    httpGet = new HttpGet("https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=" + screenName);
    httpGet.addHeader("Authorization","Bearer " + base64BearerTokenValue);
  }

  public List<Tweet> fetchRecentTweets() {
    List<Tweet> tweets = new ArrayList<>();

    // Perform GET to Twitter source.
    try (CloseableHttpResponse httpResponse = httpClient.execute(httpGet)) {
      if (!httpResponse.getStatusLine().toString().startsWith(HTTP_STATUS_OK)) {
        LOG.warn("HTTP error while fetching tweets: [" + httpResponse.getStatusLine() + "]");
        return tweets;
      }

      // Parse response.
      String responseData = EntityUtils.toString(httpResponse.getEntity());
      JSONArray rootObject = (JSONArray) JSONValue.parse(responseData);
      //JSONArray jsonArray = (JSONArray) rootObject;
      //long currentTimestamp = System.currentTimeMillis();

      // Process all flight events.
      for (Object jo : rootObject) {
        Tweet tweet = Tweet.fromJSON((org.json.simple.JSONObject) jo);
        tweets.add(tweet);
      }

      return tweets;
    } catch (IOException ex) {
      LOG.warn("Exception while fetching tweets from twitter", ex);
    }

    return tweets;
  }

}
