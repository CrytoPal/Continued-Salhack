package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.module.ui.HudModule;
import me.ionar.salhack.util.color.SalRainbowUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class BiomeComponent extends HudComponentItem {
    private static final BlockPos.Mutable blockPos = new BlockPos.Mutable();
    private final HudModule hud = (HudModule) SalHack.getModuleManager().getMod(HudModule.class);
    private final SalRainbowUtil rainbow = new SalRainbowUtil(9);
    public BiomeComponent() {
        super("Biome", 2, 123);
    }

    @Override
    public void onRender(int mouseX, int mouseY, float partialTicks, DrawContext context) {
        super.onRender(mouseX, mouseY, partialTicks, context);
        if (mc.world != null) {
            if (mc.player != null) {
                blockPos.set(mc.player.getX(), mc.player.getY(), mc.player.getZ());
                Identifier id = mc.world.getRegistryManager().get(RegistryKeys.BIOME).getId(mc.world.getBiome(blockPos).value());
                if (id != null) {
                    final String biome = "Biome: " + Formatting.WHITE + Arrays.stream(id.getPath().split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
                    setWidth(mc.textRenderer.getWidth(biome));
                    setHeight(mc.textRenderer.fontHeight);
                    if (HudModule.customFont.getValue()) FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), biome, (int) (getPositionX()), (int) (getPositionY()), hud.rainbow.getValue() ? rainbow.getRainbowColorAt(rainbow.getRainbowColorNumber()) : getTextColor(), true);
                    else context.drawTextWithShadow(mc.textRenderer, biome, (int) getPositionX(), (int) getPositionY(), hud.rainbow.getValue() ? rainbow.getRainbowColorAt(rainbow.getRainbowColorNumber()) : getTextColor());
                }
            }
        }
    }
}