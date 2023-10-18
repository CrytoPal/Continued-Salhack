package me.ionar.salhack.util;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static me.ionar.salhack.util.SalUtil.mc;

public class ChatUtils {

    public static void sendMessage(String message) {
        mc.player.sendMessage(Text.of(Formatting.AQUA + "[Salhack] " + Formatting.WHITE + message));
    }

    public static void warningMessage(String message) {
        mc.player.sendMessage(Text.of(Formatting.AQUA + "[Salhack] " + Formatting.YELLOW + message));
    }

    public static void errorMessage(String message) {
        mc.player.sendMessage(Text.of(Formatting.AQUA + "[Salhack] " + Formatting.RED + message));
    }
}
