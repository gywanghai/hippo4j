package cn.hippo4j.springboot.starter.adapter.springcloud.stream.rocketmq.example;

import cn.hippo4j.example.core.dto.SendMessageDTO;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Message produce.
 */
@Slf4j
@RestController
@AllArgsConstructor
public class MessageProduce {

    private final MessageChannel output;

    public static final String MESSAGE_CENTER_SEND_MESSAGE_TAG = "framework_message-center_send-message_tag";

    public static final String MESSAGE_CENTER_SAVE_MESSAGE_TAG = "framework_message-center_save-message_tag";

    @GetMapping("/message/send")
    public String sendMessage() {
        int maxSendSize = 10;
        for (int i = 0; i < maxSendSize; i++) {
            sendMessage(MESSAGE_CENTER_SEND_MESSAGE_TAG);
            sendMessage(MESSAGE_CENTER_SAVE_MESSAGE_TAG);
        }
        return "success";
    }

    private void sendMessage(String tags) {
        String keys = UUID.randomUUID().toString();
        SendMessageDTO payload = SendMessageDTO.builder()
                .receiver("156011xxx91")
                .uid(keys)
                .build();
        Message<?> message = MessageBuilder
                .withPayload(JSON.toJSONString(payload))
                .setHeader(MessageConst.PROPERTY_KEYS, keys)
                .setHeader(MessageConst.PROPERTY_TAGS, tags)
                .build();
        long startTime = System.currentTimeMillis();
        boolean sendResult = false;
        try {
            sendResult = output.send(message, 2000L);
        } finally {
            log.info("Send status: {}, Keys: {}, Execute time: {} ms, Message: {}",
                    sendResult,
                    keys,
                    System.currentTimeMillis() - startTime,
                    JSON.toJSONString(payload));
        }
    }
}