package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.font.Renderer;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.module.ui.HudModule;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import javax.swing.*;
import java.util.Objects;

public class PingComponent extends HudComponentItem {
    private final HudModule hud = (HudModule) ModuleManager.Get().GetMod(HudModule.class);
    private final int i = 0;
    public PingComponent() {
        super("Ping", 2, 43);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks, DrawContext context) {
        super.render(mouseX, mouseY, partialTicks, context);

        if (mc.world != null) {
            PlayerListEntry playerListEntry = Objects.requireNonNull(mc.player).networkHandler.getPlayerListEntry(mc.player.getUuid());

            final String ping = String.format("Ping " + Formatting.WHITE + playerListEntry.getLatency());

            context.drawTextWithShadow(mc.textRenderer, Text.of(ping), (int) GetX(), (int) GetY(), 0x2ACCED);

            SetWidth(Wrapper.GetMC().textRenderer.getWidth(ping));
            SetHeight(Wrapper.GetMC().textRenderer.fontHeight);
        }
    }
}