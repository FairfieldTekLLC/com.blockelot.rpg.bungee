/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blockelot.rpg.bungee.MQ;

import com.blockelot.rpg.RpgPlayer.RabbitMQ.RabbitMessagePayload;
import com.blockelot.rpg.bungee.MainPlugin;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.ProxyServer;
import java.util.Iterator;
import java.util.*;
import net.md_5.bungee.api.config.ServerInfo;

/**
 *
 * @author geev
 */
public class PlayerMoveRequest extends RequestBase {

    private String PlayerId;
    private String TargetServer;

    public String getPlayerId() {
        return PlayerId;
    }

    public void setPlayerId(String playerId) {
        PlayerId = playerId;
    }

    public String getTargetServer() {
        return TargetServer;
    }

    public void setTargetServer(String server) {
        TargetServer = server;
    }

    @Override
    public void Execute() {
        ProxiedPlayer player = MainPlugin.Players.get(java.util.UUID.fromString(getPlayerId()));
        if (player == null) {
            System.out.print("Player Not Found");
            return;
        }

        if (player.getServer().getInfo().getName().equalsIgnoreCase(getTargetServer())) {
            System.out.print("Player Already on server");
            return;
        }

        String foundKey = null;

        for (String key : ProxyServer.getInstance().getServers().keySet()) {
            if (key.equalsIgnoreCase(getTargetServer())) {
                foundKey = key;
                break;
            }
        }
        if (foundKey == null) {
            System.out.print("Cannot find server " + getTargetServer());
            return;
        }

        ServerInfo serv = ProxyServer.getInstance().getServers().get(foundKey);
        player.connect(serv);

        System.out.print("Transfering " + player.getDisplayName() + " to server " + foundKey);
    }
}
