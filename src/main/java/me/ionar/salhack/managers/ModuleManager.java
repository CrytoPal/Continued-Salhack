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
// DO NOT TOUCH THESE THEY MAY BREAK OPENING THE GUI
public class ModuleManager {

    public ModuleManager() {
    }

    public static ArrayList<Module> modules = new ArrayList<Module>();
    private ArrayList<Module> arrayListAnimations = new ArrayList<Module>();
    public void init() {
        /// Combat
        add(new KillAuraModule());
        add(new OffhandModule());

        /// Exploit
        add(new BowbombModule());

        /// Misc
        add(new FakePlayer());
        add(new FriendsModule());
        add(new MiddleClickFriendsModule());

        /// Movement
        add(new ElytraFlyModule());
        add(new SpeedModule());
        add(new Sprint());
        add(new Rotation());

        /// Render
        add(new NametagsModule());

        /// UI
        add(new ColorsModule());
        add(new ClickGuiModule());
        add(new HudEditorModule());
        add(new HudModule());
        add(new Notification());

        /// World
        add(new TimerModule());
        add(new CoordsSpooferModule());
        add(new AutoToolModule());

        /// Schematica



        loadExternalModules();

        modules.sort((p_Mod1, p_Mod2) -> p_Mod1.getDisplayName().compareTo(p_Mod2.getDisplayName()));

        final Preset preset = SalHack.getPresetsManager().getActivePreset();

        modules.forEach(mod -> {
            preset.initValuesForModule(mod);
        });

        modules.forEach(mod -> {
            mod.init();
        });
    }


    public void add(Module mod) {
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
            modules.add(mod);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final List<Module> getModuleList(ModuleType p_Type) {
        List<Module> list = new ArrayList<>();
        for (Module module : modules) {
            if (module.getModuleType().equals(p_Type)) {
                list.add(module);
            }
        }
        // Organize alphabetically
        list.sort(Comparator.comparing(Module::getDisplayName));

        return list;
    }

    public final List<Module> getModuleList() {
        return modules;
    }

    public static void onKeyPress(int key) {
        if (key == 0) return;

        modules.forEach(p_Mod -> {
            if (p_Mod.isKeyPressed(key)) {
                p_Mod.toggle(true);
            }
        });
    }

    public Module getMod(Class p_Class) {
        for (Module l_Mod : modules) {
            if (l_Mod.getClass() == p_Class)
                return l_Mod;
        }

        SalHackMod.log.error("Could not find the class " + p_Class.getName() + " in Mods list");
        return null;
    }

    public Module getModLike(String p_String) {
        for (Module l_Mod : modules) {
            if (l_Mod.getArrayListDisplayName().toLowerCase().startsWith(p_String.toLowerCase()))
                return l_Mod;
        }

        return null;
    }

    public void onModEnable(Module p_Mod) {
        arrayListAnimations.remove(p_Mod);
        arrayListAnimations.add(p_Mod);

        final Comparator<Module> comparator = (first, second) -> {
            if (Wrapper.GetMC().textRenderer == null) {
            } else {
                final String firstName = first.getFullArrayListDisplayName();
                final String secondName = second.getFullArrayListDisplayName();
                final float dif = Wrapper.GetMC().textRenderer.getWidth(secondName) - Wrapper.GetMC().textRenderer.getWidth(firstName);
                return dif != 0 ? (int) dif : secondName.compareTo(firstName);
            }
            return 0;
        };

        arrayListAnimations = (ArrayList<Module>) arrayListAnimations.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public void update() {
        if (arrayListAnimations.isEmpty())
            return;

        Module l_Mod = arrayListAnimations.get(0);

        if ((l_Mod.RemainingXAnimation -= (Wrapper.GetMC().textRenderer.getWidth(l_Mod.getFullArrayListDisplayName()) / 10)) <= 0) {
            arrayListAnimations.remove(l_Mod);
            l_Mod.RemainingXAnimation = 0;
        }
    }

    public void loadExternalModules() {
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
                        add(module);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
