package me.ionar.salhack.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.managers.*;
import net.minecraft.text.Text;

public class SalHack {
    public static int TICK_TIMER = 1;
    private static final ModuleManager moduleManager = new ModuleManager();
    private static final ImageManager imageManager = new ImageManager();
    //private static FontManager m_FontManager = new FontManager();
    private static final HudManager hudManager = new HudManager();
    private static final FriendManager friendManager = new FriendManager();
    //private static DiscordManager m_DiscordManager = new DiscordManager();
    private static final FilesManager filesManager = new FilesManager();

    private static final NotificationManager notificationManager = new NotificationManager();
    private static final CommandManager commandManager = new CommandManager();
    private static final TickRateManager tickRateManager = new TickRateManager();
    //private static NotificationManager m_NotificationManager = new NotificationManager();
    //private static WaypointManager m_WaypointManager = new WaypointManager();
    //private static CapeManager m_CapeManager = new CapeManager();
    private static final PresetsManager presetsManager = new PresetsManager();
    //private static UUIDManager UUIDManager = new UUIDManager();
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();


    public static void Init() {
        SalHackMod.log.info("Initializing Salhack");
        filesManager.init();
        SalHackMod.log.info("Initializing Files Manager");
        /// load before mods
        //m_FontManager.Load();
        presetsManager.init(); // must be before module init
        SalHackMod.log.info("Loaded Presets");
        moduleManager.init();
        SalHackMod.log.info("Loaded Modules");
        hudManager.init();
        SalHackMod.log.info("Loaded Hud");
        commandManager.init();
        SalHackMod.log.info("Loaded Commands");
        friendManager.init();
        SalHackMod.log.info("Loaded Friends");
        SalHackMod.log.info("Strange. There was no friends in your friend manager :)");
    }

    public static void postWindowInit() {
        imageManager.init();
    }

    public static ModuleManager getModuleManager() {
        return moduleManager;
    }
    public static ImageManager getImageManager(){
        return imageManager;
    }
/*
    public static FontManager GetFontManager()
    {
        return m_FontManager;
    }

 */

    /// Writes a message to ingame chat
    /// Player must be ingame for this
    public static void sendMessage(String string) {
        if (Wrapper.GetMC().player == null) return;
        Wrapper.GetMC().player.sendMessage(Text.of(string));
    }

    public static HudManager getHudManager() {
        return hudManager;
    }

    public static FriendManager getFriendManager() {
        return friendManager;
    }
/*
    public static DiscordManager GetDiscordManager()
    {
        return m_DiscordManager;
    }
 */

    public static FilesManager getFilesManager() {
        return filesManager;
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

    public static TickRateManager getTickRateManager() {
        return tickRateManager;
    }

    public static NotificationManager getNotificationManager() {
        return notificationManager;
    }

    /*

    public static WaypointManager GetWaypointManager()
    {
        return m_WaypointManager;
    }

    public static CapeManager GetCapeManager()
    {
        return m_CapeManager;
    }

 */

    public static PresetsManager getPresetsManager() {
        return presetsManager;
    }
/*
    public static UUIDManager GetUUIDManager()
    {
        return m_UUIDManager;
    }

 */
}
