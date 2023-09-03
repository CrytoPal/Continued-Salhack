package me.ionar.salhack.module.render;

import me.ionar.salhack.events.render.EventRenderEntityName;
import me.ionar.salhack.events.render.EventRenderGameOverlay;
import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.friend.Friend;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.managers.FriendManager;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.MathUtil;
import me.ionar.salhack.util.entity.EntityUtil;
import me.ionar.salhack.util.render.TransformPositionUtil;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.*;
import java.util.List;

import com.google.common.collect.Lists;
import org.joml.Vector4d;

public class NametagsModule extends Module
{
    public final Value<Boolean> Armor = new Value<Boolean>("Armor", new String[]{ "" }, "", true);
    public final Value<Boolean> Durability = new Value<Boolean>("Durability", new String[]{ "" }, "", true);
    public final Value<Boolean> ItemName = new Value<Boolean>("ItemName", new String[]{ "" }, "", true);
    public final Value<Boolean> Health = new Value<Boolean>("Health", new String[]{ "" }, "", true);
    public final Value<Boolean> Invisibles = new Value<Boolean>("Invisibles", new String[]{ "" }, "", false);
    public final Value<Boolean> EntityID = new Value<Boolean>("EntityID", new String[]{ "" }, "", false);
    public final Value<Boolean> GameMode = new Value<Boolean>("GameMode", new String[]{ "" }, "", false);
    public final Value<Boolean> Ping = new Value<Boolean>("Ping", new String[]{ "" }, "", true);
    Entity camera = mc.getCameraEntity();
    public NametagsModule()
    {
        super("NameTags", new String[]
                { "Nametag" }, "Improves nametags of players around you", 0, -1, ModuleType.RENDER);
    }

    @EventHandler
    private Listener<EventRenderGameOverlay> OnRenderGameOverlay = new Listener<>(p_Event ->
    {
        EntityUtil.getEntities().stream().filter(EntityUtil::isLiving).filter(entity -> !EntityUtil.isFakeLocalPlayer(entity))
                .filter(entity -> (entity instanceof PlayerEntity && mc.player != entity)).forEach(e ->
                {
                    RenderNameTagFor((PlayerEntity)e, p_Event);
                });
    });

    private void RenderNameTagFor(PlayerEntity e, EventRenderGameOverlay p_Event)
    {
        Vec3d pos = MathUtil.interpolateEntity(e).add(0, e.getHeight() + 0.5f, 0);
        pos = TransformPositionUtil.worldSpaceToScreenSpace(new Vec3d(pos.x, pos.y, pos.z));
        Vector4d position = null;
        if (pos != null && pos.z > 0 && pos.z < 1) {
            if (position == null)
                position = new Vector4d(pos.x, pos.y, pos.z, 0);
            position.x = Math.min(pos.x, position.x);
            position.y = Math.min(pos.y, position.y);
            position.z = Math.max(pos.x, position.z);
        }
        if (position != null) {
            Vec2f renderer = new Vec2f((float) position.x, (float) position.y);
            String name = e.getName().getString();

            int color = -1;

            final Friend friend = FriendManager.Get().GetFriend(e);

            if (friend != null)
            {
                name = friend.GetAlias();
                color = 0x00C3EE;
            }

            final PlayerEntity player = (PlayerEntity) e;
            int responseTime = -1;

            if (Ping.getValue())
            {
                try
                {
                    responseTime = (int) MathUtil.clamp(
                            Objects.requireNonNull(Wrapper.GetMC().getNetworkHandler().getPlayerListEntry(player.getUuid()).getLatency()), 0,
                            300);
                }
                catch (NullPointerException np)
                {}
            }

            String l_Name = String.format("%s %sms %s", name, responseTime, Formatting.GREEN + String.valueOf(Math.floor(e.getHealth()+e.getAbsorptionAmount())));

            FontRenderers.getTwCenMtStd22().drawString(p_Event.getContext().getMatrices(), l_Name, renderer.x - FontRenderers.getTwCenMtStd22().getStringWidth(l_Name) / 2, renderer.y - 8 - 1, color);

            if (Armor.getValue())
            {
                final Iterator<ItemStack> items = e.getArmorItems().iterator();
                final ArrayList<ItemStack> stacks = new ArrayList<>();


                stacks.add(e.getOffHandStack());

                while (items.hasNext())
                {
                    final ItemStack stack = items.next();
                    if (stack != null && stack.getItem() != Items.AIR)
                    {
                        stacks.add(stack);
                    }
                }
                stacks.add(e.getMainHandStack());

                Collections.reverse(stacks);

                int x = 0;

                if (!e.getMainHandStack().isEmpty() && e.getMainHandStack().hasCustomName())
                {
                    l_Name = e.getMainHandStack().getName().getString();

                    FontRenderers.getTwCenMtStd15().drawString(p_Event.getContext().getMatrices(), l_Name, renderer.x - FontRenderers.getTwCenMtStd15().getStringWidth(l_Name) / 2, renderer.y - mc.textRenderer.fontHeight - 35, -1);
                }

                for (ItemStack stack : stacks)
                {
                    if (stack != null)
                    {
                        final Item item = stack.getItem();
                        if (item != Items.AIR)
                        {
                            p_Event.getContext().getMatrices().push();

                            p_Event.getContext().getMatrices().translate(renderer.x + x - (16 * stacks.size() / 2), renderer.y - mc.textRenderer.fontHeight - 19, 0);
                            p_Event.getContext().drawItem(stack, 0, 0);
                            p_Event.getContext().drawItemInSlot(mc.textRenderer,stack, 0, 0);
                            p_Event.getContext().getMatrices().pop();
                            x += 16;

                            //if (this.enchants.getValue())
                            {
                                final List<String> stringsToDraw = Lists.newArrayList();

                                if (stack.isDamaged())
                                {
                                    float l_ArmorPct = ((float)(stack.getMaxDamage()-stack.getDamage()) /  (float)stack.getMaxDamage())*100.0f;
                                    float l_ArmorBarPct = Math.min(l_ArmorPct, 100.0f);

                                    stringsToDraw.add(String.format("%s", (int)l_ArmorBarPct + "%"));
                                }
                                int y = 0;
                                if (stack.getEnchantments() != null)
                                {
                                    final NbtList tags = stack.getEnchantments();
                                    for (int i = 0; i < tags.size(); i++)
                                    {
                                        final NbtCompound tagCompound = tags.getCompound(i);
                                        if (tagCompound != null && Enchantment
                                                .byRawId(tagCompound.getByte("id")) != null)
                                        {
                                            final Enchantment enchantment = Enchantment
                                                    .byRawId(tagCompound.getShort("id"));
                                            final short lvl = tagCompound.getShort("lvl");
                                            if (enchantment != null)
                                            {
                                                String ench = "";
                                                if (enchantment.isCursed())
                                                {
                                                    ench = Formatting.RED
                                                            + enchantment.getName(lvl).getString()
                                                            .substring(11).substring(0, 2)
                                                            + Formatting.GRAY + lvl;
                                                }
                                                else
                                                {
                                                    ench = enchantment.getName(lvl).getString().substring(0,
                                                            2) + lvl;
                                                }
                                                stringsToDraw.add(ench);
                                            }
                                        }
                                    }
                                }

                                // Enchanted gapple
                                if (item == Items.GOLDEN_APPLE)
                                {
                                    if (stack.getDamage() == 1)
                                    {
                                        stringsToDraw.add(Formatting.DARK_RED + "God");
                                    }
                                }

                                for (String string : stringsToDraw)
                                {
                                    p_Event.getContext().getMatrices().push();
                                    p_Event.getContext().getMatrices().translate(
                                                    renderer.x + x
                                                            - ((16.0f * stacks.size()) / 2.0f)
                                                            - (16.0f / 2.0f)
                                                            - (FontRenderers.getTwCenMtStd22().getStringWidth(string)
                                                            / 4.0f),
                                                    renderer.y
                                                            - mc.textRenderer.fontHeight - 23 - y,
                                                    0);
                                    p_Event.getContext().getMatrices().scale(0.5f, 0.5f, 0.5f);
                                    FontRenderers.getTwCenMtStd22().drawString(p_Event.getContext().getMatrices(), string, 0, 0, -1);
                                    p_Event.getContext().getMatrices().scale(2, 2, 2);
                                    p_Event.getContext().getMatrices().pop();
                                    y += 4;
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private void RenderNameTagFor2(PlayerEntity e, EventRenderGameOverlay p_Event)
    {

    }

    @EventHandler
    private Listener<EventRenderEntityName> OnRenderEntityName = new Listener<>(p_Event ->
    {
        p_Event.cancel();
    });
}
