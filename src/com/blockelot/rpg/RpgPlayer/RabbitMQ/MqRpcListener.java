/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blockelot.rpg.RpgPlayer.RabbitMQ;


import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author geev
 */
public final class MqRpcListener extends Thread implements AutoCloseable {

    private final String Server;
    private final String Exchange;
    private final String RpcQueue;
    private final BuiltinExchangeType Type;
    private final RabbitExecuter Executer;
    private Boolean Stop = false;
    private Boolean IsStopped = false;

    private ConnectionFactory Factory;
    private Connection Connection;
    private Channel Channel;
    //private Consumer Consumer;
    private final Gson gson = new Gson();

    public Boolean getIsStopped() {
        return IsStopped;
    }

    public MqRpcListener(String name, String server, String exchange, String rpcQueue, BuiltinExchangeType type, Boolean listen, RabbitExecuter executer) throws Exception {
        super(name);
        Server = server;
        Exchange = exchange;
        RpcQueue = rpcQueue;
        Type = type;
        Executer = executer;

    }

    @Override
    public void run() {
        Connection = null;
        try {
            Factory = new ConnectionFactory();
            Factory.setHost(Server);
            Factory.setUsername("minecraft");
            Factory.setPassword("minecraft");
            Factory.setVirtualHost("/");
            Connection = Factory.newConnection();
            Channel = Connection.createChannel();
            Channel.exchangeDeclare(Exchange, Type, true);
            Channel.queueDeclare(RpcQueue, true, true, false, null);
            Channel.basicQos(0, 20, false);
            Channel.queueBind(RpcQueue, Exchange, RpcQueue);
            System.out.println(" [x] Awaiting RPC requests");
            final Consumer Consumer;
            Consumer = new DefaultConsumer(Channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder()
                            .correlationId(properties.getCorrelationId())
                            .build();
                    String response = "";

                    try {
                        String message = new String(body, "UTF-8");
                        RabbitMessagePayload payload = gson.fromJson(message, RabbitMessagePayload.class);
                        RabbitMessagePayload returnLoad = Executer.Execute(payload);
                        response = gson.toJson(returnLoad);
                    } catch (RuntimeException e) {
                        System.out.println(" [.] " + e.toString());
                    } finally {
                        System.out.print("Sending Message to: " + properties.getReplyTo());
                        
                        String rQueue =properties.getReplyTo().split(":")[1];
                        String rExchange =properties.getReplyTo().split(":")[0];
                        
                        Channel.basicPublish(rExchange, rQueue, replyProps, response.getBytes("UTF-8"));
                        Channel.basicAck(envelope.getDeliveryTag(), false);
                        // RabbitMq consumer worker thread notifies the RPC server owner thread 
                        synchronized (MqRpcListener.this) {
                            this.notify();
                        }
                    }
                }
            };
            Channel.basicConsume(RpcQueue, false, Consumer);
            while (!Stop) {
                synchronized (Consumer) {
                    try {
                        Consumer.wait();
                    } catch (InterruptedException e) {
                        System.out.print(e.getMessage());
                        System.out.print(Arrays.toString(e.getStackTrace()));
                    }
                }
                IsStopped = true;
                close();
            }
        } catch (IOException | TimeoutException e) {
            System.out.print(e.getMessage());
            System.out.print(Arrays.toString(e.getStackTrace()));
        }
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

    public void Stop() {
        Stop = true;
    }

}
