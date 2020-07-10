# sentiment-analysis-demo
Sentiment analysis demo

## Contents

- tweet-fetcher - Persistent service that fetches new tweets from a named twitter handle every 5 seconds and submits to an AMQP sink. Doesn't send the entire tweet (as many edge nodes in the IoT world - it should only send in what is required)
- tweet-sentiment - Takes a tweet off a queue and analyses it for sentiment

## Usage

To run tweet runner:-

```sh
cd tweet-fetcher
mvn package
java -cp target/tweet-fetcher-1.0-SNAPSHOT.jar io.pivotal.pa.tweetfetcher.server.Main SOLACE_URL TWITTER_HANDLE TWITTER_BEARER_BASE64
```

To run tweet sentiment analysis:-
```sh
cd tweet-sentiment
mvn package
```

Then run as a spring app...

TODO INSTRUCTIONS HERE WITH INJECTED CONFIGURATION FROM CF

The tweets will be put on rm/tweets and the sentiments will be put on rm/sentiments

## Copyright and licensing

Copyright VMware 2020. All rights reserved.

Apache 2.0 licensed unless otherwise stated.

## Support

This is demo code provided as-is and is not intended for production use.
