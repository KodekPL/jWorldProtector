package jcraft.wp.util;

import java.io.File;

import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSyntaxException;

public class GsonUtils {

    private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().disableHtmlEscaping().setPrettyPrinting().create();

    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

    public static <T> T fromJson(String data, Class<T> clazz) {
        try {
            return GSON.fromJson(data, clazz);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveToJson(File file, Object object) {
        FileUtils.saveFile(file, toJson(object));
    }

    public static <T> T loadFromJson(File file, Class<T> clazz) {
        return fromJson(FileUtils.loadFile(file), clazz);
    }

}
