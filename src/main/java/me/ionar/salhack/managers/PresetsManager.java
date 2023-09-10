package me.ionar.salhack.managers;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import me.ionar.salhack.gui.click.component.menus.mods.MenuComponentPresetsList;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.preset.Preset;

public class PresetsManager {
    private final List<Preset> Presets = new CopyOnWriteArrayList<>();
    private MenuComponentPresetsList PresetList;

    public void LoadPresets() {
        File[] directories = SalHack.GetFilesManager().Presets.listFiles(File::isDirectory);
        if (directories != null) {
            for (File file : directories) {
                System.out.println(file.getName());
                Preset preset = new Preset(file.getName());
                preset.load(file);
                Presets.add(preset);
            }
        }
        Preset defaultPreset = null;
        boolean alreadyEnabled = false;
        for (Preset preset : Presets) {
            if (preset.getName().equalsIgnoreCase("default")) defaultPreset = preset;
            else if (preset.isActive()) {
                alreadyEnabled = true;
                break;
            }
        }
        if (!alreadyEnabled && defaultPreset != null) defaultPreset.setActive(true);
    }

    public void CreatePreset(String presetName) {
        SalHack.GetFilesManager().createDirectory("SalHack/Presets/"+presetName);
        SalHack.GetFilesManager().createDirectory("SalHack/Presets/"+presetName+"/Modules");
        Preset preset = new Preset(presetName);
        Presets.add(preset);
        preset.initNewPreset();
        preset.save();
        SetPresetActive(preset);
        if (PresetList != null) PresetList.AddPreset(preset);
    }

    public void RemovePreset(String presetName) {
        Preset toRemove = null;
        for (Preset preset : Presets) {
            if (preset.getName().equalsIgnoreCase(presetName)) {
                toRemove = preset;
                break;
            }
        }
        if (toRemove != null) {
            SalHack.GetFilesManager().deleteDirectory("/SalHack/Presets/" + toRemove.getName());
            Presets.remove(toRemove);
            if (PresetList != null) PresetList.RemovePreset(toRemove);
        }
    }

    public Preset getActivePreset() {
        for (Preset preset : Presets) {
            if (preset.isActive()) return preset;
        }
        // default MUST always be available
        return Presets.get(0);
    }

    public void SetPresetActive(Preset preset) {
        for (Preset preset2 : Presets) preset2.setActive(false);
        preset.setActive(true);
        ModuleManager.Modules.forEach(preset::initValuesForMod);
    }

    public final List<Preset> GetItems() {
        return Presets;
    }

    public static PresetsManager Get() {
        return SalHack.GetPresetsManager();
    }

    public void InitializeGUIComponent(MenuComponentPresetsList presetList) {
        PresetList = presetList;
    }
}
