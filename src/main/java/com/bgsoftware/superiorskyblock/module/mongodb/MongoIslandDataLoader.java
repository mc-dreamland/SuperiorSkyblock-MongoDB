package com.bgsoftware.superiorskyblock.module.mongodb;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.data.DatabaseBridge;
import com.bgsoftware.superiorskyblock.api.data.DatabaseFilter;
import com.bgsoftware.superiorskyblock.api.enums.Rating;
import com.bgsoftware.superiorskyblock.api.hooks.LazyWorldsProvider;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandFlag;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;
import com.bgsoftware.superiorskyblock.api.island.PlayerRole;
import com.bgsoftware.superiorskyblock.api.key.Key;
import com.bgsoftware.superiorskyblock.api.missions.Mission;
import com.bgsoftware.superiorskyblock.api.upgrades.Upgrade;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.EnumHelper;
import com.bgsoftware.superiorskyblock.core.LazyReference;
import com.bgsoftware.superiorskyblock.core.LazyWorldLocation;
import com.bgsoftware.superiorskyblock.core.Text;
import com.bgsoftware.superiorskyblock.core.database.cache.DatabaseCache;
import com.bgsoftware.superiorskyblock.core.database.serialization.IslandsDeserializer;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.key.Keys;
import com.bgsoftware.superiorskyblock.core.logging.Log;
import com.bgsoftware.superiorskyblock.core.serialization.Serializers;
import com.bgsoftware.superiorskyblock.core.stackedblocks.StackedBlock;
import com.bgsoftware.superiorskyblock.island.IslandUtils;
import com.bgsoftware.superiorskyblock.island.bank.SBankTransaction;
import com.bgsoftware.superiorskyblock.island.builder.IslandBuilderImpl;
import com.bgsoftware.superiorskyblock.island.role.SPlayerRole;
import com.bgsoftware.superiorskyblock.module.BuiltinModules;
import com.bgsoftware.superiorskyblock.module.mongodb.bridge.MongoDatabaseBridge;
import com.bgsoftware.superiorskyblock.module.mongodb.container.MongoIslandContainer;
import com.bgsoftware.superiorskyblock.module.mongodb.tools.DatabaseResult;
import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MongoIslandDataLoader {

    private static final SuperiorSkyblockPlugin plugin = SuperiorSkyblockPlugin.getPlugin();

    private static final Gson GSON = new GsonBuilder().create();
    private static final BigDecimal SYNCED_BANK_LIMIT_VALUE = BigDecimal.valueOf(-2);

    public static void loadIsland(UUID islandUUID, DatabaseBridge mongoDatabaseBridge) {
        System.out.println("loadIsland -> #1");
        mongoDatabaseBridge.loadObject("islands_info",
                DatabaseFilter.fromFilter("_id", islandUUID.toString()),
                resultSetRaw -> {
                    if (resultSetRaw == null) {
                        return;
                    }
                    DatabaseResult databaseResult = new DatabaseResult(resultSetRaw);
                    IslandBuilderImpl builder = new IslandBuilderImpl();

                    Optional<UUID> ownerUUID = databaseResult.getUUID("owner");
                    if (ownerUUID.isEmpty()) {
                        Log.warn("Cannot load island with invalid owner uuid, skipping...");
                        return;
                    }

                    System.out.println("loadIsland -> #2");
                    SuperiorPlayer owner = plugin.getPlayers().getSuperiorPlayer(ownerUUID.get(), false);
                    if (owner == null) {
                        DatabaseBridge playersLoader = SuperiorSkyblockPlugin.getPlugin().getFactory().createDatabaseBridge((SuperiorPlayer) null);
                        MongoPlayerDataLoader.loadPlayer(plugin.getPlayers().getPlayersContainer(), ownerUUID.get(), playersLoader, false);

                        owner = plugin.getPlayers().getSuperiorPlayer(ownerUUID.get(), false);
                        if (owner == null) {
                            Log.warn("Cannot load island with unrecognized owner uuid: " + ownerUUID.get() + ", skipping...");
//                            return;
                        }
                    }
                    System.out.println("loadIsland -> #3");
                    Optional<Location> center = databaseResult.getString("center").map(Serializers.LOCATION_SERIALIZER::deserialize);
                    if (center.isEmpty()) {
                        Log.warn("Cannot load island with invalid center, skipping...");
                        return;
                    }
                    deserializeIslandHomes(databaseResult, builder);
                    deserializeMembers(databaseResult, builder);
                    deserializeBanned(databaseResult, builder);
                    deserializePlayerPermissions(databaseResult, builder);
                    deserializeRolePermissions(databaseResult, builder);
                    deserializeUpgrades(databaseResult, builder);
                    deserializeWarps(databaseResult, builder);
                    deserializeBlockLimits(databaseResult, builder);
                    deserializeRatings(databaseResult, builder);
                    deserializeMissions(databaseResult, builder);
                    deserializeIslandFlags(databaseResult, builder);
                    deserializeGenerators(databaseResult, builder);
                    deserializeVisitors(databaseResult, builder);
                    deserializeEntityLimits(databaseResult, builder);
                    deserializeEffects(databaseResult, builder);
                    deserializeIslandChest(databaseResult, builder);
                    deserializeRoleLimits(databaseResult, builder);
                    deserializeWarpCategories(databaseResult, builder);
                    deserializeIslandBank(databaseResult, builder);
                    deserializeVisitorHomes(databaseResult, builder);
                    deserializeIslandSettings(databaseResult, builder);
                    deserializeBankTransactions(databaseResult, builder);
                    deserializePersistentDataContainer(databaseResult, builder);

                    builder.setOwner(owner)
                            .setUniqueId(islandUUID)
                            .setCenter(center.get())
                            .setName(databaseResult.getString("name").orElse(""))
                            .setSchematicName(databaseResult.getString("island_type").orElse(null))
                            .setCreationTime(databaseResult.getLong("creation_time").orElse(System.currentTimeMillis() / 1000L))
                            .setDiscord(databaseResult.getString("discord").orElse("None"))
                            .setPaypal(databaseResult.getString("paypal").orElse("None"))
                            .setBonusWorth(databaseResult.getBigDecimal("worth_bonus").orElse(BigDecimal.ZERO))
                            .setBonusLevel(databaseResult.getBigDecimal("levels_bonus").orElse(BigDecimal.ZERO))
                            .setLocked(databaseResult.getBoolean("locked").orElse(false))
                            .setIgnored(databaseResult.getBoolean("ignored").orElse(false))
                            .setDescription(databaseResult.getString("description").orElse(""))
                            .setGeneratedSchematics(databaseResult.getInt("generated_schematics").orElse(0))
                            .setUnlockedWorlds(databaseResult.getInt("unlocked_worlds").orElse(0))
                            .setLastTimeUpdated(databaseResult.getLong("last_time_updated").orElse(System.currentTimeMillis() / 1000L));

                    databaseResult.getMap("dirty_chunks").ifPresent(dirtyChunks -> {
                        deserializeDirtyChunks(builder, dirtyChunks);
                    });

                    databaseResult.getMap("block_counts").ifPresent(blockCounts -> {
                        deserializeBlockCounts(builder, blockCounts);
                    });

                    databaseResult.getString("entity_counts").ifPresent(entityCounts -> {
                        deserializeEntityCounts(builder, entityCounts);
                    });

                    Island build = builder.build();
                    plugin.getGrid().getIslandsContainer().addIsland(build);

                    RedisClient.cacheIsland(build);
                });
    }

    public static void loadStackedBlock(DatabaseResult islandDatabaseResult) {
        Optional<Map<String, Object>> stackBlocks = islandDatabaseResult.getMap("stack_blocks");
        if (!stackBlocks.isPresent()) {
            return;
        }
        String islandKey = islandDatabaseResult.getString("_id").get();
        for (Map.Entry<String, Object> blocks : stackBlocks.get().entrySet()) {
            String loc = blocks.getKey();
            String info = (String) blocks.getValue();

            String[] split = loc.split(",");
            int amount = Integer.parseInt(info.split(",")[1]);
            World world = Bukkit.getWorld("island_" + islandKey + "_" + split[0]);
            if (world == null) {
                continue;
            }
            Location location = new Location(world, Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
            plugin.getStackedBlocks().setStackedBlock(location.getBlock(), amount);
        }
    }


    public static void deserializeMembers(DatabaseResult islandDatabaseResult, Island.Builder builder) {
        // {
        //      "UUID": {
        //        "role": "str",
        //        "join_time": 123L
        //      }
        // }

        System.out.println("deserializeMembers -> #1");
        Optional<Map<String, Object>> members = islandDatabaseResult.getMap("members");
        if (members.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> memberInfo : members.get().entrySet()) {
            UUID playerUUID = UUID.fromString(memberInfo.getKey());
            Optional<Map<String, Object>> map = DatabaseResult.toMap(memberInfo.getValue());
            if (map.isEmpty()) {
                //If the code go here, it seems the data save with error!
                Log.warn("Cannot load island member with unrecognized uuid: " + playerUUID + ", skipping #1...");
                continue;
            }


            System.out.println("deserializeMembers -> #2 -> " + playerUUID);
            SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(playerUUID, false);
            if (superiorPlayer == null) {
                System.out.println("deserializeMembers -> #3");
                DatabaseBridge playersLoader = SuperiorSkyblockPlugin.getPlugin().getFactory().createDatabaseBridge((SuperiorPlayer) null);
                MongoPlayerDataLoader.loadPlayer(plugin.getPlayers().getPlayersContainer(), playerUUID, playersLoader, false);
                superiorPlayer = plugin.getPlayers().getSuperiorPlayer(playerUUID, false);
                if (superiorPlayer == null) {
                    Log.warn("成员信息加载失败！");
                    continue;
                }
//                continue;
            }
            System.out.println("deserializeMembers -> #4");
            DatabaseResult result = new DatabaseResult(map.get());

            PlayerRole playerRole = result.getInt("role").map(SPlayerRole::fromId)
                    .orElse(SPlayerRole.defaultRole());

            superiorPlayer.setPlayerRole(playerRole);
            builder.addIslandMember(superiorPlayer);
        }
    }

    public static void deserializeBanned(DatabaseResult islandDatabaseResult, Island.Builder builder) {
        // {
        //      "playerUUID": {
        //        "banned_by": "str",
        //        "banned_time": 123L
        //      }
        // }
        Optional<Map<String, Object>> banMembers = islandDatabaseResult.getMap("bans");
        if (banMembers.isEmpty()) {
            return;
        }
        for (String p : banMembers.get().keySet()) {
            UUID playerUUID = UUID.fromString(p);
            SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(playerUUID, false);
            if (superiorPlayer == null) {
                Log.warn("Cannot load island ban with unrecognized uuid: " + playerUUID + ", skipping...");
                continue;
            }
            builder.addBannedPlayer(superiorPlayer);
        }
    }

    public static void deserializeVisitors(DatabaseResult islandDatabaseResult, Island.Builder builder) {
        Optional<Map<String, Object>> visitorsMap = islandDatabaseResult.getMap("visitors");
        if (visitorsMap.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> memberInfo : visitorsMap.get().entrySet()) {
            UUID playerUUID = UUID.fromString(memberInfo.getKey());
            SuperiorPlayer visitorPlayer = plugin.getPlayers().getSuperiorPlayer(playerUUID, false);
            if (visitorPlayer == null) {
                Log.warn("Cannot load island visitor with unrecognized uuid: " + playerUUID + ", skipping...");
                return;
            }
            long visitTime = (long) memberInfo.getValue();
            builder.addUniqueVisitor(visitorPlayer, visitTime);
        }
    }

    public static void deserializePlayerPermissions(DatabaseResult islandDatabaseResult, Island.Builder builder) {

        Optional<Map<String, Object>> playerPermissions = islandDatabaseResult.getMap("player_permissions");
        if (playerPermissions.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> playerPermission : playerPermissions.get().entrySet()) {
            UUID playerUUID = UUID.fromString(playerPermission.getKey());
            SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(playerUUID, false);
            if (superiorPlayer == null) {
                Log.warn("Cannot load island player permissions with unrecognized uuid: " + playerUUID + ", skipping...");
                return;
            }
            Optional<Map<String, Object>> map = DatabaseResult.toMap(playerPermission.getValue());
            if (map.isEmpty()) {
                continue;
            }
            map.get().forEach((k, v) -> {
                builder.setPlayerPermission(superiorPlayer, IslandPrivilege.getByName(k), (int) v == 1);
            });
        }
    }

    public static void deserializeRolePermissions(DatabaseResult islandDatabaseResult, Island.Builder builder) {


        Optional<Map<String, Object>> rolePermissions = islandDatabaseResult.getMap("role_permissions");
        if (rolePermissions.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> playerPermission : rolePermissions.get().entrySet()) {
            PlayerRole playerRole = SPlayerRole.fromId((int) playerPermission.getValue());
            IslandPrivilege islandPrivilege = IslandPrivilege.getByName(playerPermission.getKey());
            if (islandPrivilege == null) {
                continue;
            }

            Optional<Map<String, Object>> map = DatabaseResult.toMap(playerPermission.getValue());
            if (map.isEmpty()) {
                Log.warn("Cannot load role permissions with invalid permission for ", builder.getUniqueId().toString(), ", skipping...");
                continue;
            }

            builder.setRolePermission(islandPrivilege, playerRole);
        }
    }

    public static void deserializeUpgrades(DatabaseResult islandDatabaseResult, Island.Builder builder) {

        Optional<Map<String, Object>> upgrades = islandDatabaseResult.getMap("upgrades");
        if (upgrades.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> upgradeInfo : upgrades.get().entrySet()) {
            Upgrade upgrade = plugin.getUpgrades().getUpgrade(upgradeInfo.getKey());
            if (upgrade == null) {
                Log.warn("Cannot load upgrades with invalid upgrade names for ", upgradeInfo.getKey(), ", skipping...");
                continue;
            }

            builder.setUpgrade(upgrade, (int) upgradeInfo.getValue());
        }
    }

    public static void deserializeWarps(DatabaseResult islandDatabaseResult, Island.Builder builder) {
        Optional<Map<String, Object>> warps = islandDatabaseResult.getMap("warps");
        if (warps.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> warpMaps : warps.get().entrySet()) {
            String name = warpMaps.getKey();
            name = IslandUtils.isWarpNameLengthValid(name) ? name : name.substring(0, IslandUtils.getMaxWarpNameLength());
            if (name.isEmpty() || name.isBlank()) {
                Log.warn("Cannot load warps with invalid names for ", name, ", skipping...");
                continue;
            }
            DatabaseResult islandWarp = DatabaseResult.fromObject(warpMaps.getValue());
            Optional<Location> location = islandWarp.getString("location").map(Serializers.LOCATION_SERIALIZER::deserialize);
            if (location.isEmpty()) {
                Log.warn("Cannot load warps with invalid locations for ", name, ", skipping...");
                return;
            }
            builder.addWarp(name, islandWarp.getString("category").orElse(""),
                    location.get(), islandWarp.getBoolean("private").orElse(!plugin.getSettings().isPublicWarps()),
                    islandWarp.getString("icon").map(Serializers.ITEM_STACK_SERIALIZER::deserialize).orElse(null));
        }
    }

    public static void deserializeDirtyChunks(Island.Builder builder, Map<String, Object> dirtyChunks) {
        if (dirtyChunks.isEmpty())
            return;

        dirtyChunks.forEach((wName, value) -> {
            String worldName = ("island_" + builder.getUniqueId().toString() + "_" + wName).toLowerCase();
            if (value instanceof List<?> list) {
                for (Object o : list) {
                    String[] chunkPositionSections = ((String) o).split(",");
                    builder.setDirtyChunk(worldName, Integer.parseInt(chunkPositionSections[0]),
                            Integer.parseInt(chunkPositionSections[1]));
                }
            }
        });
    }

    public static void deserializeBlockCounts(Island.Builder builder, Map<String, Object> blockCounts) {
        if (blockCounts.isEmpty())
            return;

        blockCounts.forEach((block, count) -> {
            Key blockKey = Keys.ofMaterialAndData(block);
            BigInteger amount = BigInteger.valueOf((long) count);
            builder.setBlockCount(blockKey, amount);
        });
    }

    public static void deserializeEntityCounts(Island.Builder builder, String entities) {
        if (Text.isBlank(entities))
            return;

        JsonArray entityCounts = GSON.fromJson(entities, JsonArray.class);

        entityCounts.forEach(entityCountElement -> {
            JsonObject entityCountObject = entityCountElement.getAsJsonObject();
            Key entityKey = Keys.ofEntityType(entityCountObject.get("id").getAsString());
            BigInteger amount = new BigInteger(entityCountObject.get("amount").getAsString());
            builder.setEntityCount(entityKey, amount);
        });
    }

    public static void deserializeBlockLimits(DatabaseResult islandDatabaseResult, Island.Builder builder) {
        Optional<Map<String, Object>> blockLimits = islandDatabaseResult.getMap("block_limits");
        if (blockLimits.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> blockLimitEntry : blockLimits.get().entrySet()) {
            String block = blockLimitEntry.getKey();
            Key key = Keys.ofMaterialAndData(block);
            if (key == null) {
                Log.warn("Cannot load block limits for invalid blocks for ", block, ", skipping...");
                continue;
            }
            builder.setBlockLimit(key, (int) blockLimitEntry.getValue());

        }

    }

    public static void deserializeEntityLimits(DatabaseResult islandDatabaseResult, Island.Builder builder) {

        Optional<Map<String, Object>> entityLimits = islandDatabaseResult.getMap("entity_limits");
        if (entityLimits.isEmpty()) {
            return;
        }
        entityLimits.get().forEach((key, value) -> {
            Key key1 = Keys.ofEntityType(key);
            if (key1 != null) {
                builder.setEntityLimit(key1, (int) value);
            }
        });
    }

    public static void deserializeRatings(DatabaseResult islandDatabaseResult, Island.Builder builder) {
        Optional<Map<String, Object>> ratings = islandDatabaseResult.getMap("ratings");
        if (ratings.isEmpty()) {
            return;
        }
        ratings.get().forEach((key, value) -> {
            UUID uuid = UUID.fromString(key);
            SuperiorPlayer ratingPlayer = plugin.getPlayers().getSuperiorPlayer(uuid, false);
            if (ratingPlayer == null) {
                Log.warn("Cannot load island rating with unrecognized uuid: " + builder.getUniqueId() + ", skipping...");
                return;
            }
            Rating rating = Rating.valueOf((int) value);
            if (rating == null) {
                Log.warn("Cannot load ratings with invalid rating value for ", builder.getUniqueId(), ", skipping...");
                return;
            }
            builder.setRating(ratingPlayer, rating);
        });
    }

    public static void deserializeMissions(DatabaseResult islandDatabaseResult, Island.Builder builder) {
        Optional<Map<String, Object>> missions = islandDatabaseResult.getMap("missions");
        if (missions.isEmpty()) {
            return;
        }
        missions.get().forEach(
                (missionName, value) -> {
                    Mission<?> mission = plugin.getMissions().getMission(missionName);
                    if (mission == null) {
                        Log.warn("Cannot load island missions with invalid mission ",
                                missionName, " for ", builder.getUniqueId(), ", skipping...");
                        return;
                    }
                    builder.setCompletedMission(mission, (int) value);
                }
        );
    }

    public static void deserializeIslandFlags(DatabaseResult islandDatabaseResult, Island.Builder builder) {
        Optional<Map<String, Object>> islandFlags = islandDatabaseResult.getMap("flags");
        if (islandFlags.isEmpty()) {
            return;
        }
        islandFlags.get().forEach((name, status) -> {
            IslandFlag islandFlag = IslandFlag.getByName(name);
            if (islandFlag == null) {
                Log.warn("Cannot load island flags with invalid flags for ", builder.getUniqueId(), ", skipping...");
                return;
            }
            builder.setIslandFlag(islandFlag, (boolean) status);
        });
    }

    public static void deserializeGenerators(DatabaseResult islandDatabaseResult, Island.Builder builder) {
        Optional<Map<String, Object>> generators = islandDatabaseResult.getMap("generators");
        if (generators.isEmpty()) {
            return;
        }
        generators.get().forEach((environment, value) -> {
            Optional<Map<String, Object>> map = DatabaseResult.toMap(value);
            if (map.isEmpty()) {
                return;
            }
            map.get().forEach((block, rate) -> {
                Key key = Keys.ofMaterialAndData(block);
                if (key == null) {
                    Log.warn("Cannot load generator rates with invalid block for ", builder.getUniqueId(), ", skipping...");
                    return;
                }
                World.Environment anEnum = EnumHelper.getEnum(World.Environment.class, environment);
                if (anEnum == null) {
                    Log.warn("Cannot load generator rates with invalid environment for ", builder.getUniqueId(), ", skipping...");
                    return;
                }
                builder.setGeneratorRate(key, (int) rate, World.Environment.values()[anEnum.ordinal()]);
            });
        });
    }

    public static void deserializeIslandHomes(DatabaseResult islandDatabaseResult, Island.Builder builder) {
        Optional<Map<String, Object>> islandHomes = islandDatabaseResult.getMap("homes");
        if (islandHomes.isEmpty()) {
            return;
        }
        islandHomes.get().forEach((environment, location) -> {
            World.Environment anEnum = EnumHelper.getEnum(World.Environment.class, environment);
            if (anEnum == null) {
                Log.warn("Cannot load island homes with invalid environment for ", builder.getUniqueId(), ", skipping...");
                return;
            }
            Location loc = Serializers.LOCATION_SERIALIZER.deserialize((String) location);
            if (loc == null) {
                Log.warn("Cannot load island homes with invalid location for ", builder.getUniqueId(), ", skipping...");
                return;
            }
            builder.setIslandHome(loc, World.Environment.values()[anEnum.ordinal()]);
        });

    }

    public static void deserializeVisitorHomes(DatabaseResult islandDatabaseResult, Island.Builder builder) {
        Optional<Map<String, Object>> visitorHomes = islandDatabaseResult.getMap("visitor_homes");
        if (visitorHomes.isEmpty()) {
            return;
        }
        visitorHomes.get().forEach((environment, location) -> {
            World.Environment anEnum = EnumHelper.getEnum(World.Environment.class, environment);
            if (anEnum == null) {
                Log.warn("Cannot load island homes with invalid environment for ", builder.getUniqueId(), ", skipping...");
                return;
            }
            Location loc = Serializers.LOCATION_SERIALIZER.deserialize((String) location);
            if (loc == null) {
                Log.warn("Cannot load island homes with invalid location for ", builder.getUniqueId(), ", skipping...");
                return;
            }
            builder.setVisitorHome(loc, World.Environment.values()[anEnum.ordinal()]);

        });
    }

    public static void deserializeEffects(DatabaseResult islandDatabaseResult, Island.Builder builder) {
        Optional<Map<String, Object>> effects = islandDatabaseResult.getMap("effects");
        if (effects.isEmpty()) {
            return;
        }
        effects.get().forEach((name, level) -> {
            PotionEffectType effectType = PotionEffectType.getByName(name);
            if (effectType == null) {
                Log.warn("Cannot load island effects with invalid effect for ", builder.getUniqueId(), ", skipping...");
                return;
            }
            builder.setIslandEffect(effectType, (int) level);

        });
    }

    public static void deserializeIslandChest(DatabaseResult islandDatabaseResult, Island.Builder builder) {
        Optional<Map<String, Object>> islandChest = islandDatabaseResult.getMap("chest");
        if (islandChest.isEmpty()) {
            return;
        }
        islandChest.get().forEach((index, content) -> {
            int i = Integer.parseInt(index);
            Optional<ItemStack[]> contents = DatabaseResult.getBlob(content).map(Serializers.INVENTORY_SERIALIZER::deserialize);
            if (contents.isEmpty()) {
                Log.warn("Cannot load island chest with invalid contents for ", builder.getUniqueId(), ", skipping...");
                return;
            }
            int contentsLength = contents.get().length;
            ItemStack[] chestContents;

            if (contentsLength % 9 != 0) {
                int amountOfRows = Math.min(1, Math.max(6, (contentsLength / 9) + 1));
                chestContents = new ItemStack[amountOfRows * 9];
                int amountOfContentsToCopy = Math.min(contentsLength, chestContents.length);
                System.arraycopy(contents.get(), 0, chestContents, 0, amountOfContentsToCopy);
            } else if (contentsLength > 54) {
                chestContents = new ItemStack[54];
                System.arraycopy(contents.get(), 0, chestContents, 0, 54);
            } else if (contentsLength < 9) {
                chestContents = new ItemStack[9];
                System.arraycopy(contents.get(), 0, chestContents, 0, contentsLength);
            } else {
                chestContents = contents.get();
            }
            builder.setIslandChest(i, chestContents);
        });
    }

    public static void deserializeRoleLimits(DatabaseResult islandDatabaseResult, Island.Builder builder) {
        Optional<Map<String, Object>> roleLimits = islandDatabaseResult.getMap("role_limits");
        if (roleLimits.isEmpty()) {
            return;
        }
        roleLimits.get().forEach((name, limit) -> {
            PlayerRole playerRole = SPlayerRole.fromId(Integer.parseInt(name));
            if (playerRole == null) {
                Log.warn("Cannot load role limit for invalid role on ", builder.getUniqueId(), ", skipping...");
                return;
            }
            builder.setRoleLimit(playerRole, (int) limit);
        });
    }

    public static void deserializeWarpCategories(DatabaseResult islandDatabaseResult, Island.Builder builder) {
        Optional<Map<String, Object>> islandDatabaseResultMap = islandDatabaseResult.getMap("warp_categories");
        if (islandDatabaseResultMap.isEmpty()) {
            return;
        }
        islandDatabaseResultMap.get().forEach((name, info) -> {
            DatabaseResult warpCategory = DatabaseResult.fromObject(info);
            builder.addWarpCategory(name, warpCategory.getInt("slot").orElse(-1),
                    warpCategory.getString("icon").map(Serializers.ITEM_STACK_SERIALIZER::deserialize).orElse(null));
        });
    }

    public static void deserializeIslandBank(DatabaseResult islandDatabaseResult, Island.Builder builder) {
        Optional<Map<String, Object>> bank = islandDatabaseResult.getMap("bank");
        if (bank.isEmpty()) {
            return;
        }
        DatabaseResult islandBank = DatabaseResult.fromObject(bank.get());
        Optional<BigDecimal> balance = islandBank.getBigDecimal("balance");
        if (!balance.isPresent()) {
            Log.warn("Cannot load island banks with invalid balance for ", builder.getUniqueId(), ", skipping...");
            return;
        }
        long currentTime = System.currentTimeMillis() / 1000;
        builder.setBalance(balance.get());
        long lastInterestTime = islandBank.getLong("last_interest_time").orElse(currentTime);
        builder.setLastInterestTime(lastInterestTime > currentTime ? lastInterestTime / 1000 : lastInterestTime);
    }

    public static void deserializeIslandSettings(DatabaseResult islandDatabaseResult, Island.Builder builder) {
        Optional<Map<String, Object>> settings = islandDatabaseResult.getMap("settings");
        if (settings.isEmpty()) {
            return;
        }
        DatabaseResult islandSettings = DatabaseResult.fromObject(settings.get());

        builder.setIslandSize(islandSettings.getInt("size").orElse(-1));
        builder.setTeamLimit(islandSettings.getInt("members_limit").orElse(-1));
        builder.setWarpsLimit(islandSettings.getInt("warps_limit").orElse(-1));
        builder.setCropGrowth(islandSettings.getDouble("crop_growth_multiplier").orElse(-1D));
        builder.setSpawnerRates(islandSettings.getDouble("spawner_rates_multiplier").orElse(-1D));
        builder.setMobDrops(islandSettings.getDouble("mob_drops_multiplier").orElse(-1D));
        builder.setCoopLimit(islandSettings.getInt("coops_limit").orElse(-1));
        builder.setBankLimit(islandSettings.getBigDecimal("bank_limit").orElse(SYNCED_BANK_LIMIT_VALUE));
    }

    public static void deserializeBankTransactions(DatabaseResult islandDatabaseResult, Island.Builder builder) {
        if (BuiltinModules.BANK.bankLogs && BuiltinModules.BANK.cacheAllLogs) {
            //TODO
            //Its not a good way use mongoDB to log something.
//
//            Optional<Map<String, Object>> bankTransactions = islandDatabaseResult.getMap("bank_transactions");
//            if (bankTransactions.isEmpty()) {
//                return;
//            }
//
//            databaseBridge.loadAllObjects("bank_transactions", bankTransactionRow -> {
//                com.bgsoftware.superiorskyblock.core.database.DatabaseResult bankTransaction = new com.bgsoftware.superiorskyblock.core.database.DatabaseResult(bankTransactionRow);
//
//                Optional<UUID> uuid = bankTransaction.getUUID("island");
//                if (!uuid.isPresent()) {
//                    Log.warn("Cannot load bank transaction for null islands, skipping...");
//                    return;
//                }
//
//                Island.Builder builder = databaseCache.computeIfAbsentInfo(uuid.get(), IslandBuilderImpl::new);
//                SBankTransaction.fromDatabase(bankTransaction).ifPresent(builder::addBankTransaction);
//            });
        }
    }

    public static void deserializePersistentDataContainer(DatabaseResult islandDatabaseResult, Island.Builder builder) {
        byte[] blob = islandDatabaseResult.getBlob("custom_data").orElse(new byte[0]);
        if (blob.length == 0)
            return;

        builder.setPersistentData(blob);
    }
}
