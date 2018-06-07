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
        System.out.print("ExecuteCalled. (" + payload.getType()+")");
        if (payload.getType().equalsIgnoreCase("PlayerWorldRequest")) {
            PlayerWorldRequest req = gson.fromJson(payload.getData(), PlayerWorldRequest.class);
            ProxiedPlayer player = MainPlugin.Players.get(java.util.UUID.fromString(req.getPlayerId()));

            PlayerWorldResponse r = new PlayerWorldResponse();
            r.setPlayerId(req.getPlayerId());
            r.setWorldName("Lobby");

            if (player != null) {
                System.out.print("found player!");
                System.out.print("Player on World: " + player.getServer().getInfo().getName());
                r.setWorldName(player.getServer().getInfo().getName());
            }
            else
            {
                System.out.print("Cannot find player!");
            }
            return new RabbitMessagePayload(r);
        }
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
