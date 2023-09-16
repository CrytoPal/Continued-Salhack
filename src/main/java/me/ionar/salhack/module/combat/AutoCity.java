package me.ionar.salhack.module.combat;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.EventEra;
import me.ionar.salhack.events.player.PlayerMotionUpdate;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.managers.BlockManager;
import me.ionar.salhack.managers.FriendManager;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.util.CrystalUtils;
import me.ionar.salhack.util.entity.EntityUtil;
import me.ionar.salhack.util.entity.PlayerUtil;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Formatting;
import me.ionar.salhack.util.Pair;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class AutoCity extends Module {
    public AutoCity() {
        super("AutoCity", new String[]{ "AutoCityBoss" }, "Automatically mines the city block if a target near you can be citied", 0, 0xDADB24, ModuleType.COMBAT);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        final ArrayList<Pair<PlayerEntity, ArrayList<BlockPos>>> cityPlayers = GetPlayersReadyToBeCitied();

        if (cityPlayers.isEmpty()) {
            SendMessage(Formatting.RED + "There is no one to city!");
            toggle(true);
            return;
        }

        PlayerEntity target = null;
        BlockPos targetBlock = null;
        double currDistance = 100;

        for (Pair<PlayerEntity, ArrayList<BlockPos>> pair : cityPlayers) {
            for (BlockPos pos : pair.getSecond()) {
                if (targetBlock == null) {
                    target = pair.getFirst();
                    targetBlock = pos;
                    continue;
                }

                double dist = pos.getSquaredDistance(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());

                if (dist < currDistance) {
                    currDistance = dist;
                    targetBlock = pos;
                    target = pair.getFirst();
                }
            }
        }

        if (targetBlock == null) {
            SendMessage(Formatting.RED + "Couldn't find any blocks to mine!");
            toggle(true);
            return;
        }

        BlockManager.SetCurrentBlock(targetBlock);
        SendMessage(Formatting.LIGHT_PURPLE + "Attempting to mine a block by your target: " + Formatting.RED + target.getName().getString());
    }

    @EventHandler
    public void OnPlayerUpdate(PlayerMotionUpdate p_Event){
        if (p_Event.getEra() != EventEra.PRE)
            return;

        boolean hasPickaxe = mc.player.getMainHandStack().getItem() == Items.DIAMOND_PICKAXE;

        if (!hasPickaxe) {
            for (int i = 0; i < 9; ++i) {
                ItemStack stack = mc.player.getInventory().getStack(i);

                if (stack.isEmpty())
                    continue;

                if (stack.getItem() == Items.DIAMOND_PICKAXE || stack.getItem() == Items.NETHERITE_PICKAXE) {
                    hasPickaxe = true;
                    mc.player.getInventory().selectedSlot = i;
                    mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(i));
                    break;
                }
            }
        }

        if (!hasPickaxe) {
            SendMessage(Formatting.RED + "No pickaxe!");
            toggle(true);
            return;
        }

        BlockPos currBlock = BlockManager.GetCurrBlock();

        if (currBlock == null) {
            SendMessage(Formatting.GREEN + "Done!");
            toggle(true);
            return;
        }

        p_Event.cancel();

        final double rotations[] =  EntityUtil.calculateLookAt(
                currBlock.getX() + 0.5,
                currBlock.getY() - 0.5,
                currBlock.getZ() + 0.5,
                mc.player);

        PlayerUtil.PacketFacePitchAndYaw((float)rotations[0], (float)rotations[1]);

        BlockManager.Update(3, false);
    }


    public static ArrayList<Pair<PlayerEntity, ArrayList<BlockPos>>> GetPlayersReadyToBeCitied() {
        ArrayList<Pair<PlayerEntity, ArrayList<BlockPos>>> players = new ArrayList<>();

        for (Entity entity : Wrapper.GetMC().world.getPlayers().stream().filter(entityPlayer -> !FriendManager.Get().IsFriend(entityPlayer) && entityPlayer != MinecraftClient.getInstance().player).collect(Collectors.toList())) {
            ArrayList<BlockPos> positions = new ArrayList<>();

            for (int i = 0; i < 4; ++i) {
                BlockPos o = EntityUtil.GetPositionVectorBlockPos(entity, surroundOffset[i]);

                // ignore if the surrounding block is not obsidian
                if (Wrapper.GetMC().world.getBlockState(o).getBlock() != Blocks.OBSIDIAN)
                    continue;

                boolean passCheck = false;

                switch (i)
                {
                    case 0:
                        passCheck = CrystalUtils.canPlaceCrystal(o.north().down());
                        break;
                    case 1:
                        passCheck = CrystalUtils.canPlaceCrystal(o.east().down());
                        break;
                    case 2:
                        passCheck = CrystalUtils.canPlaceCrystal(o.south().down());
                        break;
                    case 3:
                        passCheck = CrystalUtils.canPlaceCrystal(o.west().down());
                        break;
                }

                if (passCheck)
                    positions.add(o);
            }

            if (!positions.isEmpty())
                players.add(new Pair<>((PlayerEntity) entity, positions));
        }
        return players;
    }

    private static final BlockPos[] surroundOffset = {
                    new BlockPos(0, 0, -1), // north
                    new BlockPos(1, 0, 0), // east
                    new BlockPos(0, 0, 1), // south
                    new BlockPos(-1, 0, 0) // west
    };
}