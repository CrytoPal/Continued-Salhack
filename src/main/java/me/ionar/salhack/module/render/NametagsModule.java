package me.ionar.salhack.module.render;

import io.github.racoondog.norbit.EventHandler;
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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.List;

import com.google.common.collect.Lists;
import org.joml.Vector4d;

public class NametagsModule extends Module {
    public final Value<Boolean> Armor = new Value<>("Armor", new String[]{""}, "", true);
    public final Value<Boolean> Durability = new Value<>("Durability", new String[]{""}, "", true);
    public final Value<Boolean> ItemName = new Value<>("ItemName", new String[]{""}, "", true);
    public final Value<Boolean> Health = new Value<>("Health", new String[]{""}, "", true);
    public final Value<Boolean> Invisibles = new Value<>("Invisibles", new String[]{""}, "", false);
    public final Value<Boolean> EntityID = new Value<>("EntityID", new String[]{""}, "", false);
    public final Value<Boolean> GameMode = new Value<>("GameMode", new String[]{""}, "", false);
    public final Value<Boolean> Ping = new Value<>("Ping", new String[]{""}, "", true);
    Entity camera = mc.getCameraEntity();
    public NametagsModule() {
        super("NameTags", new String[]{ "Nametag" }, "Improves nametags of players around you", 0, -1, ModuleType.RENDER);
    }

    @EventHandler
    private void OnRenderGameOverlay(EventRenderGameOverlay event) {
        EntityUtil.getEntities().stream().filter(EntityUtil::isLiving).filter(entity -> !EntityUtil.isFakeLocalPlayer(entity))
                .filter(entity -> (entity instanceof PlayerEntity && mc.player != entity)).forEach(e ->
                        RenderNameTagFor((PlayerEntity) e, event));
    }

    private void RenderNameTagFor(PlayerEntity entity, EventRenderGameOverlay Event) {
        DrawContext context = Event.getContext();
        Vec3d pos = MathUtil.interpolateEntity(entity).add(0, entity.getHeight() + 0.5f, 0);
        pos = TransformPositionUtil.worldSpaceToScreenSpace(new Vec3d(pos.x, pos.y, pos.z));
        Vector4d position = null;
        if (pos.z > 0 && pos.z < 1) {
            position = new Vector4d(pos.x, pos.y, pos.z, 0);
            position.x = Math.min(pos.x, position.x);
            position.y = Math.min(pos.y, position.y);
            position.z = Math.max(pos.x, position.z);
        }
        if (position != null) {
            Vec2f renderer = new Vec2f((float) position.x, (float) position.y);
            String name = entity.getEntityName();

            int color = -1;

            final Friend friend = FriendManager.Get().GetFriend(entity);

            if (friend != null) {
                name = entity.getEntityName();
                color = 0x00C3EE;
            }

            String Name = getName(entity, name);

            FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), Name, renderer.x - FontRenderers.getTwCenMtStd22().getStringWidth(Name) / 2, renderer.y - 8 - 1, color);

            if (Armor.getValue()) {
                final Iterator<ItemStack> items = entity.getArmorItems().iterator();
                final ArrayList<ItemStack> stacks = new ArrayList<>();

                stacks.add(entity.getOffHandStack());

                while (items.hasNext()) {
                    final ItemStack stack = items.next();
                    if (stack != null && stack.getItem() != Items.AIR) stacks.add(stack);
                }
                stacks.add(entity.getMainHandStack());

                Collections.reverse(stacks);

                int x = 0;

                if (!entity.getMainHandStack().isEmpty() && entity.getMainHandStack().hasCustomName()) {
                    Name = entity.getMainHandStack().getName().getString();
                    FontRenderers.getTwCenMtStd15().drawString(context.getMatrices(), Name, renderer.x - FontRenderers.getTwCenMtStd15().getStringWidth(Name) / 2, renderer.y - mc.textRenderer.fontHeight - 35, -1);
                }

                for (ItemStack stack : stacks) {
                    if (stack != null) {
                        final Item item = stack.getItem();
                        if (item != Items.AIR) {
                            context.getMatrices().push();
                            context.getMatrices().translate(renderer.x + x - ((float) (16 * stacks.size()) / 2), renderer.y - mc.textRenderer.fontHeight - 19, 0);
                            context.drawItem(stack, 0, 0);
                            context.drawItemInSlot(mc.textRenderer,stack, 0, 0);
                            context.getMatrices().pop();
                            x += 16;

                            //if (this.enchants.getValue())
                            {
                                final List<String> stringsToDraw = Lists.newArrayList();

                                if (stack.isDamaged()) {
                                    float ArmorPercent = ((float)(stack.getMaxDamage()-stack.getDamage()) /  (float)stack.getMaxDamage())*100.0f;
                                    float ArmorBarPercent = Math.min(ArmorPercent, 100.0f);
                                    stringsToDraw.add(String.format("%s", (int)ArmorBarPercent + "%"));
                                }
                                int y = 0;
                                if (stack.getEnchantments() != null) {
                                    final NbtList tags = stack.getEnchantments();
                                    for (int i = 0; i < tags.size(); i++) {
                                        final NbtCompound tagCompound = tags.getCompound(i);
                                        if (tagCompound != null && Enchantment.byRawId(tagCompound.getByte("id")) != null) {
                                            final Enchantment enchantment = Enchantment.byRawId(tagCompound.getShort("id"));
                                            final short lvl = tagCompound.getShort("lvl");
                                            if (enchantment != null) {
                                                String enchant;
                                                if (enchantment.isCursed()) enchant = Formatting.RED + enchantment.getName(lvl).getString().substring(11).substring(0, 2) + Formatting.GRAY + lvl;
                                                else enchant = enchantment.getName(lvl).getString().substring(0, 2) + lvl;
                                                stringsToDraw.add(enchant);
                                            }
                                        }
                                    }
                                }

                                // Enchanted gapple
                                if (item == Items.GOLDEN_APPLE && stack.getDamage() == 1) stringsToDraw.add(Formatting.DARK_RED + "God");
                                for (String string : stringsToDraw) {
                                    context.getMatrices().push();
                                    context.getMatrices().translate(renderer.x + x - ((16.0f * stacks.size()) / 2.0f) - (16.0f / 2.0f) - (FontRenderers.getTwCenMtStd22().getStringWidth(string) / 4.0f), renderer.y - mc.textRenderer.fontHeight - 23 - y, 0);
                                    context.getMatrices().scale(0.5f, 0.5f, 0.5f);
                                    FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), string, 0, 0, -1);
                                    context.getMatrices().scale(2, 2, 2);
                                    context.getMatrices().pop();
                                    y += 4;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private String getName(PlayerEntity entity, String name) {
        int responseTime = -1;

        ClientPlayNetworkHandler handler = Wrapper.GetMC().getNetworkHandler();
        if (Ping.getValue() && handler != null) {
            PlayerListEntry entry = handler.getPlayerListEntry(entity.getUuid());
            if (entry != null) responseTime = MathHelper.clamp(entry.getLatency(), 0, 300);
        }

        return String.format("%s %sms %s", name, responseTime, Formatting.GREEN + String.valueOf(Math.floor(entity.getHealth()+ entity.getAbsorptionAmount())));
    }


    //private void RenderNameTagFor2(PlayerEntity e, EventRenderGameOverlay p_Event) {}
}
