package me.ionar.salhack.preset;


import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;

@SuppressWarnings({"CallToPrintStackTrace", "rawtypes", "unchecked"})
public class Preset {
    private String DisplayName;
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, String>> ValueListMods = new ConcurrentHashMap<>();
    private boolean Active;

    public Preset(String displayName) {
        DisplayName = displayName;
    }

    public void initNewPreset() {
        ModuleManager.Get().GetModuleList().forEach(this::addModuleSettings);
    }

    public void addModuleSettings(final Module module) {
        ConcurrentHashMap<String, String> valsMap = new ConcurrentHashMap<>();
        valsMap.put("enabled", module.isEnabled() ? "true" : "false");
        valsMap.put("display", module.getDisplayName());
        valsMap.put("keybind", String.valueOf(module.getKey()));
        valsMap.put("hidden",  module.isHidden() ? "true" : "false");
        module.getValueList().forEach(val -> {if (val.getValue() != null) valsMap.put(val.getName(), val.getValue().toString());});
        ValueListMods.put(module.getDisplayName(), valsMap);
        save();
    }

    // this will load the settings for presets, and modules settings
    public void load(File directory) {
        File exists = new File("SalHack/Presets/" + directory.getName() + "/" + directory.getName() + ".json");
        if (!exists.exists()) return;

        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("SalHack/Presets/" + directory.getName() + "/" + directory.getName() + ".json"));
            Map<?, ?> map = gson.fromJson(reader, Map.class);
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String key = (String) entry.getKey();
                String val = (String) entry.getValue();
                if (key.equals("displayName")) {
                    DisplayName = val;
                }
            }
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try (Stream<Path> paths = Files.walk(Paths.get("SalHack/Presets/" + directory.getName() + "/Modules/"))) {
            paths.filter(Files::isRegularFile).forEach(path -> {
                try {
                    Gson gson = new Gson();
                    Reader reader = Files.newBufferedReader(Paths.get("SalHack/Presets/" + directory.getName() + "/Modules/" + path.getFileName().toString()));
                    Map<?, ?> map = gson.fromJson(reader, Map.class);
                    ConcurrentHashMap<String, String> valsMap = new ConcurrentHashMap<>();
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        String key = (String) entry.getKey();
                        String val = (String) entry.getValue();
                        valsMap.put(key, val);
                    }
                    ValueListMods.put(path.getFileName().toString().substring(0, path.getFileName().toString().indexOf(".json")), valsMap);
                    reader.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.setPrettyPrinting().create();
            Writer writer = Files.newBufferedWriter(Paths.get("SalHack/Presets/" + DisplayName + "/" + DisplayName + ".json"));
            Map<String, String> map = new HashMap<>();
            map.put("displayName", DisplayName);
            gson.toJson(map, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            for (Entry<String, ConcurrentHashMap<String, String>> entry : ValueListMods.entrySet()) {
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.setPrettyPrinting().create();
                Writer writer = Files.newBufferedWriter(Paths.get("SalHack/Presets/" + DisplayName + "/Modules/" + entry.getKey() + ".json"));
                Map<String, String> map = new HashMap<>();
                for (Entry<String, String> value : entry.getValue().entrySet()) {
                    String key = value.getKey();
                    String val = value.getValue();
                    map.put(key, val);
                }
                gson.toJson(map, writer);
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    public void initValuesForMod(Module module) {
        if (ValueListMods.containsKey(module.getDisplayName())) {
            for (Entry<String, String> value : ValueListMods.get(module.getDisplayName()).entrySet()) {
                String Key = value.getKey();
                String Value = value.getValue();

                if (Key.equalsIgnoreCase("enabled")) {
                    if (Value.equalsIgnoreCase("true")) {
                        if (!module.isEnabled()) module.toggleNoSave();
                    } else if (module.isEnabled()) module.toggle();
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

                for (Value valueObj : module.valueList) {
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
