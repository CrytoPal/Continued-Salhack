package me.ionar.salhack.module.combat;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.player.PlayerMotionUpdate;
import me.ionar.salhack.gui.hud.components.ArmorHudComponent;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.Timer;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

public class AutoArmor extends Module
{
    public final Value<Float> delay = new Value<Float>("Delay", new String[]
            { "Del" }, "The amount of delay in milliseconds.", 50.0f, 0.0f, 1000.0f, 1.0f);
    public final Value<Boolean> curse = new Value<Boolean>("Curse", new String[]
            { "Curses" }, "Prevents you from equipping armor with cursed enchantments.", false);
    public final Value<Boolean> PreferElytra = new Value<Boolean>("Elytra", new String[] {"Wings"}, "Prefers elytra over chestplate if available", false);
    public final Value<Boolean> ElytraReplace = new Value<Boolean>("ElytraReplace", new String[] {"ElytraReplace"}, "Attempts to replace your broken elytra", false);

    private Timer timer = new Timer();

    public AutoArmor()
    {
        super("AutoArmor", new String[]
                { "AutoArm", "AutoArmour" }, "Automatically equips armor", 0, 0x249FDB, ModuleType.COMBAT);
    }

   // private AutoMendArmorModule AutoMend = null;

    @Override
    public void onEnable()
    {
        super.onEnable();

     //   AutoMend = (AutoMendArmorModule)ModuleManager.Get().GetMod(AutoMendArmorModule.class);
    }

    private void SwitchItemIfNeed(ItemStack p_Stack, EquipmentSlot p_Slot, int p_ArmorSlot)
    {
        if (p_Stack.getItem() == Items.AIR)
        {
            if (!timer.passed(delay.getValue()))
                return;

            final int l_FoundSlot = findArmorSlot(p_Slot);

            if (l_FoundSlot != -1)
            {

                timer.reset();

                /// support for xcarry
                if (l_FoundSlot <= 4)
                {
                    /// We can't use quick move for this. have to send 2 packets, pickup and drop down.
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, l_FoundSlot, 0, SlotActionType.PICKUP, mc.player);
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, p_ArmorSlot, 0, SlotActionType.PICKUP, mc.player);
                }
                else
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, l_FoundSlot, 0, SlotActionType.QUICK_MOVE, mc.player);
            }
        }
    }

    @EventHandler
    public void OnPlayerUpdate(PlayerMotionUpdate event){
        if (mc.currentScreen instanceof GenericContainerScreen)
            return;

      //  if (AutoMend != null && AutoMend.isEnabled())
      //      return;

        SwitchItemIfNeed(mc.player.getInventory().getStack(36), EquipmentSlot.FEET, 8);
        SwitchItemIfNeed(mc.player.getInventory().getStack(37), EquipmentSlot.LEGS, 7);
        SwitchItemIfNeed(mc.player.getInventory().getStack(38), EquipmentSlot.CHEST, 6);
        SwitchItemIfNeed(mc.player.getInventory().getStack(39), EquipmentSlot.HEAD, 5);

        if (ElytraReplace.getValue() && !mc.player.getInventory().getStack(6).isEmpty())
        {
            ItemStack stack = mc.player.getInventory().getStack(6);

            if (stack.getItem() instanceof ElytraItem)
            {
                if (!ElytraItem.isUsable(stack) && ArmorHudComponent.GetPctFromStack(stack) < 3)
                {
                    for (int i = 0; i < mc.player.getInventory().size(); ++i)
                    {
                        /// @see: https://wiki.vg/Inventory, 0 is crafting slot, and 5,6,7,8 are Armor slots
                        if (i == 0 || i == 5 || i == 6 || i == 7 || i == 8)
                            continue;

                        ItemStack s = mc.player.getInventory().getStack(i);
                        if (s != null && s.getItem() != Items.AIR)
                        {
                            if (s.getItem() instanceof ElytraItem && ElytraItem.isUsable(s))
                            {
                                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, mc.player);
                                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 6, 0, SlotActionType.PICKUP, mc.player);
                                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, mc.player);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private int findArmorSlot(EquipmentSlot type)
    {
        int slot = -1;
        float damage = 0;

        for (int i = 0; i < 36; i++)
        {

            ItemStack s = mc.player.getInventory().getStack(i);
            if (s != null && s.getItem() != Items.AIR)
            {
                if (s.getItem() instanceof ArmorItem armor)
                {
                    if (armor.getType().getEquipmentSlot() == type)
                    {
                        final float currentDamage = (armor.getProtection() + EnchantmentHelper.getLevel(Enchantments.PROTECTION, s));

                        final boolean cursed = this.curse.getValue() && (EnchantmentHelper.hasBindingCurse(s));

                        if (currentDamage > damage && !cursed)
                        {
                            damage = currentDamage;
                            slot = i;
                        }
                    }
                }
                else if (type == EquipmentSlot.CHEST && PreferElytra.getValue() && s.getItem() instanceof ElytraItem && ArmorHudComponent.GetPctFromStack(s) > 3)
                    return (i < 9 ? i + 36 : i);
            }
        }

        return (slot < 9 ? slot + 36 : slot);
    }
}
