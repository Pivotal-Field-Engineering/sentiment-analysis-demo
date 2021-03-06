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

import org.json.simple.JSONObject;

public class Tweet {
  private String id = "";
  private String text = "";
  private String createdAt = "";

  public static Tweet fromJSON(JSONObject json) {
    Tweet tweet = new Tweet();
    tweet.id = json.get("id").toString();
    tweet.text = json.get("text").toString();
    tweet.createdAt = json.get("created_at").toString();
    return tweet;
  }

  public String getText() {
    return text;
  }

  public String getId() {
    return id;
  }

  public String getCreatedAt() {
    return createdAt;
  }
}