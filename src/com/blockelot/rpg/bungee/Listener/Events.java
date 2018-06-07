package com.blockelot.rpg.bungee.Listener;

import com.blockelot.rpg.bungee.MainPlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;

public class Events implements Listener {

    @EventHandler
    public void onPostLoginEvent(PostLoginEvent e) {
        if (!MainPlugin.Players.containsKey(e.getPlayer().getUniqueId())) {
            MainPlugin.Players.put(e.getPlayer().getUniqueId(), e.getPlayer());
        }
        MainPlugin.Instance.getProxy().broadcast(ChatColor.GREEN + "[+] " + ChatColor.DARK_GREEN + e.getPlayer().getName() + " has joined the network!");
    }

    @EventHandler
    public void onPlayerDisconnectEvent(PlayerDisconnectEvent e) {
        //ProxyServer.getInstance().getServers()
        
        if (MainPlugin.Players.containsKey(e.getPlayer().getUniqueId())) {
            MainPlugin.Players.remove(e.getPlayer().getUniqueId());
        }
        MainPlugin.Instance.getProxy().broadcast(ChatColor.DARK_RED + "[-] " + ChatColor.RED + e.getPlayer().getName() + " has left the network!");
    }

}
