package me.ionar.salhack.preset;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;

@SuppressWarnings({"rawtypes", "unchecked"})
public class Preset {
    private String displayName;
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, String>> moduleValues = new ConcurrentHashMap<>();
    private boolean active;

    public Preset(String displayName) {
        this.displayName = displayName;
    }

    public void initNewPreset() {
        SalHack.getModuleManager().getModuleList().forEach(this::addModuleSettings);
    }

    public void addModuleSettings(final Module module) {
        ConcurrentHashMap<String, String> valsMap = new ConcurrentHashMap<>();
        valsMap.put("enabled", module.isEnabled() ? "true" : "false");
        valsMap.put("display", module.getDisplayName());
        valsMap.put("keybind", String.valueOf(module.getKey()));
        valsMap.put("hidden",  module.isHidden() ? "true" : "false");
        module.getValues().forEach(val -> {if (val.getValue() != null) valsMap.put(val.getName(), val.getValue().toString());});
        moduleValues.put(module.getDisplayName(), valsMap);
        save();
    }

    // this will load the settings for presets, and modules settings
    public void load(File directory) {
        File exists = new File("SalHack/Presets/" + directory.getName() + "/" + directory.getName() + ".json");
        if (!exists.exists()) return;
        String content = SalHack.getFilesManager().read(exists.getPath());
        Map<?, ?> map = SalHack.gson.fromJson(content, Map.class);
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = (String) entry.getKey();
            String val = (String) entry.getValue();
            if (key.equals("displayName")) {
                displayName = val;
            }
        }

        try (Stream<Path> paths = Files.walk(Paths.get("SalHack/Presets/" + directory.getName() + "/Modules/"))) {
            paths.filter(Files::isRegularFile).forEach(path -> {
                String content2 = SalHack.getFilesManager().read("SalHack/Presets/"+directory.getName()+"/Modules/"+path.getFileName().toString());
                Map<?, ?> map2 = SalHack.gson.fromJson(content2, Map.class);
                ConcurrentHashMap<String, String> valsMap = new ConcurrentHashMap<>();
                for (Map.Entry<?, ?> entry : map2.entrySet()) {
                    String key = (String) entry.getKey();
                    String val = (String) entry.getValue();
                    valsMap.put(key, val);
                }
                moduleValues.put(path.getFileName().toString().substring(0, path.getFileName().toString().indexOf(".json")), valsMap);
            });
        } catch (IOException ignored) {}
    }

    public void save() {
        Map<String, String> map = new HashMap<>();
        map.put("displayName", displayName);
        SalHack.getFilesManager().write("SalHack/Presets/" + displayName + "/" + displayName + ".json", SalHack.gson.toJson(map, Map.class));
        for (Entry<String, ConcurrentHashMap<String, String>> entry : moduleValues.entrySet()) {
            map = new HashMap<>();
            for (Entry<String, String> value : entry.getValue().entrySet()) {
                String key = value.getKey();
                String val = value.getValue();
                map.put(key, val);
            }
            SalHack.getFilesManager().write("SalHack/Presets/"+ displayName +"/Modules/"+entry.getKey()+".json", SalHack.gson.toJson(map, Map.class));
        }
    }

    public String getName() {
        return displayName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean b) {
        active = b;
    }

    public void init(Module module) {
        if (moduleValues.containsKey(module.getDisplayName())) {
            for (Entry<String, String> value : moduleValues.get(module.getDisplayName()).entrySet()) {
                String Key = value.getKey();
                String Value = value.getValue();

                if (Key.equalsIgnoreCase("enabled")) {
                    if (Value.equalsIgnoreCase("true") && !module.isEnabled()) module.toggle(false);
                    else if (module.isEnabled()) module.toggle(true);
                    continue;
                }

                if (Key.equalsIgnoreCase("display")) {
                    module.displayName = Value;
                    continue;
                }

                if (Key.equalsIgnoreCase("keybind")) {
                    module.key = Integer.parseInt(Value);
                    continue;
                }

                if (Key.equalsIgnoreCase("hidden")) {
                    module.hidden = Value.equalsIgnoreCase("true");
                    continue;
                }

                for (Value valueObj : module.values) {
                    if (valueObj.getName().equalsIgnoreCase(value.getKey())) {
                        if (valueObj.getValue() instanceof Number && !(valueObj.getValue() instanceof Enum)) {
                            if (valueObj.getValue() instanceof Integer) valueObj.setForcedValue(Integer.parseInt(Value));
                            else if (valueObj.getValue() instanceof Float) valueObj.setForcedValue(Float.parseFloat(Value));
                            else if (valueObj.getValue() instanceof Double) valueObj.setForcedValue(Double.parseDouble(Value));
                        } else if (valueObj.getValue() instanceof Boolean) {
                            valueObj.setForcedValue(Value.equalsIgnoreCase("true"));
                        } else if (valueObj.getValue() instanceof Enum) {
                            Enum e = valueObj.getEnumReal(Value);
                            if (e != null) valueObj.setForcedValue(e);
                        } else if (valueObj.getValue() instanceof String) valueObj.setForcedValue(Value);
                        break;
                    }
                }
            }
        }
    }
}
