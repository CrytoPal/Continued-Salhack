package me.ionar.salhack.module.combat;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.player.PlayerMotionUpdate;
import me.ionar.salhack.gui.hud.components.ArmorHudComponent;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.Timer;
import me.ionar.salhack.util.entity.ItemUtil;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class AutoArmor extends Module {
    public final Value<Float> delay = new Value<>("Delay", new String[]{"Del"}, "The amount of delay in milliseconds.", 50.0f, 0.0f, 1000.0f, 1.0f);
    public final Value<Boolean> curse = new Value<>("Curse", new String[]{"Curses"}, "Prevents you from equipping armor with cursed enchantments.", false);
    public final Value<Boolean> preferElytra = new Value<>("Elytra", new String[]{"Wings"}, "Prefers elytra over chestplate if available", false);
    public final Value<Boolean> elytraReplace = new Value<>("ElytraReplace", new String[]{"ElytraReplace"}, "Attempts to replace your broken elytra", false);
    private final Timer timer = new Timer();

    public AutoArmor() {
        super("AutoArmor", new String[]{ "AutoArm", "AutoArmour" }, "Automatically equips armor", 0, 0x249FDB, ModuleType.COMBAT);
    }

    // private AutoMendArmorModule AutoMend = null;

    @Override
    public void onEnable() {
        super.onEnable();
        //AutoMend = (AutoMendArmorModule)ModuleManager.Get().GetMod(AutoMendArmorModule.class);
    }

    private void SwitchItemIfNeed(ItemStack stack, EquipmentSlot slot, int armorSlot) {
        if (mc.interactionManager == null || mc.player == null) return;
        if (stack.getItem() == Items.AIR) {
            if (!timer.passed(delay.getValue())) return;
            final int foundSlot = findArmorSlot(slot);
            if (foundSlot != -1) {
                timer.reset();
                if (foundSlot <= 4) {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, foundSlot, 0, SlotActionType.PICKUP, mc.player);
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, armorSlot, 0, SlotActionType.PICKUP, mc.player);
                } else mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, foundSlot, 0, SlotActionType.QUICK_MOVE, mc.player);
            }
        }
    }

    @EventHandler
    public void OnPlayerUpdate(PlayerMotionUpdate event){
        if (mc.currentScreen instanceof GenericContainerScreen || mc.player == null || mc.interactionManager == null) return;
        //if (AutoMend != null && AutoMend.isEnabled())
        //    return;
        SwitchItemIfNeed(mc.player.getInventory().getStack(36), EquipmentSlot.FEET, 8);
        SwitchItemIfNeed(mc.player.getInventory().getStack(37), EquipmentSlot.LEGS, 7);
        SwitchItemIfNeed(mc.player.getInventory().getStack(38), EquipmentSlot.CHEST, 6);
        SwitchItemIfNeed(mc.player.getInventory().getStack(39), EquipmentSlot.HEAD, 5);
        if (elytraReplace.getValue() && !mc.player.getInventory().getStack(6).isEmpty()) {
            ItemStack stack = mc.player.getInventory().getStack(6);
            if (stack.getItem() instanceof ElytraItem) {
                if (!ElytraItem.isUsable(stack) && ArmorHudComponent.getPctFromStack(stack) < 3) {
                    for (int i = 0; i < mc.player.getInventory().size(); ++i) {
                        if (i == 0 || i == 5 || i == 6 || i == 7 || i == 8) continue;
                        ItemStack s = mc.player.getInventory().getStack(i);
                        if (s != null && s.getItem() != Items.AIR) {
                            if (s.getItem() instanceof ElytraItem && ElytraItem.isUsable(s)) {
                                ItemUtil.move(i, 6);
                                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, mc.player);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private int findArmorSlot(EquipmentSlot type) {
        if (mc.player == null) return -1;
        int slot = -1;
        float damage = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack s = mc.player.getInventory().getStack(i);
            if (s != null && s.getItem() != Items.AIR) {
                if (s.getItem() instanceof ArmorItem armor) {
                    if (armor.getType().getEquipmentSlot() == type) {
                        final float currentDamage = (armor.getProtection() + EnchantmentHelper.getLevel(Enchantments.PROTECTION, s));
                        final boolean cursed = this.curse.getValue() && (EnchantmentHelper.hasBindingCurse(s));
                        if (currentDamage > damage && !cursed) {
                            damage = currentDamage;
                            slot = i;
                        }
                    }
                }
                else if (type == EquipmentSlot.CHEST && preferElytra.getValue() && s.getItem() instanceof ElytraItem && ArmorHudComponent.getPctFromStack(s) > 3) return (i < 9 ? i + 36 : i);
            }
        }
        return (slot < 9 ? slot + 36 : slot);
    }
}
