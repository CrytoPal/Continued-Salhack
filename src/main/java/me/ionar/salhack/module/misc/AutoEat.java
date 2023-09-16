package me.ionar.salhack.module.misc;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.EventEra;
import me.ionar.salhack.events.player.PlayerMotionUpdate;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.entity.PlayerUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;

public class AutoEat extends Module {
    public final Value<Float> HealthToEatAt = new Value<Float>("HealthToEatAt", new String[] {"Health"}, "Will eat gaps at required health", 15.0f, 0.0f, 36.0f, 3.0f);
    public final Value<Float> RequiredHunger = new Value<Float>("Hunger", new String[] {"Hunger"}, "Required hunger to eat", 18.0f, 0.0f, 20.0f, 1.0f);

    public AutoEat() {
        super("AutoEat", new String[] {"Eat"}, "Automatically eats food, depending on hunger, or health", 0, 0xFFFB11, ModuleType.MISC);
    }

    private boolean m_WasEating = false;

    @Override
    public void onDisable() {
        super.onDisable();

        if (m_WasEating)
        {
            m_WasEating = false;
            mc.options.useKey.setPressed(false);
        }
    }

    @EventHandler
    public void OnPlayerUpdate(PlayerMotionUpdate p_Event) {

        float l_Health = mc.player.getHealth() + mc.player.getAbsorptionAmount();

        if (HealthToEatAt.getValue() >= l_Health) {
            if (mc.player.getMainHandStack().getItem() != Items.GOLDEN_APPLE) {
                for (int l_I = 0; l_I < 9; ++l_I) {
                    if (mc.player.getInventory().getStack(l_I).isEmpty() || mc.player.getInventory().getStack(l_I).getItem() != Items.GOLDEN_APPLE)
                        continue;

                    mc.player.getInventory().selectedSlot = l_I;
                    mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(l_I));
                    break;
                }

                if (mc.currentScreen == null)
                    mc.options.useKey.setPressed(true);
                else
                    mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);

                m_WasEating = true;
            }
        }

        else if (RequiredHunger.getValue() >= mc.player.getHungerManager().getFoodLevel()) {
            boolean l_CanEat = false;

            for (int l_I = 0; l_I < 9; ++l_I) {
                ItemStack l_Stack = mc.player.getInventory().getStack(l_I);

                if (mc.player.getInventory().getStack(l_I).isEmpty())
                    continue;

                if (l_Stack.getItem().isFood()) {
                    l_CanEat = true;
                    mc.player.getInventory().selectedSlot = l_I;
                    mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(l_I));
                    break;
                }
            }

            if (l_CanEat) {
                if (mc.currentScreen == null)
                    mc.options.useKey.setPressed(true);
                else
                    mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);

                m_WasEating = true;
            }
        }

        else if (m_WasEating) {
            m_WasEating = false;
            mc.options.useKey.setPressed(false);
        }
    }
}