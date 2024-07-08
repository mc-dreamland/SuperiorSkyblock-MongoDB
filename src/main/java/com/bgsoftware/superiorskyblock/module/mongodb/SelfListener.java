package com.bgsoftware.superiorskyblock.module.mongodb;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.data.DatabaseBridge;
import com.bgsoftware.superiorskyblock.api.data.DatabaseFilter;
import com.bgsoftware.superiorskyblock.api.events.*;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.objects.Pair;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.database.cache.DatabaseCache;
import com.bgsoftware.superiorskyblock.core.database.serialization.IslandsDeserializer;
import com.bgsoftware.superiorskyblock.core.database.serialization.PlayersDeserializer;
import com.bgsoftware.superiorskyblock.core.logging.Log;
import com.bgsoftware.superiorskyblock.core.serialization.Serializers;
import com.bgsoftware.superiorskyblock.island.builder.IslandBuilderImpl;
import com.bgsoftware.superiorskyblock.module.mongodb.bridge.MongoDatabaseIslandSaver;
import com.bgsoftware.superiorskyblock.module.mongodb.container.MongoIslandContainer;
import com.bgsoftware.superiorskyblock.module.mongodb.container.MongoPlayerContainer;
import com.bgsoftware.superiorskyblock.module.mongodb.tools.DatabaseResult;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

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

    private static final UUID CONSOLE_UUID = new UUID(0, 0);
    private void loadPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        SuperiorPlayer superiorPlayer = plugin.getPlayers().getPlayersContainer().getSuperiorPlayer(uuid);


        if (superiorPlayer != null) {
            return;
        }

        DatabaseBridge playersLoader = SuperiorSkyblockPlugin.getPlugin().getFactory().createDatabaseBridge((SuperiorPlayer) null);
        MongoPlayerDataLoader.loadPlayer(plugin.getPlayers().getPlayersContainer(), uuid, playersLoader, true);
    }

    @EventHandler(priority = EventPriority.NORMAL  , ignoreCancelled = true)
    private void onPlayerJoinLoad(PlayerJoinEvent e) {
        loadPlayer(e.getPlayer());
    }

    @EventHandler
    private void onIslandCreate(IslandCreateEvent event) {
        Island island = event.getIsland();
        SuperiorPlayer owner = island.getOwner();
        updatePlayerIsland(owner, island);
    }

    @EventHandler
    private void onPlayerJoinIsland(IslandJoinEvent event) {
        SuperiorPlayer superiorPlayer = event.getPlayer();
        Island island = event.getIsland();
        updatePlayerIsland(superiorPlayer, island);
    }

    @EventHandler
    private void onPlayerLeaveIsland(IslandQuitEvent event) {
        SuperiorPlayer superiorPlayer = event.getPlayer();
        updatePlayerIsland(superiorPlayer, null);
    }

    @EventHandler
    private void onIslandDisband(IslandDisbandEvent event) {
        SuperiorPlayer superiorPlayer = event.getPlayer();
        updatePlayerIsland(superiorPlayer, null);
    }

    @EventHandler
    private void onBlockStack(BlockStackEvent event) {
        Block block = event.getBlock();
        int newCount = event.getNewCount();
        MongoDatabaseIslandSaver.updateStackedBlock(block, newCount);

    }

    @EventHandler
    private void onBlockUnstack(BlockUnstackEvent event) {
        Block block = event.getBlock();
        int newCount = event.getNewCount();
        MongoDatabaseIslandSaver.updateStackedBlock(block, newCount);
    }

    @EventHandler
    private void onIslandLoad(WorldLoadEvent event) {
        String name = event.getWorld().getName();
        if (!name.startsWith("island")) {
            return;
        }

        DatabaseBridge islandLoader = SuperiorSkyblockPlugin.getPlugin().getFactory().createDatabaseBridge((Island) null);
        islandLoader.loadObject("islands_info",
                DatabaseFilter.fromFilter("_id", name.split("_")[1]),
                resultSetRaw -> {
                    MongoIslandDataLoader.loadStackedBlock(new DatabaseResult(resultSetRaw));
                });

    }

    public static void updatePlayerIsland(SuperiorPlayer superiorPlayer, Island island) {
        MongoCollection<Document> collection = MongoDBClient.getCollection("players_info");

        if (island == null) {
            collection.updateOne(
                    new Document("_id", superiorPlayer.getUniqueId().toString()),
                    new Document("$unset", new Document("island", ""))
            );
        } else {
            collection.updateOne(
                    new Document("_id", superiorPlayer.getUniqueId().toString()),
                    new Document("$set", new Document("island", island.getUniqueId().toString()))
            );
        }
    }


}
