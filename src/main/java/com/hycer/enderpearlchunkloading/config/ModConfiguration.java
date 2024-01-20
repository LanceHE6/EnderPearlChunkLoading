package com.hycer.enderpearlchunkloading.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfiguration {

    private boolean status;
    private int loadTimeThreshold;

    private static final String CONFIG_DIR = "config/";
    private static final String CONFIG_FILE_PATH = CONFIG_DIR + "EnderPearlChunkLoading.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ModConfiguration() {
        checkAndLoadConfig();
    }

    public void checkAndLoadConfig() {
        File configFile = new File(CONFIG_FILE_PATH);
        if (!configFile.exists()) {
            createDefaultConfig(configFile);
        } else {
            loadConfig(configFile);
        }
    }

    private void createDefaultConfig(File configFile) {
        JsonObject configObject = new JsonObject();
        configObject.addProperty("status", false);
        configObject.addProperty("loadTimeThreshold", 10);
        status = false;
        loadTimeThreshold = 10;

        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(configObject, writer);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadConfig(File configFile) {
        try (FileReader reader = new FileReader(configFile)) {
            JsonObject configObject = JsonParser.parseReader(reader).getAsJsonObject();

            status = configObject.get("status").getAsBoolean();
            loadTimeThreshold = configObject.get("loadTimeThreshold").getAsInt();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 保存配置进json文件中
     */
    private void saveConfig() {
        JsonObject configObject = new JsonObject();
        configObject.addProperty("status", status);
        configObject.addProperty("loadTimeThreshold", loadTimeThreshold);

        File configFile = new File(CONFIG_FILE_PATH);
        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(configObject, writer);
            writer.flush();
            System.out.println("[EPCL]配置已更新");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * setter
     */
    public void setStatus(boolean status) {
        this.status = status;
        saveConfig();
    }

    public void setLoadTimeThreshold(int loadTimeThreshold) {
        this.loadTimeThreshold = loadTimeThreshold;
        saveConfig();
    }
    /**
     * getter
     */
    public boolean getStatus() {
        return status;
    }

    public int getLoadTimeThreshold() {
        return loadTimeThreshold;
    }
}
