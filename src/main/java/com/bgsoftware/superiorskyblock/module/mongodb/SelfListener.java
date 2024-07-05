package com.bgsoftware.superiorskyblock.module.mongodb;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.events.PluginInitializeEvent;
import com.bgsoftware.superiorskyblock.api.events.PluginLoadDataEvent;
import com.bgsoftware.superiorskyblock.module.mongodb.container.MongoIslandContainer;
import com.bgsoftware.superiorskyblock.module.mongodb.container.MongoPlayerContainer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SelfListener implements Listener {
    private SuperiorSkyblock plugin;

    public SelfListener(SuperiorSkyblock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPluginInitializeEvent(PluginInitializeEvent event) {
        MongoPlayerContainer mongoPlayerContainer = new MongoPlayerContainer(this.plugin);
        MongoIslandContainer mongoIslandContainer = new MongoIslandContainer(this.plugin);
        event.setPlayersContainer(mongoPlayerContainer);
        event.setIslandsContainer(mongoIslandContainer);
    }

    @EventHandler
    public void onPluginLoadDataEvent(PluginLoadDataEvent event) {
        event.setCancelled(true);

    }
}
