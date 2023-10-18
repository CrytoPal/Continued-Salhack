package me.ionar.salhack.managers;

import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Module.ModuleType;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.module.combat.*;
import me.ionar.salhack.module.exploit.Bowbomb;
import me.ionar.salhack.module.misc.*;
import me.ionar.salhack.module.movement.*;
import me.ionar.salhack.module.world.Timer;
import me.ionar.salhack.util.*;
import me.ionar.salhack.module.render.Fullbright;
import me.ionar.salhack.module.render.NametagsModule;
import me.ionar.salhack.module.ui.*;
import me.ionar.salhack.module.world.AutoTool;
import me.ionar.salhack.module.world.CoordsSpoofer;
import me.ionar.salhack.preset.Preset;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static me.ionar.salhack.main.Wrapper.mc;

public class ModuleManager {
    public static ModuleManager Get() {
        return SalHack.GetModuleManager();
    }

    public ModuleManager() {
    }

    public static ArrayList<Module> Mods = new ArrayList<>();
    private ArrayList<Module> ArrayListAnimations = new ArrayList<>();

    public void Init() {
        /// Combat
        Add(new KillAura());
        Add(new Offhand());
        Add(new AutoCrystalRewrite());
        Add(new Surround());
        Add(new AutoTrap());
        Add(new AntiCityBoss());
        Add(new AutoArmor());
        Add(new BowSpam());
        Add(new AutoCity());
        Add(new AutoExp());
        Add(new Velocity());
        Add(new MiddleClickPearl());

        /// Exploit
        Add(new Bowbomb());

        /// Misc
        Add(new FakePlayer());
        Add(new Friends());
        Add(new MiddleClickFriends());
        Add(new Rotation());
        Add(new AntiAFK());
        Add(new AutoBonemeal());
        Add(new AutoDye());
        Add(new AutoEat());
        Add(new AutoFarmland());
        Add(new AutoMount());
        Add(new AutoShear());
        Add(new AutoShovelPath());
        Add(new RetardChat());

        /// Movement
        Add(new ElytraFly());
        Add(new Flight());
        Add(new Speed());
        Add(new Sprint());
        Add(new Sneak());
        Add(new Jesus());

        /// Render
        Add(new NametagsModule());
        Add(new Fullbright());

        /// UI
        Add(new ColorsModule());
        Add(new ClickGuiModule());
        Add(new HudEditorModule());
        Add(new HudModule());
        Add(new Notification());

        /// World
        Add(new Timer());
        Add(new CoordsSpoofer());
        Add(new AutoTool());

        /// Schematica


        LoadExternalModules();

        Mods.sort((p_Mod1, p_Mod2) -> p_Mod1.getDisplayName().compareTo(p_Mod2.getDisplayName()));

        final Preset preset = PresetsManager.Get().getActivePreset();

        Mods.forEach(mod -> {
            preset.initValuesForMod(mod);
        });

        Mods.forEach(mod -> {
            mod.init();
        });
    }


    public void Add(Module mod) {
        try {
            for (Field field : mod.getClass().getDeclaredFields()) {
                if (Value.class.isAssignableFrom(field.getType())) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    final Value val = (Value) field.get(mod);
                    val.InitializeModule(mod);
                    mod.getValueList().add(val);
                }
            }
            Mods.add(mod);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final List<Module> GetModuleList(ModuleType p_Type) {
        List<Module> list = new ArrayList<>();
        for (Module module : Mods) {
            if (module.getModuleType().equals(p_Type)) {
                list.add(module);
            }
        }
        // Organize alphabetically
        list.sort(Comparator.comparing(Module::getDisplayName));

        return list;
    }

    public final List<Module> GetModuleList() {
        return Mods;
    }

    public static void OnKeyPress(int key) {
        if (key == 0) return;

        Mods.forEach(p_Mod -> {
            if (p_Mod.isKeyPressed(key)) {
                p_Mod.toggle(true);
            }
        });
    }

    public Module GetMod(Class p_Class) {
        for (Module l_Mod : Mods) {
            if (l_Mod.getClass() == p_Class)
                return l_Mod;
        }

        SalHackMod.log.error("Could not find the class " + p_Class.getName() + " in Mods list");
        return null;
    }

    public Module GetModLike(String p_String) {
        for (Module l_Mod : Mods) {
            if (l_Mod.GetArrayListDisplayName().toLowerCase().startsWith(p_String.toLowerCase()))
                return l_Mod;
        }

        return null;
    }

    public void OnModEnable(Module p_Mod) {
        ArrayListAnimations.remove(p_Mod);
        ArrayListAnimations.add(p_Mod);

        final Comparator<Module> comparator = (first, second) -> {
            if (mc.textRenderer == null) {
            } else {
                final String firstName = first.GetFullArrayListDisplayName();
                final String secondName = second.GetFullArrayListDisplayName();
                final float dif = mc.textRenderer.getWidth(secondName) - mc.textRenderer.getWidth(firstName);
                return dif != 0 ? (int) dif : secondName.compareTo(firstName);
            }
            return 0;
        };

        ArrayListAnimations = (ArrayList<Module>) ArrayListAnimations.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public void Update() {
        if (ArrayListAnimations.isEmpty())
            return;

        Module l_Mod = ArrayListAnimations.get(0);

        if ((l_Mod.RemainingXAnimation -= (mc.textRenderer.getWidth(l_Mod.GetFullArrayListDisplayName()) / 10)) <= 0) {
            ArrayListAnimations.remove(l_Mod);
            l_Mod.RemainingXAnimation = 0;
        }
    }

    public void LoadExternalModules() {
        try {
            final File dir = new File("SalHack/CustomMods");

            for (Class newClass : ReflectionUtil.getClassesEx(dir.getPath())) {
                if (newClass == null)
                    continue;

                // if we have found a class and the class inherits "Module"
                if (Module.class.isAssignableFrom(newClass)) {
                    //create a new instance of the class
                    final Module module = (Module) newClass.newInstance();

                    if (module != null) {
                        // initialize the modules
                        Add(module);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
