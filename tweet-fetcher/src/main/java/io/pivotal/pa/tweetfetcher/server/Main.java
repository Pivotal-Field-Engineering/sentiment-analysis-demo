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

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

  private static final Logger LOG = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) throws Exception {

    if (args.length < 3) {
      System.err.println("Expected 3 arguments: Use Main <AMQPUri> <TwitterScreenName> <TwitterBearerTokenBase64>");
      System.exit(1);
    }

    // TODO drive from env variables
    TwitterDataService dataService = new TwitterDataService(args[0]);
    TweetSource dataServer = new TweetSource(args[1],args[2],dataService);
    dataServer.start();

    LOG.info("Twitter to Messaging service started. Press enter to quit.");
    System.in.read();

    dataServer.stop();

  }

}
