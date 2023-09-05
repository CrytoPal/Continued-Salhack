package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.module.ui.HudModule;
import me.ionar.salhack.util.color.SalRainbowUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TrueDurabilityComponent extends HudComponentItem {

    private final HudModule hud = (HudModule) ModuleManager.Get().GetMod(HudModule.class);

    private final SalRainbowUtil Rainbow = new SalRainbowUtil(9);
    public TrueDurabilityComponent() {
        super("TrueDurability", 2, 113);
        SetHidden(false);
    }


    private String durability;

    @Override
    public void render(int mouseX, int mouseY, float partialTicks, DrawContext context) {
        super.render(mouseX, mouseY, partialTicks, context);

        ItemStack stack = mc.player.getMainHandStack();

        if (!stack.isEmpty() && (stack.getItem() instanceof ToolItem || stack.getItem() instanceof ArmorItem || stack.getItem() instanceof SwordItem)) {
            durability = "Durability: " + Formatting.GREEN + (stack.getMaxDamage() - stack.getDamage());

        } else {
            durability = "Durability:";
        }

        if (HudModule.CustomFont.getValue()) {
            FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), durability, (int) (GetX()), (int) (GetY()), hud.Rainbow.getValue() ? Rainbow.GetRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor(), true);
        } else {
            context.drawTextWithShadow(mc.textRenderer, Text.of(durability), (int) GetX(), (int) GetY(), hud.Rainbow.getValue() ? Rainbow.GetRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor());
        }
        Rainbow.OnRender();
        SetWidth(Wrapper.GetMC().textRenderer.getWidth(durability));
        SetHeight(Wrapper.GetMC().textRenderer.fontHeight);
    }
}