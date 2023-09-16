package me.ionar.salhack.main;

import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.managers.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import static me.ionar.salhack.main.Wrapper.mc;

public class SalHack {

    public static int TICK_TIMER = 1;
    private static ModuleManager m_ModuleManager = new ModuleManager();

    private static ImageManager m_ImageManager = new ImageManager();
    //private static FontManager m_FontManager = new FontManager();
    private static HudManager m_HudManager = new HudManager();
    private static FriendManager m_FriendManager = new FriendManager();
    //private static DiscordManager m_DiscordManager = new DiscordManager();
    private static DirectoryManager m_DirectoryManager = new DirectoryManager();

    private static NotificationManager m_NotificationManager = new NotificationManager();
    private static CommandManager m_CommandManager = new CommandManager();
    private static TickRateManager m_TickRateManager = new TickRateManager();
    //private static NotificationManager m_NotificationManager = new NotificationManager();
    //private static WaypointManager m_WaypointManager = new WaypointManager();
    //private static CapeManager m_CapeManager = new CapeManager();
    private static PresetsManager m_PresetsManager = new PresetsManager();
    //private static UUIDManager m_UUIDManager = new UUIDManager();


    public static void Init() {
        SalHackMod.log.info("Initalizing Salhack");
        m_DirectoryManager.Init();
        SalHackMod.log.info("Initalizing Directory Manager");

        /// load before mods
        //m_FontManager.Load();
        m_PresetsManager.LoadPresets(); // must be before module init
        SalHackMod.log.info("Loaded Presets");
        m_ModuleManager.Init();
        SalHackMod.log.info("Loaded Modules");
        m_HudManager.Init();
        SalHackMod.log.info("Loaded Hud");
        m_CommandManager.InitalizeCommands();
        SalHackMod.log.info("Loaded Commands");
        m_FriendManager.Load();
        SalHackMod.log.info("Loaded Friends");
        SalHackMod.log.info("Strange. There was no friends in your friend manager :)");
    }

    public static void postWindowInit() {
        m_ImageManager.Load();
    }

    public static ModuleManager GetModuleManager() {
        return m_ModuleManager;
    }
    public static ImageManager GetImageManager(){
        return m_ImageManager;
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
        mc.player.sendMessage(Text.of(string));
    }

    public static HudManager GetHudManager() {
        return m_HudManager;
    }

    public static FriendManager GetFriendManager() {
        return m_FriendManager;
    }
/*
    public static DiscordManager GetDiscordManager()
    {
        return m_DiscordManager;
    }
 */

    public static DirectoryManager GetDirectoryManager() {
        return m_DirectoryManager;
    }

    public static CommandManager GetCommandManager() {
        return m_CommandManager;
    }

    public static TickRateManager GetTickRateManager() {
        return m_TickRateManager;
    }

    public static NotificationManager GetNotificationManager() {
        return m_NotificationManager;
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
        return m_PresetsManager;
    }
/*
    public static UUIDManager GetUUIDManager()
    {
        return m_UUIDManager;
    }

 */
}
