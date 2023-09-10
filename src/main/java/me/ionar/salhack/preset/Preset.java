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
    private String DisplayName;
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, String>> moduleValues = new ConcurrentHashMap<>();
    private boolean Active;

    public Preset(String displayName) {
        DisplayName = displayName;
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
        module.getValueList().forEach(val -> {if (val.getValue() != null) valsMap.put(val.getName(), val.getValue().toString());});
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
                DisplayName = val;
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
        map.put("displayName", DisplayName);
        SalHack.getFilesManager().write("SalHack/Presets/" + DisplayName + "/" + DisplayName + ".json", SalHack.gson.toJson(map, Map.class));
        for (Entry<String, ConcurrentHashMap<String, String>> entry : moduleValues.entrySet()) {
            map = new HashMap<>();
            for (Entry<String, String> value : entry.getValue().entrySet()) {
                String key = value.getKey();
                String val = value.getValue();
                map.put(key, val);
            }
            SalHack.getFilesManager().write("SalHack/Presets/"+DisplayName+"/Modules/"+entry.getKey()+".json", SalHack.gson.toJson(map, Map.class));
        }
    }

    public String getName() {
        return DisplayName;
    }

    public boolean isActive() {
        return Active;
    }

    public void setActive(boolean b) {
        Active = b;
    }

    public void initValuesForModule(Module module) {
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
                    module.DisplayName = Value;
                    continue;
                }

                if (Key.equalsIgnoreCase("keybind")) {
                    module.Key = Integer.parseInt(Value);
                    continue;
                }

                if (Key.equalsIgnoreCase("hidden")) {
                    module.Hidden = Value.equalsIgnoreCase("true");
                    continue;
                }

                for (Value valueObj : module.ValueList) {
                    if (valueObj.getName().equalsIgnoreCase(value.getKey())) {
                        if (valueObj.getValue() instanceof Number && !(valueObj.getValue() instanceof Enum)) {
                            if (valueObj.getValue() instanceof Integer) valueObj.SetForcedValue(Integer.parseInt(Value));
                            else if (valueObj.getValue() instanceof Float) valueObj.SetForcedValue(Float.parseFloat(Value));
                            else if (valueObj.getValue() instanceof Double) valueObj.SetForcedValue(Double.parseDouble(Value));
                        } else if (valueObj.getValue() instanceof Boolean) {
                            valueObj.SetForcedValue(Value.equalsIgnoreCase("true"));
                        } else if (valueObj.getValue() instanceof Enum) {
                            Enum e = valueObj.GetEnumReal(Value);
                            if (e != null) valueObj.SetForcedValue(e);
                        } else if (valueObj.getValue() instanceof String) valueObj.SetForcedValue(Value);
                        break;
                    }
                }
            }
        }
    }
}
