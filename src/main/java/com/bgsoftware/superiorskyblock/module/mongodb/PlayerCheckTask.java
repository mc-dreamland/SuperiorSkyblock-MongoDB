package com.bgsoftware.superiorskyblock.module.mongodb;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerCheckTask extends BukkitRunnable {
    private final SuperiorSkyblock plugin;
    private final Island island;
    private final UUID uuid;
    int checkTimes = 0;
    private static final Map<UUID, PlayerCheckTask> taskMap = new HashMap<>();

    public PlayerCheckTask(SuperiorSkyblock plugin, Island island) {
        this.plugin = plugin;
        this.island = island;
        this.uuid = island.getUniqueId();
        this.runTaskTimerAsynchronously(plugin, 60, 60 * 20L);
        taskMap.put(uuid, this);
    }

    @Override
    public void run() {
        // 在这里检测条件，如果不满足则取消任务并删除缓存
        if (!checkCondition()) {
            cancel();
            taskMap.remove(uuid);
            // 也可以在这里处理删除缓存的操作
        }
    }

    private void unloadIsland() {

    }

    private boolean checkCondition() {
        // 在这里编写你的条件检测逻辑
        boolean allOffline = true;
        for (SuperiorPlayer islandMember : island.getIslandMembers(true)) {
            if (islandMember.isOnline() || RedisClient.isPlayerActive(islandMember)) {
                allOffline = false;
                //TODO 检查redis缓存更新时间
                checkTimes = 0;
                break;
            }
            if (RedisClient.isPlayerActive(islandMember)) {
                checkTimes = 0;
                break;
            }
        }
        if (allOffline) {
            checkTimes++;
            return checkTimes < 5;
        } else {
            for (SuperiorPlayer islandMember : island.getIslandMembers(true)) {
                RedisClient.updatePlayer(islandMember);
            }
        }

        return true; // 示例：始终返回true


    }
}
