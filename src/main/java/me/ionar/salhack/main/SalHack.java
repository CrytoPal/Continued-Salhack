package me.ionar.salhack.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.managers.*;
import net.minecraft.text.Text;

public class SalHack {
    public static int TICK_TIMER = 1;
    private static final ModuleManager ModuleManager = new ModuleManager();
    private static final ImageManager ImageManager = new ImageManager();
    //private static FontManager m_FontManager = new FontManager();
    private static final HudManager HudManager = new HudManager();
    private static final FriendManager FriendManager = new FriendManager();
    //private static DiscordManager m_DiscordManager = new DiscordManager();
    private static final FilesManager FilesManager = new FilesManager();

    private static final NotificationManager NotificationManager = new NotificationManager();
    private static final CommandManager CommandManager = new CommandManager();
    private static final TickRateManager TickRateManager = new TickRateManager();
    //private static NotificationManager m_NotificationManager = new NotificationManager();
    //private static WaypointManager m_WaypointManager = new WaypointManager();
    //private static CapeManager m_CapeManager = new CapeManager();
    private static final PresetsManager PresetsManager = new PresetsManager();
    //private static UUIDManager UUIDManager = new UUIDManager();

    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();


    public static void Init() {
        SalHackMod.log.info("Initializing Salhack");
        FilesManager.Init();
        SalHackMod.log.info("Initializing Files Manager");

        /// load before mods
        //m_FontManager.Load();
        PresetsManager.LoadPresets(); // must be before module init
        SalHackMod.log.info("Loaded Presets");
        ModuleManager.Init();
        SalHackMod.log.info("Loaded Modules");
        HudManager.Init();
        SalHackMod.log.info("Loaded Hud");
        CommandManager.InitializeCommands();
        SalHackMod.log.info("Loaded Commands");
        FriendManager.Load();
        SalHackMod.log.info("Loaded Friends");
        SalHackMod.log.info("Strange. There was no friends in your friend manager :)");
    }

    public static void postWindowInit() {
        ImageManager.Load();
    }

    public static ModuleManager GetModuleManager() {
        return ModuleManager;
    }
    public static ImageManager GetImageManager(){
        return ImageManager;
    }
/*
    public static FontManager GetFontManager()
    {
        return m_FontManager;
    }

 */

    /// Writes a message to ingame chat
    /// Player must be ingame for this
    public static void SendMessage(String string) {
        if (Wrapper.GetMC().player == null) return;
        Wrapper.GetMC().player.sendMessage(Text.of(string));
    }

    public static HudManager GetHudManager() {
        return HudManager;
    }

    public static FriendManager GetFriendManager() {
        return FriendManager;
    }
/*
    public static DiscordManager GetDiscordManager()
    {
        return m_DiscordManager;
    }
 */

    public static FilesManager GetFilesManager() {
        return FilesManager;
    }

    public static CommandManager GetCommandManager() {
        return CommandManager;
    }

    public static TickRateManager GetTickRateManager() {
        return TickRateManager;
    }

    public static NotificationManager GetNotificationManager() {
        return NotificationManager;
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

    public static PresetsManager GetPresetsManager() {
        return PresetsManager;
    }
/*
    public static UUIDManager GetUUIDManager()
    {
        return m_UUIDManager;
    }

 */
}
