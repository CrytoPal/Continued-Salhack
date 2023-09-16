package me.ionar.salhack.module.combat;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.world.TickEvent;
import me.ionar.salhack.module.Module;
import net.minecraft.item.BowItem;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

import static me.ionar.salhack.main.Wrapper.mc;

public class BowSpam extends Module {
    public BowSpam() {
        super("BowSpam", new String[]
                { "BS" }, "Releases the bow as fast as possible", 0, 0xDB2424, ModuleType.COMBAT);
    }

    @EventHandler
    public void onTick(TickEvent event){
        if (mc.player.getMainHandStack().getItem() instanceof BowItem && mc.player.isUsingItem() && mc.player.getItemUseTime() >= 3) {
            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
            mc.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(mc.player.getOffHandStack().getItem() == Items.BOW ? Hand.OFF_HAND : Hand.MAIN_HAND, 0));
            mc.player.stopUsingItem();
        }
    }
}