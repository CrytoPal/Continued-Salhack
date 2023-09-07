package me.ionar.salhack.module.combat;

import me.ionar.salhack.events.client.EventClientTick;
import me.ionar.salhack.managers.FriendManager;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.entity.ItemUtil;
import me.ionar.salhack.util.entity.PlayerUtil;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;

public final class OffhandModule extends Module {
    public final Value<Float> health = new Value<Float>("Health", new String[]
            {"Hp"}, "The amount of health needed to acquire a totem.", 16.0f, 0.0f, 20.0f, 0.5f);
    public final Value<offhandMode> Mode = new Value<offhandMode>("Mode", new String[]{"Mode"}, "If you are above the required health for a totem, x will be used in offhand instead.", offhandMode.Totem);
    // Will fix later
    //public final Value<AutoTotemMode> FallbackMode = new Value<AutoTotemMode>("Fallback", new String[]{"FallbackMode"}, "If you don't have the required item for mode, this will be the fallback.", AutoTotemMode.Crystal);
    public final Value<Float> FallDistance = new Value<Float>("FallDistance", new String[]{"Fall"}, "If your fall distance exceeds this value, use a totem", 15.0f, 0.0f, 100.0f, 10.0f);
    public final Value<Boolean> TotemOnElytra = new Value<Boolean>("TotemOnElytra", new String[]{"Elytra"}, "Will automatically switch to a totem if you're elytra flying", true);
    public final Value<Boolean> OffhandGapOnSword = new Value<Boolean>("SwordGap", new String[]{"SwordGap"}, "Will override all else, and try and use a gap in offhand when using a sword in main hand", false);
    public final Value<Boolean> OffhandStrNoStrSword = new Value<Boolean>("StrGap", new String[]{"Strength"}, "Will put a potion if offhand if you don't have strength and wearing a sword", false);
    public final Value<Boolean> Override = new Value<Boolean>("Override", new String[]{"O"}, "Replaces your current offhand with the item mode if there's another item", false);
    public final Value<Boolean> NearPlayers = new Value<Boolean>("e",new String[]{"NP"},"hi", true);

    // public final Value<Boolean> InventorySwitch = new Value<Boolean>("Switch in Inv", new String[]{"Strength"}, "Puts the Item into your offhand while inventory gui is on.", true);
    // public final Value<Boolean> HotbarFirst = new Value<Boolean>("HotbarFirst", new String[]{"Recursive"}, "Prioritizes your hotbar before inventory slots", false);

    @EventHandler
    private final Listener<EventClientTick> OnPlayerUpdate = new Listener<>(event ->
    {
        if (mc.player != null) {
            boolean elytra = mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA && mc.player.isFallFlying();

            if (!mc.player.getMainHandStack().isEmpty()) {
                if (health.getValue() <= PlayerUtil.GetHealthWithAbsorption() && mc.player.getMainHandStack().getItem() instanceof SwordItem && OffhandStrNoStrSword.getValue()) {
                    SwitchOffHandIfNeed(offhandMode.Strength);
                    return;
                }

                /// Sword override
                if (health.getValue() <= PlayerUtil.GetHealthWithAbsorption() && mc.player.getMainHandStack().getItem() instanceof SwordItem && OffhandGapOnSword.getValue()) {
                    SwitchOffHandIfNeed(offhandMode.EGap);
                    return;
                }
            }

            /// First check health, most important as we don't want to die for no reason.
            if (health.getValue() > PlayerUtil.GetHealthWithAbsorption() || Mode.getValue() == offhandMode.Totem || (TotemOnElytra.getValue() && elytra) || (mc.player.fallDistance >= FallDistance.getValue() && !elytra) || noNearbyPlayers()) {
                SwitchOffHandIfNeed(offhandMode.Totem);
                return;
            }

            /// If we meet the required health

            SwitchOffHandIfNeed(Mode.getValue());

        }
    });

    public OffhandModule() {
        super("Offhand", new String[]
                {"OF"}, "Automatically puts an Item of your choice in your offhand", 0, 0xDADB24, ModuleType.COMBAT);
    }

    @Override
    public String getMetaData() {
        return String.valueOf(Mode.getValue());
    }

    private void SwitchOffHandIfNeed(offhandMode val) {
        Item item = GetItemFromModeVal(val);

        if (mc.player.playerScreenHandler == mc.player.currentScreenHandler) {
            if (mc.player.getOffHandStack().isEmpty()) {
                for (int i = 9; i < 45; i++) {
                    if (mc.player.getInventory().getStack(i >= 36 ? i - 36 : i).getItem() == item) {
                        ItemUtil.Move(i,45);
                        return;
                    }
                }
            }
            if (mc.player.getOffHandStack().getItem() != item && Override.getValue()) {
                for (int i = 9; i < 45; i++) {
                    if (mc.player.getInventory().getStack(i >= 36 ? i - 36 : i).getItem() == item) {
                        ItemUtil.Move(i,45);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();

    }

    public Item GetItemFromModeVal(offhandMode val) {
        switch (val) {
            case Crystal:
                return Items.END_CRYSTAL;
            case EGap:
                return Items.ENCHANTED_GOLDEN_APPLE;
            case Pearl:
                return Items.ENDER_PEARL;
            case Chorus:
                return Items.CHORUS_FRUIT;
            case Strength:
                return Items.POTION;
            case Shield:
                return Items.SHIELD;
            default:
                break;
        }

        return Items.TOTEM_OF_UNDYING;
    }

    private String GetItemNameFromModeVal(offhandMode val) {
        switch (val) {
            case Crystal:
                return "End Crystal";
            case EGap:
                return "EGap";
            case Pearl:
                return "Pearl";
            case Chorus:
                return "Chorus";
            case Strength:
                return "Strength";
            case Shield:
                return "Shield";
            default:
                break;
        }

        return "Totem";
    }

    private boolean noNearbyPlayers() {
        return offhandMode.Crystal == Mode.getValue() && mc.world.getPlayers().stream().noneMatch(e -> e != mc.player && isValidTarget(e));
    }

    private boolean isValidTarget(Entity entity) {
        if (FriendManager.Get().IsFriend(entity)) {
            return false;
        }

        if (entity == mc.player) {
            return false;
        }
        return !(mc.player.distanceTo(entity) > 15);
    }

    public enum offhandMode {
        Totem,
        EGap,
        Crystal,
        Pearl,
        Chorus,
        Strength,
        Shield,
    }
}