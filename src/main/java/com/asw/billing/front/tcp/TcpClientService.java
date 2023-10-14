package com.asw.billing.front.tcp;

import com.asw.billing.dto.DTOCardOperationList;
import com.asw.billing.service.BillingService;
import com.asw.billing.storage.BillingStorage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
public class TcpClientService implements Runnable {

    private final Socket socket;
    private final BillingService billingService;
    private final ObjectMapper jsonMapper;

    public TcpClientService(Socket socket, BillingService billingService, ObjectMapper jsonMapper) {
        this.socket = socket;
        this.billingService = billingService;
        this.jsonMapper = jsonMapper;
        try {
            this.socket.setSoTimeout(100);
            this.socket.setKeepAlive(true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            //сообщения разделяются \n
            while (true) {
                int length = 4096 * 4;
                StringBuilder dataString = new StringBuilder();
                InputStream in = socket.getInputStream();
                if (!socket.isConnected()) break;
                byte[] messageByte = new byte[length];
                int totalBytesRead = 0;
                while (true) {
                    //log.info("in  while before read {} ", this);
                    int currentBytesRead = in.read(messageByte);
                    //log.info("currentBytesRead:{}, this:{}, is Connected:{}, isClosed:{}", currentBytesRead, this, socket.isConnected(), socket.isClosed());
                    if (currentBytesRead == -1) return;

                    totalBytesRead = currentBytesRead + totalBytesRead;
                    dataString
                            .append(new String(messageByte, 0, currentBytesRead, StandardCharsets.UTF_8));
                    //log.info("readed: {}", dataString);
                    if (messageByte[currentBytesRead - 1] == '\n') break;
                }
                //log.info("after while {} ", this);
                if (dataString.isEmpty()) continue;
                String content = dataString.toString();
                //log.info("content={}", content);
                JsonNode jsonNode = jsonMapper.readTree(content);
                JsonNodeType nodeType = jsonNode.getNodeType();
                //log.info("nodeType={}", nodeType);

                if (JsonNodeType.OBJECT == nodeType){
                    DTOCardOperationList operationList = jsonMapper.readValue(content, DTOCardOperationList.class);
                    //log.info("process operations: {}", operationList.operationList());
                    billingService.processOperations(operationList.operationList().stream()
                            .map(dto -> new BillingStorage.CardOperation(dto.cardId(), dto.operationDateTime(), dto.amount()))
                            .collect(Collectors.toList()));
                    socket.getOutputStream().write("1".getBytes(StandardCharsets.UTF_8));
                } else if (JsonNodeType.STRING == nodeType){
                    content = jsonNode.asText();
                    BillingStorage.Card card = billingService.createCard(content);
                    String data = jsonMapper.writeValueAsString(card);
                    socket.getOutputStream().write(data.getBytes(StandardCharsets.UTF_8));
                }else if (JsonNodeType.NUMBER == nodeType){
                    Long id = jsonNode.asLong();
                    BillingStorage.Card card = billingService.getCard(id);
                    String data = jsonMapper.writeValueAsString(card);
                    //log.info("send data {}", data);
                    socket.getOutputStream().write(data.getBytes(StandardCharsets.UTF_8));
                }else{
                    log.info("Bad node type:{}, content:{}", nodeType, content);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage() + " this:" + this);
        }


    }
}
