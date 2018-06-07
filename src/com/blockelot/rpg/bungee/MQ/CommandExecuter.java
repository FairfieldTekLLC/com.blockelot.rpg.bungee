/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blockelot.rpg.bungee.MQ;

import com.blockelot.rpg.RpgPlayer.RabbitMQ.RabbitExecuter;
import com.blockelot.rpg.RpgPlayer.RabbitMQ.RabbitMessagePayload;
import com.blockelot.rpg.bungee.MainPlugin;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import java.util.UUID;
import com.google.gson.Gson;
import java.util.Arrays;
import java.util.concurrent.*;

/**
 *
 * @author geev
 */
public class CommandExecuter extends RabbitExecuter implements Runnable {

    private final Gson gson = new Gson();
    private static BlockingQueue CommandQueue = new ArrayBlockingQueue(1024);

    @Override
    public RabbitMessagePayload Execute(RabbitMessagePayload payload) {
        if (payload.getType().equalsIgnoreCase("PlayerMoveRequest")) {
            PlayerMoveRequest req = gson.fromJson(payload.getData(), PlayerMoveRequest.class);
            CommandQueue.add(req);
        }
        return new RabbitMessagePayload(new ResponseOk());
    }

    @Override
    public void run() {
        //System.out.print("Checking Queue");
        while (!CommandQueue.isEmpty()) {
            try {
                RequestBase base = (RequestBase) CommandQueue.take();
                base.Execute();
            } catch (Exception e) {
                System.out.print("Error: " + e.getMessage());
                System.out.print("Error: " + Arrays.toString(e.getStackTrace()));
            }
        }
        MainPlugin.Instance.getProxy().getScheduler().schedule(MainPlugin.Instance, this, 1, TimeUnit.SECONDS);
    }

}
