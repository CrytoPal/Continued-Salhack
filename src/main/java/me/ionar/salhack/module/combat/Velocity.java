package me.ionar.salhack.module.combat;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.network.PacketEvent;
import me.ionar.salhack.mixin.VelocityUpdateAccessor;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;

public final class Velocity extends Module {

    public final Value<Integer> horizontal_vel = new Value<Integer>("Horizontal", new String[]{"Horizontal_Velocity", "HVel", "HV", "HorizontalVel", "Horizontal", "H"}, "The horizontal velocity you will take.", 0, 0, 100, 1);
    public final Value<Integer> vertical_vel = new Value<Integer>("Veritcal", new String[]{"Vertical_Velocity", "VVel", "VV", "VerticalVel", "Vertical", "Vert", "V"}, "The vertical velocity you will take.", 0, 0, 100, 1);
    public final Value<Boolean> explosions = new Value<Boolean>("Explosions", new String[]{"Explosions", "Explosion", "EXP", "EX", "Expl"}, "Apply velocity modifier on explosion velocity.", true);
    public final Value<Boolean> bobbers = new Value<Boolean>("Bobbers", new String[]{"Bobb", "Bob", "FishHook", "FishHooks"}, "Apply velocity modifier on fishing bobber velocity.", true);
    public final Value<Boolean> NoPush = new Value<Boolean>("NoPush", new String[]{"AntiPush"}, "Disable collision with entities, blocks and water", true);

    @EventHandler
    public void onPlayerMotionUpdate(PacketEvent.Receive event){
        if (mc.player == null)
            return;

        if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket) {
            final EntityVelocityUpdateS2CPacket packet = (EntityVelocityUpdateS2CPacket) event.getPacket();
            if (packet.getId() == mc.player.getId()) {
                if (this.horizontal_vel.getValue() == 0 && this.vertical_vel.getValue() == 0) {
                    event.cancel();
                    return;
                }

                if (this.horizontal_vel.getValue() != 100) {
                    ((VelocityUpdateAccessor)packet).setX(packet.getVelocityX() / 100 * horizontal_vel.getValue());
                    ((VelocityUpdateAccessor)packet).setZ(packet.getVelocityZ() / 100 * horizontal_vel.getValue());
                }

                if (this.vertical_vel.getValue() != 100) {
                    ((VelocityUpdateAccessor)packet).setY(packet.getVelocityY() / 100 * vertical_vel.getValue());
                }
            }
        }
        if (event.getPacket() instanceof ExplosionS2CPacket && explosions.getValue()) {
            final ExplosionS2CPacket packet = (ExplosionS2CPacket) event.getPacket();

            if (this.horizontal_vel.getValue() == 0 && vertical_vel.getValue() == 0) {
                event.cancel();
                return;
            }

            if (this.horizontal_vel.getValue() != 100) {
                ((VelocityUpdateAccessor)packet).setX((int) (packet.getPlayerVelocityX() / 100 * horizontal_vel.getValue()));
                ((VelocityUpdateAccessor)packet).setZ((int) packet.getPlayerVelocityZ() / 100 * horizontal_vel.getValue());
            }

            if (this.vertical_vel.getValue() != 100) {
                ((VelocityUpdateAccessor)packet).setY((int)packet.getPlayerVelocityY() / 100 * vertical_vel.getValue());
            }
        }
    }

    public Velocity() {
        super("Velocity", "Modify the velocity you take", 0, 0x9B24DB, ModuleType.COMBAT);
    }

    @Override
    public String getMetaData() {
        return String.format("H:%s%% V:%s%%", this.horizontal_vel.getValue(), this.vertical_vel.getValue());
    }

}