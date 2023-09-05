package me.ionar.salhack.module.movement;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Formatting;

import me.ionar.salhack.events.network.EventNetworkPacketEvent;
import me.ionar.salhack.events.player.EventPlayerTravel;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.MathUtil;
import me.ionar.salhack.util.Timer;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;

public final class ElytraFlyModule extends Module
{
    public final Value<Mode> mode = new Value<Mode>("Mode", new String[]
            { "Mode", "M" }, "Mode to use for 2b2t flight.", Mode.Superior);
    public final Value<Float> speed = new Value<Float>("Speed", new String[]
            { "Spd" }, "Speed multiplier for flight, higher values equals more speed. - 2b speed recommended is 1.8~", 1.82f, 0.0f, 10.0f, 0.1f);
    public final Value<Float> DownSpeed = new Value<Float>("DownSpeed", new String[]
            { "DS" }, "DownSpeed multiplier for flight, higher values equals more speed.", 1.82f, 0.0f, 10.0f, 0.1f);
    public final Value<Float> GlideSpeed = new Value<Float>("GlideSpeed", new String[]
            { "GlideSpeed" }, "Glide value for acceleration, this is divided by 10000.", 1f, 0f, 10f, 1f);
    public final Value<Float> UpSpeed = new Value<Float>("UpSpeed", new String[]
            { "UpSpeed" }, "Up speed for elytra.", 2.0f, 0f, 10f, 1f);
    public final Value<Boolean> Accelerate = new Value<Boolean>("Accelerate", new String[]
            { "Accelerate", "Accelerate" }, "Auto accelerates when going up", true);
    public final Value<Integer> vAccelerationTimer = new Value<Integer>("Timer", new String[]
            { "AT" }, "Acceleration timer, default 1000", 1000, 0, 10000, 1000);
    public final Value<Float> RotationPitch = new Value<Float>("RotationPitch", new String[]
            { "RP" }, "RotationPitch default 0.0, this is for going up, -90 is lowest you can face, 90 is highest", 0.0f, -90f, 90f, 10.0f);
    public final Value<Boolean> CancelInWater = new Value<Boolean>("CancelInWater", new String[]
            { "CiW" }, "Cancel in water, anticheat will flag you if you try to go up in water, accelerating will still work.", true);
    public final Value<Integer> CancelAtHeight = new Value<Integer>("CancelAtHeight", new String[]
            { "CAH" }, "Doesn't allow flight Y is below, or if too close to bedrock. since 2b anticheat is wierd", 5, 0, 10, 1);
    public final Value<Boolean> InstantFly = new Value<Boolean>("InstantFly", new String[]
            { "IF" }, "Sends the fall flying packet when your off ground", true);
    public final Value<Boolean> EquipElytra = new Value<Boolean>("EquipElytra", new String[] {"EE"}, "Equips your elytra when enabled if you're not already wearing one", false);
    public final Value<Boolean> PitchSpoof = new Value<Boolean>("PitchSpoof", new String[] {"PS"}, "Spoofs your pitch for hauses new patch", false);

    private Timer PacketTimer = new Timer();
    private Timer AccelerationTimer = new Timer();
    private Timer AccelerationResetTimer = new Timer();
    private Timer InstantFlyTimer = new Timer();
    private boolean SendMessage = false;

    private enum Mode
    {
        Normal, Tarzan, Superior, Packet, Control
    }

    public ElytraFlyModule()
    {
        super("ElytraFly", new String[]
                { "ElytraFly2b2t" }, "Allows you to fly with elytra on 2b2t", 0, 0x24DB26, ModuleType.MOVEMENT);
    }

    private int ElytraSlot = -1;

    @Override
    public void onEnable()
    {
        super.onEnable();

        ElytraSlot = -1;

        if (EquipElytra.getValue())
        {
            if (mc.player != null && mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() != Items.ELYTRA)
            {
                for (int l_I = 0; l_I < 44; ++l_I)
                {
                    ItemStack l_Stack = mc.player.getInventory().getStack(l_I);

                    if (l_Stack.isEmpty() || l_Stack.getItem() != Items.ELYTRA)
                        continue;

                    ElytraItem l_Elytra = (ElytraItem)l_Stack.getItem();

                    ElytraSlot = l_I;
                    break;
                }

                if (ElytraSlot != -1)
                {
                    boolean l_HasArmorAtChest = mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() != Items.AIR;

                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, ElytraSlot, 0, SlotActionType.PICKUP, mc.player);
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 6, 0, SlotActionType.PICKUP, mc.player);

                    if (l_HasArmorAtChest)
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, ElytraSlot, 0, SlotActionType.PICKUP, mc.player);
                }
            }
        }
    }

    @Override
    public void onDisable()
    {
        super.onDisable();

        if (mc.player == null)
            return;

        if (ElytraSlot != -1)
        {
            boolean l_HasItem = !mc.player.getInventory().getStack(ElytraSlot).isEmpty() || mc.player.getInventory().getStack(ElytraSlot).getItem() != Items.AIR;

            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 6, 0, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, ElytraSlot, 0, SlotActionType.PICKUP, mc.player);

            if (l_HasItem)
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 6, 0, SlotActionType.PICKUP, mc.player);
        }
    }

    @Override
    public String getMetaData()
    {
        return this.mode.getValue().name();
    }

    @EventHandler
    private Listener<EventPlayerTravel> OnTravel = new Listener<>(p_Event ->
    {
        if (mc.player == null) return;

        /// Player must be wearing an elytra.
        if (mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() != Items.ELYTRA)
            return;

        if (!mc.player.isFallFlying())
        {
            if (!mc.player.isOnGround() && InstantFly.getValue())
            {
                if (!InstantFlyTimer.passed(1000))
                    return;

                InstantFlyTimer.reset();

                mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            }

            return;
        }

        switch (mode.getValue())
        {
            case Normal:
            case Tarzan:
            case Packet:
                HandleNormalModeElytra(p_Event);
                break;
            case Superior:
                HandleImmediateModeElytra(p_Event);
                break;
            case Control:
                HandleControlMode(p_Event);
                break;
            default:
                break;
        }
    });

    public void HandleNormalModeElytra(EventPlayerTravel p_Travel)
    {
        double l_YHeight = mc.player.getY();

        if (l_YHeight <= CancelAtHeight.getValue())
        {
            if (!SendMessage)
            {
                SalHack.SendMessage(Formatting.RED + "WARNING, you must scaffold up or use fireworks, as YHeight <= CancelAtHeight!");
                SendMessage = true;
            }

            return;
        }

        boolean l_IsMoveKeyDown = mc.player.input.movementForward > 0 || mc.player.input.movementSideways > 0;

        boolean l_CancelInWater = !mc.player.isTouchingWater() && !mc.player.isInLava() && CancelInWater.getValue();

        if (mc.player.input.jumping)
        {
            //p_Travel.cancel();
            Accelerate();
            return;
        }

        if (!l_IsMoveKeyDown)
        {
            AccelerationTimer.resetTimeSkipTo(-vAccelerationTimer.getValue());
        }
        else if ((mc.player.getPitch() <= RotationPitch.getValue() || mode.getValue() == Mode.Tarzan) && l_CancelInWater)
        {
            if (Accelerate.getValue())
            {
                if (AccelerationTimer.passed(vAccelerationTimer.getValue()))
                {
                    Accelerate();
                    return;
                }
            }
            return;
        }

        //p_Travel.cancel();
        Accelerate();
    }

    public void HandleImmediateModeElytra(EventPlayerTravel p_Travel)
    {
        if (mc.player.input.jumping)
        {
            double l_MotionSq = Math.sqrt(mc.player.getVelocity().x * mc.player.getVelocity().x + mc.player.getVelocity().z * mc.player.getVelocity().z);

            if (l_MotionSq > 1.0)
            {
                return;
            }
            else
            {
                double[] dir = MathUtil.directionSpeedNoForward(speed.getValue());

                mc.player.setVelocity(dir[0], -(GlideSpeed.getValue() / 10000f), dir[1]);
            }

            //p_Travel.cancel();
            return;
        }

        mc.player.setVelocity(0, 0, 0);

        //p_Travel.cancel();

        double[] dir = MathUtil.directionSpeed(speed.getValue());

        if (mc.player.input.movementSideways != 0 || mc.player.input.movementForward != 0)
        {
            mc.player.setVelocity(dir[0], -(GlideSpeed.getValue() / 10000f), dir[1]);
        }

        if (mc.player.input.sneaking)
            mc.player.setVelocity(mc.player.getVelocity().x, -DownSpeed.getValue(), mc.player.getVelocity().z);
    }

    public void Accelerate()
    {
        if (AccelerationResetTimer.passed(vAccelerationTimer.getValue()))
        {
            AccelerationResetTimer.reset();
            AccelerationTimer.reset();
            SendMessage = false;
        }

        float l_Speed = this.speed.getValue();

        final double[] dir = MathUtil.directionSpeed(l_Speed);

        mc.player.setVelocity(mc.player.getVelocity().x, -(GlideSpeed.getValue() / 10000f), mc.player.getVelocity().z);

        if (mc.player.input.movementSideways != 0 || mc.player.input.movementForward != 0)
        {
            mc.player.setVelocity(dir[0], mc.player.getVelocity().y, dir[1]);
        }
        else
        {
            mc.player.setVelocity(0, mc.player.getVelocity().y, 0);
        }

        if (mc.player.input.sneaking)
            mc.player.setVelocity(mc.player.getVelocity().x, -DownSpeed.getValue(), mc.player.getVelocity().z);
    }


    private void HandleControlMode(EventPlayerTravel p_Event)
    {
        final double[] dir = MathUtil.directionSpeed(speed.getValue());

        if (mc.player.input.movementSideways != 0 || mc.player.input.movementForward != 0)
        {
            mc.player.setVelocity(dir[0], mc.player.getVelocity().y, dir[1]);

            mc.player.addVelocity(-((mc.player.getVelocity().x*(Math.abs(mc.player.getPitch())+90)/90) - mc.player.getVelocity().x), mc.player.getVelocity().y, -((mc.player.getVelocity().z*(Math.abs(mc.player.getPitch())+90)/90) - mc.player.getVelocity().z));
        }
        else
        {
            mc.player.setVelocity(0, mc.player.getVelocity().y, 0);
        }

        mc.player.setVelocity(mc.player.getVelocity().x, (-MathUtil.degToRad(mc.player.getPitch())) * mc.player.input.movementForward, mc.player.getVelocity().z);

        //p_Event.cancel();
    }

    @EventHandler
    private Listener<EventNetworkPacketEvent> PacketEvent = new Listener<>(p_Event ->
    {
        if (p_Event.getPacket() instanceof PlayerMoveC2SPacket && PitchSpoof.getValue())
        {
            if (!mc.player.isFallFlying())
                return;

            if (p_Event.getPacket() instanceof PlayerMoveC2SPacket.Full && PitchSpoof.getValue())
            {
                PlayerMoveC2SPacket.Full rotation = (PlayerMoveC2SPacket.Full) p_Event.getPacket();

                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(rotation.getX(0), rotation.getY(0), rotation.getZ(0), rotation.isOnGround()));
                p_Event.cancel();
            }
            else if (p_Event.getPacket() instanceof PlayerMoveC2SPacket.LookAndOnGround && PitchSpoof.getValue())
            {
                p_Event.cancel();
            }
        }
    });
}
