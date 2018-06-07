package com.blockelot.rpg.bungee;

import java.util.HashMap;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import java.util.UUID;
import com.blockelot.rpg.bungee.Listener.Events;
import com.blockelot.rpg.RpgPlayer.RabbitMQ.MqRpcListener;
import com.rabbitmq.client.BuiltinExchangeType;
import com.blockelot.rpg.bungee.MQ.CommandExecuter;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class MainPlugin extends Plugin {

    public static HashMap<UUID, ProxiedPlayer> Players = new HashMap<UUID, ProxiedPlayer>();
    
    private CommandExecuter Exec;
    private static MqRpcListener MqListen;

    public MainPlugin() {
        Instance = this;

    }
    public static MainPlugin Instance;

    @Override
    public void onEnable() {
        // You should not put an enable message in your plugin.
        // BungeeCord already does so
        getProxy().getPluginManager().registerListener(this, new Events());
        try {
            Exec = new CommandExecuter();
            MqListen = new MqRpcListener("BungeeQueue", "192.168.211.63", "Minecraft", "BungeeQueue", BuiltinExchangeType.DIRECT, Boolean.TRUE, Exec);
            (new Thread(MqListen)).start();
            
        } catch (Exception e) {
            System.out.print("Exception: " + e.getMessage());
            System.out.print("Exception: " + Arrays.toString(e.getStackTrace()));
        }
        getProxy().getScheduler().schedule(this, Exec, 1, TimeUnit.SECONDS);
        getLogger().info("Yay! It loads!");

        

    }

    @Override

    public void onDisable() {
        if (MqListen != null) {
            MqListen.Stop();
            MqListen.close();
        }
        

    }

}
