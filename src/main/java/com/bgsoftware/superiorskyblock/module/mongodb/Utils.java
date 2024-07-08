package com.bgsoftware.superiorskyblock.module.mongodb;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.core.logging.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Utils {
    private static String serverName;
    private static String serverType;

    public static boolean isSelfServer(String s) {
        return serverName.equals(s);
    }

    public static String getSelfServerName() {
        return serverName;
    }


    public static void initServerInfo(SuperiorSkyblock plugin) {

        try {
            File file = new File("server.properties");
            BufferedReader read = new BufferedReader(new FileReader(file));
            String line;
            while ((line = read.readLine()) != null) {
                if (line.startsWith("server-type")) {
                    serverType = line.replace("server-type=", "").replace(" ", "");
                }
                if (line.startsWith("server-name")) {
                    serverName = line.replace("server-name=", "").replace(" ", "");
                }
            }

            if (serverType == null || serverType.isEmpty() || serverName == null || serverName.isEmpty()) {
                Log.info("=================================================================");
                Log.info("server-name 或 server-type 未设置, 请在server.properties 中设置");
                Log.info("你需要在 server.properties 中设置 server-name=? 和 server-type=?");
                Log.info("为避免意外，服务器已强制关闭，请配置完成后再启动！");
                Log.info("=================================================================");
                plugin.getServer().shutdown();
            } else {
                Log.warn("=================================================================");
                Log.warn("加载完成！祝您游戏愉快！");
                Log.warn("Server Type -> " + serverType);
                Log.warn("Server Name -> " + serverName);
                Log.warn("=================================================================");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
