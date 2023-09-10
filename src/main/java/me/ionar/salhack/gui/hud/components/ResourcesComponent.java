package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.module.ui.HudModule;
import me.ionar.salhack.util.color.SalRainbowUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ResourcesComponent extends HudComponentItem {
    private final HudModule hud = (HudModule) SalHack.getModuleManager().getMod(HudModule.class);

    private final SalRainbowUtil Rainbow = new SalRainbowUtil(2);

    public final Value<Boolean> Totems = new Value<Boolean>("Totems", new String[]{ "Totems" }, "Include Totems", true);
    public final Value<Boolean> Crystals = new Value<Boolean>("Crystals", new String[]{ "Crystals" }, "Include Crystals", true);
    public final Value<Boolean> EXP = new Value<Boolean>("EXP", new String[]{ "EXP" }, "Include EXP", true);
    public final Value<Boolean> EGap = new Value<Boolean>("EGap", new String[]{ "EGap" }, "Include EGap", true);
    public ResourcesComponent() {
        super("Resources", 2, 53);
        setHidden(false);
    }

    @Override
    public void onRender(int mouseX, int mouseY, float partialTicks, DrawContext context) {
        super.onRender(mouseX, mouseY, partialTicks, context);

        int EGapCount = getItemCount(Items.ENCHANTED_GOLDEN_APPLE);
        int totemCount = getItemCount(Items.TOTEM_OF_UNDYING);
        int expCount = getItemCount(Items.EXPERIENCE_BOTTLE);
        int crystalCount = getItemCount(Items.END_CRYSTAL);

        final String EGapCount1 = "EGap: " + Formatting.WHITE + EGapCount;
        final String totemCount1 = "Totems: " + Formatting.WHITE + totemCount;
        final String crystals1 = "Crystals: " + Formatting.WHITE + crystalCount;
        final String expCount1 = "Exp: " + Formatting.WHITE + expCount;

        if (EGap.getValue()) {
            if (HudModule.CustomFont.getValue()) {
                FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), EGapCount1, (int) (getPositionX()), (int) (getPositionY()), hud.Rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor(), true);
            } else {
                context.drawTextWithShadow(mc.textRenderer, Text.of(EGapCount1), (int) getPositionX(), (int) getPositionY(), hud.Rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor());
            }
            Rainbow.onRender();
            setWidth(mc.textRenderer.getWidth(EGapCount1));
            setHeight(mc.textRenderer.fontHeight + 30);
        }
        if (Totems.getValue()) {
            if (HudModule.CustomFont.getValue()) {
                FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), totemCount1, (int) (getPositionX()), (int) (getPositionY()) + 30, hud.Rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor(), true);
            } else {
                context.drawTextWithShadow(mc.textRenderer, Text.of(totemCount1), (int) getPositionX(), (int) getPositionY() + 30, hud.Rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor());
            }
        }
        if (Crystals.getValue()) {
            if (HudModule.CustomFont.getValue()) {
                FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), crystals1, (int) (getPositionX()), (int) (getPositionY()) + 10, hud.Rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor(), true);
            } else {
                context.drawTextWithShadow(mc.textRenderer, Text.of(crystals1), (int) getPositionX(), (int) getPositionY() + 10, hud.Rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor());
            }
        }
        if (EXP.getValue()) {
            if (HudModule.CustomFont.getValue()) {
                FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), expCount1, (int) (getPositionX()), (int) (getPositionY()) + 20, hud.Rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor(), true);
            } else {
                context.drawTextWithShadow(mc.textRenderer, Text.of(expCount1), (int) getPositionX(), (int) getPositionY() + 20, hud.Rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor());
            }
        }
    }

    public int getItemCount(Item item) {
        if (mc.player != null) {
            int n = 0;
            int n2 = 44;
            for (int i = 0; i <= n2; ++i) {
                ItemStack itemStack = mc.player.getInventory().getStack(i);
                if (itemStack.getItem() != item) continue;
                n += itemStack.getCount();
            }
            return n;
        }
        return 0;
    }
}