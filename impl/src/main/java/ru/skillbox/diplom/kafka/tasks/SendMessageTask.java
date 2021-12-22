package ru.skillbox.diplom.kafka.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import ru.skillbox.diplom.kafka.dto.Payload;
import ru.skillbox.diplom.kafka.engine.Producer;

import java.time.LocalDate;
import java.util.concurrent.ExecutionException;

@Component
@ConditionalOnProperty(value = "unit.kafka.producer-enabled", havingValue = "true")
public class SendMessageTask {
    private final Logger logger = LoggerFactory.getLogger(SendMessageTask.class);

    private final Producer producer;

    public SendMessageTask(Producer producer) {
        this.producer = producer;
    }

    // run every 3 sec
    @Scheduled(fixedRateString = "3000")
    public void send() throws ExecutionException, InterruptedException {

        ListenableFuture<SendResult<String, Payload>> listenableFuture = this.producer.sendMessage("INPUT_DATA", "IN_KEY", new Payload("hello", 1L));

        SendResult<String, Payload> result = listenableFuture.get();
        logger.info(String.format("Produced:\ntopic: %s\noffset: %d\npartition: %d\nvalue size: %d", result.getRecordMetadata().topic(),
                result.getRecordMetadata().offset(),
                result.getRecordMetadata().partition(), result.getRecordMetadata().serializedValueSize()));
    }
}