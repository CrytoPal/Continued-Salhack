package me.ionar.salhack.managers;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Module.ModuleType;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.module.combat.OffhandModule;
import me.ionar.salhack.module.combat.KillAuraModule;
import me.ionar.salhack.module.exploit.BowbombModule;
import me.ionar.salhack.module.misc.FakePlayer;
import me.ionar.salhack.module.misc.FriendsModule;
import me.ionar.salhack.module.misc.MiddleClickFriendsModule;
import me.ionar.salhack.module.movement.ElytraFlyModule;
import me.ionar.salhack.module.movement.Rotation;
import me.ionar.salhack.module.movement.SpeedModule;
import me.ionar.salhack.module.movement.Sprint;
import me.ionar.salhack.module.render.NametagsModule;
import me.ionar.salhack.module.ui.*;
import me.ionar.salhack.module.world.AutoToolModule;
import me.ionar.salhack.module.world.CoordsSpooferModule;
import me.ionar.salhack.module.world.TimerModule;
import me.ionar.salhack.preset.Preset;
import me.ionar.salhack.util.ReflectionUtil;

@SuppressWarnings("rawtypes")
public class ModuleManager {
    public static ModuleManager Get() {
        return SalHack.GetModuleManager();
    }

    public ModuleManager() {}

    public static ArrayList<Module> Modules = new ArrayList<>();
    private ArrayList<Module> ArrayListAnimations = new ArrayList<>();
    public void Init() {
        /// Combat
        Add(new KillAuraModule());
        Add(new OffhandModule());

        /// Exploit
        Add(new BowbombModule());

        /// Misc
        Add(new FakePlayer());
        Add(new FriendsModule());
        Add(new MiddleClickFriendsModule());

        /// Movement
        Add(new ElytraFlyModule());
        Add(new SpeedModule());
        Add(new Sprint());
        Add(new Rotation());

        /// Render
        Add(new NametagsModule());

        /// UI
        Add(new ColorsModule());
        Add(new ClickGuiModule());
        Add(new HudEditorModule());
        Add(new HudModule());
        Add(new Notification());

        /// World
        Add(new TimerModule());
        Add(new CoordsSpooferModule());
        Add(new AutoToolModule());

        /// Schematica


        LoadExternalModules();
        Modules.sort(Comparator.comparing(Module::getDisplayName));
        final Preset preset = PresetsManager.Get().getActivePreset();
        Modules.forEach(preset::initValuesForMod);
        Modules.forEach(Module::init);
    }


    public void Add(Module mod) {
        try {
            for (Field field : mod.getClass().getDeclaredFields()) {
                if (Value.class.isAssignableFrom(field.getType())) {
                    if (!field.canAccess(null)) field.setAccessible(true);
                    final Value val = (Value) field.get(mod);
                    val.InitializeModule(mod);
                    mod.getValueList().add(val);
                }
            }
            Modules.add(mod);
        } catch (Exception ignored) {}
    }

    public final List<Module> GetModuleList(ModuleType moduleType) {
        List<Module> list = new ArrayList<>();
        for (Module module : Modules) {
            if (module.getModuleType().equals(moduleType)) list.add(module);
        }
        // Organize alphabetically
        list.sort(Comparator.comparing(Module::getDisplayName));
        return list;
    }

    public final List<Module> GetModuleList() {
        return Modules;
    }

    public static void OnKeyPress(int key) {
        if (key == 0) return;
        Modules.forEach(module -> {
            if (module.isKeyPressed(key)) module.toggle(true);
        });
    }

    public Module GetMod(Class clazz) {
        for (Module module : Modules) {
            if (module.getClass() == clazz) return module;
        }
        SalHackMod.log.error("Could not find the class " + clazz.getName() + " in Mods list");
        return null;
    }

    public Module GetModLike(String name) {
        for (Module module : Modules) {
            if (module.getArrayListDisplayName().toLowerCase().startsWith(name.toLowerCase())) return module;
        }
        return null;
    }

    public void OnModEnable(Module module) {
        ArrayListAnimations.remove(module);
        ArrayListAnimations.add(module);
        final Comparator<Module> comparator = (first, second) -> {
            if (Wrapper.GetMC().textRenderer != null) {
                final String firstName = first.getFullArrayListDisplayName();
                final String secondName = second.getFullArrayListDisplayName();
                final float dif = Wrapper.GetMC().textRenderer.getWidth(secondName) - Wrapper.GetMC().textRenderer.getWidth(firstName);
                return dif != 0 ? (int) dif : secondName.compareTo(firstName);
            }
            return 0;
        };
        ArrayListAnimations = (ArrayList<Module>) ArrayListAnimations.stream().sorted(comparator).collect(Collectors.toList());
    }

    public void Update() {
        if (ArrayListAnimations.isEmpty()) return;
        Module module = ArrayListAnimations.get(0);
        if ((module.RemainingXAnimation -= ((float) Wrapper.GetMC().textRenderer.getWidth(module.getFullArrayListDisplayName()) / 10)) <= 0) {
            ArrayListAnimations.remove(module);
            module.RemainingXAnimation = 0;
        }
    }

    public void LoadExternalModules() {
        try {
            final File dir = new File("SalHack/CustomMods");
            for (Class<?> newClass : ReflectionUtil.getClassesEx(dir.getPath())) {
                if (newClass == null) continue;
                // if we have found a class and the class inherits "Module"
                if (Module.class.isAssignableFrom(newClass)) {
                    //create a new instance of the class
                    final Module module = (Module) newClass.getDeclaredConstructor().newInstance();
                    Add(module);
                }
            }
        } catch (Exception ignored) {}
    }
}
