package me.ionar.salhack.module.misc;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.player.PlayerMotionUpdate;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.entity.EntityUtil;
import me.ionar.salhack.util.entity.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.ShearsItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;

import java.util.ArrayList;
import java.util.Comparator;


public class AutoShear extends Module {
    public final Value<Integer> Radius = new Value<>("Radius", new String[] {"R"}, "Radius to search for sheep", 4, 0, 10, 1);

    public AutoShear()
    {
        super("AutoShear", new String[] {""}, "Shears sheep in range", 0, -1, ModuleType.MISC);
    }

    @EventHandler
    public void OnPlayerUpdate(PlayerMotionUpdate p_Event) {
        if (!(mc.player.getMainHandStack().getItem() instanceof ShearsItem))
            return;


        ArrayList<Entity> entities = new ArrayList<>();
        mc.world.getEntities().forEach(entities::add);

        SheepEntity l_Sheep = entities.stream()
                .filter(p_Entity -> IsValidSheep(p_Entity))
                .map(p_Entity -> (SheepEntity) p_Entity)
                .min(Comparator.comparing(p_Entity -> mc.player.squaredDistanceTo(p_Entity)))
                .orElse(null);

        if (l_Sheep != null) {
            p_Event.cancel();

            final double l_Pos[] =  EntityUtil.calculateLookAt(
                    l_Sheep.getX(),
                    l_Sheep.getY(),
                    l_Sheep.getZ(),
                    mc.player);

            PlayerUtil.PacketFacePitchAndYaw((float)l_Pos[0], (float)l_Pos[1]);

            mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.interact(l_Sheep, mc.player.isSneaking(), Hand.MAIN_HAND));
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }

    private boolean IsValidSheep(Entity p_Entity) {
        if (!(p_Entity instanceof SheepEntity l_Sheep))
            return false;

        if (p_Entity.distanceTo(mc.player) > Radius.getValue())
            return false;

        if (l_Sheep.isShearable())
            return true;

        return false;
    }
}