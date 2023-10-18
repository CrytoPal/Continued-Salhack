package me.ionar.salhack.module.combat;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.EventEra;
import me.ionar.salhack.events.player.PlayerMotionUpdate;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.ChatUtils;
import me.ionar.salhack.util.Timer;
import me.ionar.salhack.util.entity.ItemUtil;
import me.ionar.salhack.util.entity.PlayerUtil;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

public final class AutoExp extends Module {
    public final Value<Float> Delay = new Value<>("Delay", new String[]{"D"}, "Delay for moving armor pieces", 1.0f, 0.0f, 10.0f, 1.0f);
    public final Value<Float> Pct = new Value<>("Pct", new String[]{"P"}, "Amount of armor pct to heal at, so you don't waste extra experience potions", 90.0f, 0.0f, 100.0f, 10.0f);
    public final Value<Boolean> GhostHand = new Value<>("GhostHand", new String[]{"GH"}, "Uses ghost hand for exp", false);
    private final LinkedList<MendState> SlotsToMoveTo = new LinkedList<>();
    private final Timer timer = new Timer();
    private final Timer internalTimer = new Timer();
    private boolean ReadyToMend = false;
    private boolean AllDone = false;
    @EventHandler
    private void onPlayerUpdate(PlayerMotionUpdate event) {
        if (event.getEra() != EventEra.PRE)
            return;

        event.cancel();

        if (mc.player != null) {
            mc.player.setPitch(90);

            if (timer.passed(Delay.getValue() * 100)) {
                timer.reset();

                if (SlotsToMoveTo.isEmpty())
                    return;

                boolean needBreak = false;

                for (MendState state : SlotsToMoveTo) {
                    if (state.MovedToInv)
                        continue;

                    state.MovedToInv = true;

                    //   SendMessage("" + state.SlotMovedTo);

                    if (state.Reequip) {
                        if (state.SlotMovedTo <= 4) {
                            ItemUtil.Move(state.SlotMovedTo, state.ArmorSlot);
                        }
                    } else {
                        ItemUtil.Move(state.SlotMovedTo, state.ArmorSlot);
                        ItemUtil.Move(state.ArmorSlot, state.SlotMovedTo);
                    }

                    needBreak = true;
                    break;
                }

                if (!needBreak) {
                    ReadyToMend = true;

                    if (AllDone) {
                        ChatUtils.warningMessage("Disabling.");
                        toggle(true);
                        return;
                    }
                }
            }

            if (!internalTimer.passed(1000))
                return;

            if (ReadyToMend && !AllDone) {
                ItemStack currItem = mc.player.getMainHandStack();

                int currSlot = -1;
                if (currItem.isEmpty() || currItem.getItem() != Items.EXPERIENCE_BOTTLE) {
                    int slot = PlayerUtil.GetItemInHotbar(Items.EXPERIENCE_BOTTLE);

                    if (slot != -1) {
                        currSlot = mc.player.getInventory().selectedSlot;
                        mc.player.getInventory().selectedSlot = slot;
                    } else {
                        ChatUtils.errorMessage("No XP Found! Disabling.");

                        SlotsToMoveTo.forEach(state ->
                        {
                            state.MovedToInv = false;
                            state.Reequip = true;
                        });

                        SlotsToMoveTo.get(0).MovedToInv = true;
                        AllDone = true;
                        return;
                    }
                }

                currItem = mc.player.getMainHandStack();

                if (currItem.isEmpty() || currItem.getItem() != Items.EXPERIENCE_BOTTLE)
                    return;

                final Iterator<ItemStack> armor = mc.player.getArmorItems().iterator();

                while (armor.hasNext()) {
                    ItemStack stack = armor.next();

                    if (stack == ItemStack.EMPTY || stack.getItem() == Items.AIR)
                        continue;

                    float armorPct = GetArmorPct(stack);

                    if (armorPct >= Pct.getValue()) {
                        if (!SlotsToMoveTo.isEmpty()) {
                            MendState state = SlotsToMoveTo.get(0);

                            if (state.DoneMending) {
                                SlotsToMoveTo.forEach(state1 ->
                                {
                                    state1.MovedToInv = false;
                                    state1.Reequip = true;
                                });
                                ChatUtils.sendMessage(Formatting.GREEN + "All done!");
                                state.MovedToInv = true;
                                AllDone = true;
                                return;
                            }

                            state.DoneMending = true;
                            state.MovedToInv = false;
                            state.Reequip = false;

                            ChatUtils.sendMessage("Done Mending");
                            ReadyToMend = false;

                            SlotsToMoveTo.remove(0);
                            SlotsToMoveTo.add(state);
                        }

                        return;
                    } else {
                        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);

                        if (currSlot != -1 && GhostHand.getValue()) {
                            mc.player.getInventory().selectedSlot = currSlot;
                        }

                        break;
                    }
                }
            }
        }
    }

    public AutoExp() {
        super("AutoExp", "Fixes your armor", 0, 0x24DBD4, ModuleType.COMBAT);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        ArrayList<ItemStack> ArmorsToMend = new ArrayList<ItemStack>();
        SlotsToMoveTo.clear();
        ReadyToMend = false;
        AllDone = false;

        int slot = PlayerUtil.GetItemInHotbar(Items.EXPERIENCE_BOTTLE);

        if (slot == -1) {
            ChatUtils.errorMessage("You don't have any XP! Disabling!");
            toggle(true);
            return;
        }

        final Iterator<ItemStack> armor = mc.player.getArmorItems().iterator();

        int i = 0;
        boolean needMend = false;

        while (armor.hasNext()) {
            final ItemStack item = armor.next();
            if (item != ItemStack.EMPTY && item.getItem() != Items.AIR) {
                ArmorsToMend.add(item);

                float pct = GetArmorPct(item);

                if (pct < Pct.getValue()) {
                    needMend = true;
                    ChatUtils.sendMessage(Formatting.LIGHT_PURPLE + "[" + ++i + "] Mending " + Formatting.AQUA + item.getName() + Formatting.LIGHT_PURPLE + " it has " + pct + "%.");
                }
            }
        }

        if (ArmorsToMend.isEmpty() || !needMend) {
            ChatUtils.warningMessage("Nothing to mend!");
            toggle(true);
            return;
        }

        ArmorsToMend.sort(Comparator.comparing(ItemStack::getDamage).reversed());

        ArmorsToMend.forEach(item ->
        {
            ChatUtils.sendMessage(item.getName() + " " + item.getDamage());
        });

        i = 0;

        final Iterator<ItemStack> itr = ArmorsToMend.iterator();

        boolean first = true;

        for (i = 0; i < mc.player.getInventory().size(); ++i) {
            if (i == 0 || i == 5 || i == 6 || i == 7 || i == 8)
                continue;

            ItemStack stack = mc.player.getInventory().getStack(i);

            /// Slot must be empty or air
            if (!stack.isEmpty() && stack.getItem() != Items.AIR)
                continue;

            if (!itr.hasNext())
                break;

            ItemStack armorS = itr.next();
            SlotsToMoveTo.add(new MendState(first, i, GetSlotByItemStack(armorS), GetArmorPct(armorS) < Pct.getValue(), String.format(armorS.getName() + "")));
            if (first)
                first = false;

            // SendMessage("Found free slot " + i + " for " + armorS.getDisplayName() + " stack here is " + stack.getDisplayName());
        }
    }

    public int GetSlotByItemStack(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem) {
            ArmorItem armor = (ArmorItem) stack.getItem();

            switch (armor.getSlotType()) {
                case CHEST:
                    return 6;
                case FEET:
                    return 8;
                case HEAD:
                    return 5;
                case LEGS:
                    return 7;
                default:
                    break;
            }
        }
        return 0;
    }

    private float GetArmorPct(ItemStack stack) {
        return ((float) (stack.getMaxDamage() - stack.getDamage()) / (float) stack.getMaxDamage()) * 100.0f;
    }

    private class MendState {
        public boolean MovedToInv = false;
        public int SlotMovedTo = -1;
        public boolean Reequip = false;
        public int ArmorSlot = -1;
        public boolean DoneMending = false;
        public boolean NeedMend = true;
        public String ItemName;
        public MendState(boolean movedToInv, int slotMovedTo, int armorSlot, boolean needMend, String itemName) {
            MovedToInv = movedToInv;
            SlotMovedTo = slotMovedTo;
            ArmorSlot = armorSlot;
            NeedMend = needMend;
            ItemName = itemName;
        }
    }
}