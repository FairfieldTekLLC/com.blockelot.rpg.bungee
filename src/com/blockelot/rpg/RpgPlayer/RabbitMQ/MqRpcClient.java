/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blockelot.rpg.RpgPlayer.RabbitMQ;
import com.google.gson.Gson;
import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author geev
 */
public class MqRpcClient implements AutoCloseable {

    private Connection Connection;
    private Channel Channel;
    private final String Exchange;
    private final Gson gson = new Gson();

    public MqRpcClient(String server, String exchange, BuiltinExchangeType type) throws IOException, TimeoutException {
        Exchange = exchange;
        //Create the Connection Factory
        ConnectionFactory Factory = new ConnectionFactory();
        Factory.setHost(server);
        Factory.setUsername("minecraft");
        Factory.setPassword("minecraft");
        Factory.setVirtualHost("/");
        Connection = Factory.newConnection();
        Channel = Connection.createChannel();
        //Only need one message at a time, since we only expect one message
        Channel.basicQos(1);
        //We create the exchange incase it doesn't exist
        Channel.exchangeDeclare(Exchange, type, true);
    }
    
    public <T> T Call(String exchange, String queueName, Object payload, long timeoutSeconds, Class<T> clazz) throws IOException, InterruptedException, TimeoutException 
    {
        RabbitMessagePayload Sendpayload = new RabbitMessagePayload(payload);
        RabbitMessagePayload response = call(exchange,queueName,Sendpayload,timeoutSeconds);
        return clazz.cast(gson.fromJson(response.getData(), clazz));
    }
    

    private RabbitMessagePayload call(String exchange, String queueName, RabbitMessagePayload rmp, long timeoutSeconds) throws IOException, InterruptedException, TimeoutException {
        //Really don't need this since we create a Queue for each call.
        final String corrId = UUID.randomUUID().toString();
        //A temporary Queue used to get the reply.
        String replyQueueName = queueName + "." + UUID.randomUUID().toString();
        Channel.queueDeclare(replyQueueName, true, false, true, null);
        //Bind the Queue to the channel
        Channel.queueBind(replyQueueName, exchange, replyQueueName);
        //Build the message properties
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .correlationId(corrId)
                .replyTo(exchange + ":" + replyQueueName)
                .build();
        //Convert the object to JSon
        String message = gson.toJson(rmp);
        //Publish the message
        Channel.basicPublish(exchange, queueName, props, message.getBytes("UTF-8"));
        RabbitMessagePayload payload = null;
        //Start a timer
        long start = System.currentTimeMillis();
        while (payload == null) {
            //Non-Blocking, will return null if no message.
            GetResponse resp = Channel.basicGet(replyQueueName, false);
            if (resp == null) {
                //Check timeout.
                long now = System.currentTimeMillis();
                if ((now - start) > (timeoutSeconds * 1000)) {
                    throw new TimeoutException("RPC Call took to long.");
                }
                continue;
            }
            //Ack the message.
            Channel.basicAck(resp.getEnvelope().getDeliveryTag(), false);
            //Convert string data to payload
            payload = gson.fromJson(new String(resp.getBody(), "UTF-8"), RabbitMessagePayload.class);
        }
        //Delete the queue
        Channel.queueDelete(replyQueueName);
        
        return payload;
    }

    @Override
    public void close() {
        try {
            if (Channel.isOpen()) {
                Channel.close();
            }
        } catch (IOException | TimeoutException e) {
            System.out.print(e.getMessage());
            System.out.print(Arrays.toString(e.getStackTrace()));
        }
        Channel = null;
        try {
            if (Connection.isOpen()) {
                Connection.close();

            }
        } catch (IOException e) {
            System.out.print(e.getMessage());
           System.out.print(Arrays.toString(e.getStackTrace()));
        }
        Connection = null;
    }
}
