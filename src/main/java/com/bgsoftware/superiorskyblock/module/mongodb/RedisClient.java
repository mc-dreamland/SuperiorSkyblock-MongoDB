package com.bgsoftware.superiorskyblock.module.mongodb;

import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.google.common.base.Preconditions;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClients;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class RedisClient {

    public static JedisPool jedisPool;
    private static int index;

    public static void connect(String url, int dbIndex) {
        // 配置连接池
        GenericObjectPoolConfig<Jedis> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(5);
        config.setMaxIdle(3);
        config.setMinIdle(2);

        // 创建连接池
        jedisPool = new JedisPool(config, url, 6379);
        index = dbIndex;
    }

    public static void open(Consumer<Jedis> consumer) {
        Preconditions.checkArgument(index >= 0);
        Jedis jedis = jedisPool.getResource();
        jedis.select(index);
        if (index != 0) {
            jedis.select(index);
        }
        try {
            consumer.accept(jedis);
        } finally {
            jedis.select(0);
            jedis.close();
        }
    }

    /**
     * 获取缓存中数据在哪个服务器中加载的。
     * 如果redis中数据超过10分钟没更新，则视为数据已过期。
     * @param playerUUID
     * @return 服务器名字，若没加载则返回空
     */
    public static String getLoadedServer(UUID playerUUID) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.select(index);
            Map<String, String> playerCache = jedis.hgetAll("SkyCityPlayers:" + playerUUID.toString());
            if (playerCache == null || playerCache.isEmpty()) {
                return "";
            }
            long l = Long.parseLong(playerCache.getOrDefault("update", "-1"));
            if (System.currentTimeMillis() - l > 3600000) {
                return "";
            }
            String server = playerCache.get("server");
            if (Utils.isSelfServer(server)) {
                jedis.hset("SkyCityPlayers:" + playerUUID.toString(), "update", String.valueOf(System.currentTimeMillis()));
                return server;
            }

        } finally {
            jedis.select(0);
            jedis.close();
        }
        return "";
    }

    public static void cachePlayer(SuperiorPlayer player) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.select(index);
            Map<String, String> playerInfo = new HashMap<>();
            playerInfo.put("update", String.valueOf(System.currentTimeMillis()));
            playerInfo.put("server", Utils.getSelfServerName());
            jedis.hset("SkyCityPlayer:" + player.getUniqueId().toString(), playerInfo);
            jedis.expire("SkyCityPlayer:" + player.getUniqueId().toString(), 3600);

        } finally {
            jedis.select(0);
            jedis.close();
        }
    }

    public static void cacheIsland(Island island) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.select(index);
            Map<String, String> playerInfo = new HashMap<>();
            playerInfo.put("update", String.valueOf(System.currentTimeMillis()));
            playerInfo.put("server", Utils.getSelfServerName());
            jedis.hset("SkyCityIsland:" + island.getUniqueId().toString(), playerInfo);
            jedis.expire("SkyCityIsland:" + island.getUniqueId().toString(), 3600);

        } finally {
            jedis.select(0);
            jedis.close();
        }
    }

    public static boolean isPlayerActive(SuperiorPlayer islandMember) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.select(index);
            UUID playerUUID = islandMember.getUniqueId();
            Map<String, String> playerCache = jedis.hgetAll("SkyCityPlayers:" + playerUUID.toString());
            if (playerCache == null || playerCache.isEmpty()) {
                return false;
            }
            long l = Long.parseLong(playerCache.getOrDefault("update", "-1"));
            if (System.currentTimeMillis() - l < 300000) {
                return true;
            }
        } finally {
            jedis.select(0);
            jedis.close();
        }
        return false;
    }

    public static void updatePlayer(SuperiorPlayer player) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.select(index);
            UUID playerUUID = player.getUniqueId();
            jedis.hset("SkyCityPlayers:" + playerUUID.toString(), "update", String.valueOf(System.currentTimeMillis()));
        } finally {
            jedis.select(0);
            jedis.close();
        }
    }
}
