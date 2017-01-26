Content Based Apache Kafka
=================

Developer Guide

Content filtering and Consumer Interest handling

With newly introduced content filtering features using consumer interests feature can be accessed by using new client API KafkaContentConsumer<K,V>. With this new feature, we use consumer interest concepts which depict content filtering features client wants to achieve by enabling content filtering. Every Content Consumer has their own consumer interests and they are expected to provide input in the following format similar to a Boolean expression, except that the Boolean literals are replaced by strings (interests). This expression format is referred to as NOA expression.

Example:-

String query = "China|(Sri Lanka&India)

Space characters are only allowed inside an interest keyword such as "Sri Lanka" but not with "China|..." Or "Sri Lanka&India". Using main public method setInterest(String) interests query need to be set and before sending fetch quests into cluster nodes, interests will be sent by KafkaContentConsumer.

Usage Example:-

Following example demonstrates how to set consumer interests as a NOA expression

Properties props = new Properties();
props.put("bootstrap.servers", "localhost:9092");
props.put("group.id", "test" + ThreadLocalRandom.current().nextInt(1, 10000));
props.put("enable.auto.commit", "true");
props.put("auto.commit.interval.ms", "1000");
props.put("session.timeout.ms", "30000");
props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
KafkaContentConsumer<String,String>consumer=new KafkaContentConsumer<String,String>(props);
String expression= "China|Inida|Sri Lanka";
String topic = "test";
consumer.setInterests(expression);
consumer.subscribe(topic);


Note that all the other features and functionalities provided by existing KakfaConsumer are available for new KafkaContentConsumer API as well. Therefore those other features are not described in this document but only the newly added features.
