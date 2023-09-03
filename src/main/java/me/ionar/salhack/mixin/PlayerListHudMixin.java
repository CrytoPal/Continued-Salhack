package me.ionar.salhack.mixin;

import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.module.ui.HudModule;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Nullables;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.scoreboard.Team;

import java.util.Comparator;
import java.util.List;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {

    private static final Comparator<Object> ENTRY_ORDERING = Comparator.comparingInt((entry) -> {
        return ((PlayerListEntry)entry).getGameMode() == GameMode.SPECTATOR ? 1 : 0;
    }).thenComparing((entry) -> {
        return (String) Nullables.mapOrElse(((PlayerListEntry)entry).getScoreboardTeam(), Team::getName, "");
    }).thenComparing((entry) -> {
        return ((PlayerListEntry)entry).getProfile().getName();
    }, String::compareToIgnoreCase);

    @Inject(method = "collectPlayerEntries", at = @At("HEAD"), cancellable = true)
    private void collectPlayerEntriesHook(CallbackInfoReturnable<List<PlayerListEntry>> cir) {
        cir.setReturnValue(Wrapper.GetMC().player.networkHandler.getListedPlayerListEntries().stream().sorted(ENTRY_ORDERING).limit(HudModule.ExtraTab.getValue()).toList());
    }
}
