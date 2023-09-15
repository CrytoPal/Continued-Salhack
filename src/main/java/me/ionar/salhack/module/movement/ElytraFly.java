package me.ionar.salhack.module.movement;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.network.PacketEvent;
import me.ionar.salhack.events.player.PlayerTravelEvent;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.MathUtil;
import me.ionar.salhack.util.Timer;
import me.ionar.salhack.util.entity.ItemUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Formatting;

public final class ElytraFly extends Module {
    public final Value<modes> mode = new Value<>("Mode", new String[]{"Mode", "M"}, "Mode to use for 2b2t flight.", modes.Superior);
    public final Value<Float> speed = new Value<>("Speed", new String[]{"Spd"}, "Speed multiplier for flight, higher values equals more speed. - 2b speed recommended is 1.8~", 1.82f, 0.0f, 10.0f, 0.1f);
    public final Value<Float> downSpeed = new Value<>("DownSpeed", new String[]{"DS"}, "DownSpeed multiplier for flight, higher values equals more speed.", 1.82f, 0.0f, 10.0f, 0.1f);
    public final Value<Float> glideSpeed = new Value<>("GlideSpeed", new String[]{"GlideSpeed"}, "Glide value for acceleration, this is divided by 10000.", 1f, 0f, 10f, 1f);
    public final Value<Float> upSpeed = new Value<>("UpSpeed", new String[]{"UpSpeed"}, "Up speed for elytra.", 2.0f, 0f, 10f, 1f);
    public final Value<Boolean> accelerate = new Value<>("Accelerate", new String[]{"Accelerate", "Accelerate"}, "Auto accelerates when going up", true);
    public final Value<Integer> verticalAccelerationTimer = new Value<>("Timer", new String[]{"AT"}, "Acceleration timer, default 1000", 1000, 0, 10000, 1000);
    public final Value<Float> rotationPitch = new Value<>("RotationPitch", new String[]{"RP"}, "RotationPitch default 0.0, this is for going up, -90 is lowest you can face, 90 is highest", 0.0f, -90f, 90f, 10.0f);
    public final Value<Boolean> cancelInWater = new Value<>("CancelInWater", new String[]{"CiW"}, "Cancel in water, anticheat will flag you if you try to go up in water, accelerating will still work.", true);
    public final Value<Integer> cancelAtHeight = new Value<>("CancelAtHeight", new String[]{"CAH"}, "Doesn't allow flight Y is below, or if too close to bedrock. since 2b anticheat is wierd", 5, 0, 10, 1);
    public final Value<Boolean> instantFly = new Value<>("InstantFly", new String[]{"IF"}, "Sends the fall flying packet when your off ground", true);
    public final Value<Boolean> equipElytra = new Value<>("EquipElytra", new String[]{"EE"}, "Equips your elytra when enabled if you're not already wearing one", false);
    public final Value<Boolean> pitchSpoof = new Value<>("PitchSpoof", new String[]{"PS"}, "Spoofs your pitch for hauses new patch", false);
    private final Timer packetTimer = new Timer();
    private final Timer accelerationTimer = new Timer();
    private final Timer accelerationResetTimer = new Timer();
    private final Timer instantFlyTimer = new Timer();
    private boolean sendMessage = false;
    private int elytraSlot = -1;
    public enum modes {
        Normal, Tarzan, Superior, Packet, Control
    }

    public ElytraFly() {
        super("ElytraFly", new String[]{ "ElytraFly2b2t" }, "Allows you to fly with elytra on 2b2t", 0, 0x24DB26, ModuleType.MOVEMENT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.player == null || mc.interactionManager == null) {
            toggle(true);
            return;
        }
        elytraSlot = -1;
        if (equipElytra.getValue()) {
            if (mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() != Items.ELYTRA) {
                for (int i = 0; i < 44; ++i) {
                    ItemStack Stack = mc.player.getInventory().getStack(i);
                    if (Stack.isEmpty() || Stack.getItem() != Items.ELYTRA) continue;
                    elytraSlot = i;
                    break;
                }
                if (elytraSlot != -1) {
                    boolean HasArmorAtChest = mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() != Items.AIR;
                    ItemUtil.move(elytraSlot, 6);
                    if (HasArmorAtChest) mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, elytraSlot, 0, SlotActionType.PICKUP, mc.player);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.player == null || mc.interactionManager == null) return;
        if (elytraSlot != -1) {
            boolean HasItem = !mc.player.getInventory().getStack(elytraSlot).isEmpty() || mc.player.getInventory().getStack(elytraSlot).getItem() != Items.AIR;
            ItemUtil.move(6, elytraSlot);
            if (HasItem) mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 6, 0, SlotActionType.PICKUP, mc.player);
        }
    }

    @Override
    public String getMetaData() {
        return mode.getValue().name();
    }

    @EventHandler
    private void onTravel(PlayerTravelEvent event) {
        if (mc.player == null) return;
        /// Player must be wearing an elytra.
        if (mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() != Items.ELYTRA) return;
        if (!mc.player.isFallFlying()) {
            if (!mc.player.isOnGround() && instantFly.getValue()) {
                if (!instantFlyTimer.passed(1000)) return;
                instantFlyTimer.reset();
                mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            }
            return;
        }
        switch (mode.getValue()) {
            case Normal, Tarzan, Packet -> handleNormalModeElytra(event);
            case Superior -> handleImmediateModeElytra(event);
            case Control -> handleControlMode(event);
            default -> {}
        }
    }

    public void handleNormalModeElytra(PlayerTravelEvent Travel) {
        if (mc.player == null) return;
        double YHeight = mc.player.getY();
        if (YHeight <= cancelAtHeight.getValue()) {
            if (!sendMessage) {
                SalHack.sendMessage(Formatting.RED + "WARNING, you must scaffold up or use fireworks, as YHeight <= CancelAtHeight!");
                sendMessage = true;
            }
            return;
        }
        boolean IsMoveKeyDown = mc.player.input.movementForward > 0 || mc.player.input.movementSideways > 0;
        boolean cancelInWater = !mc.player.isTouchingWater() && !mc.player.isInLava() && this.cancelInWater.getValue();
        if (mc.player.input.jumping) {
            accelerate();
            return;
        }
        if (!IsMoveKeyDown) accelerationTimer.resetTimeSkipTo(-verticalAccelerationTimer.getValue());
        else if ((mc.player.getPitch() <= rotationPitch.getValue() || mode.getValue() == modes.Tarzan) && cancelInWater) {
            if (accelerate.getValue() && accelerationTimer.passed(verticalAccelerationTimer.getValue())) {
                accelerate();
                return;
            }
            return;
        }
        accelerate();
    }

    public void handleImmediateModeElytra(PlayerTravelEvent Travel) {
        if (mc.player == null) return;
        if (mc.player.input.jumping) {
            double MotionSquared = Math.sqrt(mc.player.getVelocity().x * mc.player.getVelocity().x + mc.player.getVelocity().z * mc.player.getVelocity().z);
            if (MotionSquared > 1.0) return;
            else {
                double[] dir = MathUtil.directionSpeedNoForward(speed.getValue());
                mc.player.setVelocity(dir[0], -(glideSpeed.getValue() / 10000f), dir[1]);
            }
            return;
        }
        mc.player.setVelocity(0, 0, 0);
        double[] dir = MathUtil.directionSpeed(speed.getValue());
        if (mc.player.input.movementSideways != 0 || mc.player.input.movementForward != 0) mc.player.setVelocity(dir[0], -(glideSpeed.getValue() / 10000f), dir[1]);
        if (mc.player.input.sneaking) mc.player.setVelocity(mc.player.getVelocity().x, -downSpeed.getValue(), mc.player.getVelocity().z);
    }

    public void accelerate() {
        if (mc.player == null) return;
        if (accelerationResetTimer.passed(verticalAccelerationTimer.getValue())) {
            accelerationResetTimer.reset();
            accelerationTimer.reset();
            sendMessage = false;
        }
        final double[] dir = MathUtil.directionSpeed(speed.getValue());
        mc.player.setVelocity(mc.player.getVelocity().x, -(glideSpeed.getValue() / 10000f), mc.player.getVelocity().z);
        if (mc.player.input.movementSideways != 0 || mc.player.input.movementForward != 0) mc.player.setVelocity(dir[0], mc.player.getVelocity().y, dir[1]);
        else mc.player.setVelocity(0, mc.player.getVelocity().y, 0);
        if (mc.player.input.sneaking) mc.player.setVelocity(mc.player.getVelocity().x, -downSpeed.getValue(), mc.player.getVelocity().z);
    }


    private void handleControlMode(PlayerTravelEvent Event) {
        if (mc.player == null) return;
        final double[] dir = MathUtil.directionSpeed(speed.getValue());
        if (mc.player.input.movementSideways != 0 || mc.player.input.movementForward != 0) {
            mc.player.setVelocity(dir[0], mc.player.getVelocity().y, dir[1]);
            mc.player.addVelocity(-((mc.player.getVelocity().x*(Math.abs(mc.player.getPitch())+90)/90) - mc.player.getVelocity().x), mc.player.getVelocity().y, -((mc.player.getVelocity().z*(Math.abs(mc.player.getPitch())+90)/90) - mc.player.getVelocity().z));
        } else mc.player.setVelocity(0, mc.player.getVelocity().y, 0);
        mc.player.setVelocity(mc.player.getVelocity().x, (-MathUtil.degToRad(mc.player.getPitch())) * mc.player.input.movementForward, mc.player.getVelocity().z);
    }

    @EventHandler
    private void onPacketSend(PacketEvent.Send event) {
        if (!event.isPre()) return;
        if (mc.player == null) return;
        if (event.getPacket() instanceof PlayerMoveC2SPacket && pitchSpoof.getValue()) {
            if (!mc.player.isFallFlying()) return;
            if (event.getPacket() instanceof PlayerMoveC2SPacket.Full rotation && pitchSpoof.getValue()) {
                if (mc.getNetworkHandler() == null) return;
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(rotation.getX(0), rotation.getY(0), rotation.getZ(0), rotation.isOnGround()));
                event.cancel();
            } else if (event.getPacket() instanceof PlayerMoveC2SPacket.LookAndOnGround && pitchSpoof.getValue()) event.cancel();
        }
    }
}
