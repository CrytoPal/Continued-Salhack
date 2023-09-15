package me.ionar.salhack.module.misc;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.EventEra;
import me.ionar.salhack.events.player.PlayerMotionUpdate;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.Timer;

import me.ionar.salhack.util.entity.EntityUtil;
import me.ionar.salhack.util.entity.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;


import java.util.ArrayList;
import java.util.Comparator;

public class AutoMount extends Module
{
    public final Value<Boolean> Boats = new Value<Boolean>("Boats", new String[] {"Boat"}, "Mounts boats", true);
    public final Value<Boolean> Horses = new Value<Boolean>("Horses", new String[] {"Horse"}, "Mounts Horses", true);
    public final Value<Boolean> SkeletonHorses = new Value<Boolean>("SkeletonHorses", new String[] {"SkeletonHorse"}, "Mounts SkeletonHorses", true);
    public final Value<Boolean> Donkeys = new Value<Boolean>("Donkeys", new String[] {"Donkey"}, "Mounts Donkeys", true);
    public final Value<Boolean> Pigs = new Value<Boolean>("Pigs", new String[] {"Pig"}, "Mounts Pigs", true);
    public final Value<Boolean> Llamas = new Value<Boolean>("Llamas", new String[] {"Llama"}, "Mounts Llamas", true);
    public final Value<Boolean> Striders = new Value<Boolean>("Striders", new String[] {"Strider"}, "Mounts Striders", true);
    public final Value<Integer> Range = new Value<Integer>("Range", new String[] {"R"}, "Range to search for mountable entities", 4, 0, 10, 1);
    public final Value<Float> Delay = new Value<Float>("Delay", new String[] {"D"}, "Delay to use", 1.0f, 0.0f, 10.0f, 1.0f);

    public AutoMount()
    {
        super("AutoMount", new String[] {""}, "Automatically attempts to mount an entity near you", 0, -1, ModuleType.MISC);
    }

    private Timer timer = new Timer();

    @EventHandler
    public void OnPlayerUpdate(PlayerMotionUpdate p_Event){
        if(p_Event.getEra() != EventEra.PRE)
            return;

        if (mc.player.isRiding())
            return;

        if (!timer.passed(Delay.getValue() * 1000))
            return;

        timer.reset();

        ArrayList<Entity> entities = new ArrayList<>();
        mc.world.getEntities().forEach(entities::add);

        Entity l_Entity = entities.stream()
                .filter(p_Entity -> isValidEntity(p_Entity))
                .min(Comparator.comparing(p_Entity -> mc.player.squaredDistanceTo(p_Entity)))
                .orElse(null);

        if (l_Entity != null)
        {
            p_Event.cancel();

            final double l_Pos[] = EntityUtil.calculateLookAt(
                    l_Entity.getX(),
                    l_Entity.getY(),
                    l_Entity.getZ(),
                    mc.player);

            PlayerUtil.PacketFacePitchAndYaw((float) l_Pos[0], (float) l_Pos[1]);

            mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.interact(l_Entity, mc.player.isSneaking(), Hand.MAIN_HAND));
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }

    private boolean isValidEntity(Entity entity)
    {
        if (entity.distanceTo(mc.player) > Range.getValue())
            return false;

        if (entity instanceof AbstractHorseEntity horse_Entity)
        {
            if (horse_Entity.isBaby())
                return false;
        }

        if (entity instanceof BoatEntity && Boats.getValue())
            return true;

        if (entity instanceof SkeletonHorseEntity && SkeletonHorses.getValue())
            return true;

        if (entity instanceof HorseEntity && Horses.getValue())
            return true;

        if (entity instanceof DonkeyEntity && Donkeys.getValue())
            return true;

        if (entity instanceof PigEntity pig && Pigs.getValue())
        {

            if (pig.isSaddled())
                return true;

            return false;
        }

        if (entity instanceof LlamaEntity l_Llama && Llamas.getValue())
        {
            if (!l_Llama.isBaby())
                return true;
        }

        // New Salhack = new entities :D
        if (entity instanceof StriderEntity strider && Striders.getValue())
        {
            if (!strider.isBaby() && strider.isSaddled())
                return true;
        }

        return false;
    }
}