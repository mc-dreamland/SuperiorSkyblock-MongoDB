package com.bgsoftware.superiorskyblock.module.mongodb.container;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.data.DatabaseFilter;
import com.bgsoftware.superiorskyblock.api.player.container.PlayersContainer;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.SequentialListBuilder;
import com.bgsoftware.superiorskyblock.module.mongodb.MongoPlayerDataLoader;
import com.bgsoftware.superiorskyblock.module.mongodb.bridge.MongoDatabaseBridge;
import com.bgsoftware.superiorskyblock.module.mongodb.tools.DatabaseResult;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MongoPlayerContainer implements PlayersContainer {
    private SuperiorSkyblock plugin;
    private static final UUID CONSOLE_UUID = new UUID(0, 0);
    private final Map<UUID, SuperiorPlayer> players = new ConcurrentHashMap<>();
    private final Map<String, SuperiorPlayer> playersByNames = new ConcurrentHashMap<>();

    public MongoPlayerContainer(SuperiorSkyblock plugin) {
        this.plugin = plugin;
    }

    @Nullable
    @Override
    public SuperiorPlayer getSuperiorPlayer(String name) {
        System.out.println("MongoPlayerContainer getSuperiorPlayer #1");
        SuperiorPlayer superiorPlayer = this.playersByNames.get(name.toLowerCase(Locale.ENGLISH));

        if (superiorPlayer == null) {
            superiorPlayer = players.values().stream()
                    .filter(player -> player.getName().equalsIgnoreCase(name))
                    .findFirst().orElse(null);
            if (superiorPlayer != null) {
                this.playersByNames.put(name.toLowerCase(Locale.ENGLISH), superiorPlayer);
            }
        }

        return superiorPlayer;
    }

    @Nullable
    @Override
    public SuperiorPlayer getSuperiorPlayer(UUID playerUUID) {
        return this.players.get(playerUUID);
    }

    @Override
    public List<SuperiorPlayer> getAllPlayers() {
        return new SequentialListBuilder<SuperiorPlayer>().build(this.players.values());
    }

    @Override
    public void addPlayer(SuperiorPlayer superiorPlayer) {
        this.players.put(superiorPlayer.getUniqueId(), superiorPlayer);
        String playerName = superiorPlayer.getName();
        if (!"null".equals(playerName)) {
            this.playersByNames.put(playerName.toLowerCase(Locale.ENGLISH), superiorPlayer);
        }
    }

    @Override
    public void removePlayer(SuperiorPlayer superiorPlayer) {
        this.players.remove(superiorPlayer.getUniqueId());
        this.playersByNames.remove(superiorPlayer.getName().toLowerCase(Locale.ENGLISH));
    }
}
