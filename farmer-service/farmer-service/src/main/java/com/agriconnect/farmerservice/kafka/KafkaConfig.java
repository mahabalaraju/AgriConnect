package com.agriconnect.farmerservice.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // ─────────────────────────────────────────
    // Producer Configuration
    // ─────────────────────────────────────────
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // Ensures no duplicate messages even if producer retries
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        // Retry up to 3 times if publish fails
        config.put(ProducerConfig.RETRIES_CONFIG, 3);

        // Wait for all replicas to acknowledge
        config.put(ProducerConfig.ACKS_CONFIG, "all");

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // ─────────────────────────────────────────
    // Auto Create Topics
    // ─────────────────────────────────────────
    @Bean
    public NewTopic farmerRegisteredTopic() {
        return TopicBuilder.name("farmer.registered")
                .partitions(3)
                .replicas(1)
                .build();
    }
}

/*(

**Key things to notice and learn:**

`KafkaTemplate.send(topic, key, value)` — we use `farmerId` as the **message key**. This is important because
 Kafka guarantees ordering within a partition for the same key. All events for the same farmer always go to the same partition.

`CompletableFuture.whenComplete` — sending to Kafka is **asynchronous**. We don't block the main thread waiting for Kafka confirmation. 
Instead we attach a callback that logs success or failure.

`ENABLE_IDEMPOTENCE_CONFIG = true` — if network fails and producer retries, Kafka guarantees the message is written **exactly once**, not duplicated.

`ACKS_CONFIG = "all"` — producer waits until **all replicas** have written the message before confirming success. Most reliable setting.

`partitions(3)` — topic has 3 partitions meaning 3 consumers can read in parallel later for scaling.

`JsonSerializer` — we serialize our `FarmerRegisteredEvent` object as JSON automatically. Consumer side will deserialize it back.

---

**Your kafka package is now:**
```
kafka/
├── KafkaConfig.java              ✅
├── FarmerEventProducer.java      ✅
└── FarmerRegisteredEvent.java    ✅

*/