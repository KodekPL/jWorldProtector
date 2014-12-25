package jcraft.wp.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import org.bukkit.configuration.file.YamlConfiguration;

public abstract class YamlHandler {

    private final File file;

    public YamlHandler(File file) {
        this.file = file;
    }

    public File getFile() {
        return this.file;
    }

    public void save() {
        final YamlConfiguration yaml = new YamlConfiguration();

        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            SaveField saveField = (SaveField) field.getAnnotation(SaveField.class);

            if (saveField == null) {
                continue;
            }

            try {
                yaml.set(((saveField.section().length() == 0) ? "" : saveField.section() + '.') + field.getName(), field.get(this));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }
        }

        try {
            yaml.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        final YamlConfiguration yaml = YamlConfiguration.loadConfiguration(this.file);

        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            SaveField saveField = (SaveField) field.getAnnotation(SaveField.class);

            if (saveField == null) {
                continue;
            }

            if (!yaml.contains(((saveField.section().length() == 0) ? "" : saveField.section() + '.') + field.getName())) {
                continue;
            }

            final Object object = yaml.get(((saveField.section().length() == 0) ? "" : saveField.section() + '.') + field.getName());

            try {
                field.set(this, object);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }
        }
    }

}
