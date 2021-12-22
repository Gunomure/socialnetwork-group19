package ru.skillbox.diplom.kafka.engine;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import ru.skillbox.diplom.kafka.dto.Payload;

@Service
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@ConditionalOnProperty(value = "unit.kafka.producer-enabled", havingValue = "true")
public class Producer {

    private final KafkaTemplate<String, Payload> kafkaTemplate;

    public Producer(KafkaTemplate<String, Payload> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public ListenableFuture<SendResult<String, Payload>> sendMessage(String topic, String key, Payload message) {

        return this.kafkaTemplate.send(topic, key, message);
    }


}
