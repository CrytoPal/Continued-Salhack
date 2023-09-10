package me.ionar.salhack.managers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.io.FileUtils;

import me.ionar.salhack.gui.click.component.menus.mods.MenuComponentPresetsList;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.preset.Preset;
// DO NOT TOUCH THESE THEY MAY BREAK OPENING THE GUI
public class PresetsManager {
    private List<Preset> _presets = new CopyOnWriteArrayList<>();
    private MenuComponentPresetsList _presetList;

    public void init() {
        File[] directories = new File(SalHack.getFilesManager().getCurrentDirectory() + "/SalHack/Presets/").listFiles(File::isDirectory);

        for (File file : directories) {
            System.out.println("" + file.getName().toString());
            Preset preset = new Preset(file.getName().toString());
            preset.load(file);
            _presets.add(preset);
        }

        Preset defaultPreset = null;
        boolean alreadyEnabled = false;

        for (Preset p : _presets) {
            if (p.getName().equalsIgnoreCase("default"))
                defaultPreset = p;
            else if (p.isActive()) {
                alreadyEnabled = true;
                break;
            }
        }

        if (!alreadyEnabled && defaultPreset != null) {
            defaultPreset.setActive(true);
        }
    }

    public void createPreset(String presetName) {
        try {
            new File(SalHack.getFilesManager().getCurrentDirectory() + "/SalHack/Presets/" + presetName).mkdirs();
            new File(SalHack.getFilesManager().getCurrentDirectory() + "/SalHack/Presets/" + presetName + "/Modules").mkdirs();
            Preset preset = new Preset(presetName);
            _presets.add(preset);
            preset.initNewPreset();
            preset.save();
            setPresetActive(preset);

            if (_presetList != null) {
                _presetList.AddPreset(preset);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removePreset(String presetName) {
        Preset toRemove = null;

        for (Preset p : _presets) {
            if (p.getName().equalsIgnoreCase(presetName)) {
                toRemove = p;
                break;
            }
        }

        if (toRemove != null) {
            try {
                FileUtils.deleteDirectory(new File(SalHack.getFilesManager().getCurrentDirectory() + "/SalHack/Presets/" + toRemove.getName()));
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            _presets.remove(toRemove);
            if (_presetList != null) {
                _presetList.RemovePreset(toRemove);
            }
        }
    }

    public Preset getActivePreset() {
        for (Preset p : _presets) {
            if (p.isActive())
                return p;
        }

        // default MUST always be available
        return _presets.get(0);
    }

    public void setPresetActive(Preset preset) {
        for (Preset p : _presets) {
            p.setActive(false);
        }

        preset.setActive(true);

        ModuleManager.modules.forEach(preset::init);
    }

    public final List<Preset> getItems() {
        return _presets;
    }


    public void initializeGUIComponent(MenuComponentPresetsList presetList) {
        _presetList = presetList;
    }
}
