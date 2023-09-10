package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.main.Wrapper;
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
    private static final BlockPos.Mutable BLOCK_POS = new BlockPos.Mutable();

    private final HudModule hud = (HudModule) SalHack.getModuleManager().getMod(HudModule.class);

    private final SalRainbowUtil Rainbow = new SalRainbowUtil(9);
    public BiomeComponent() {
        super("Biome", 2, 123);
    }

    @Override
    public void onRender(int p_MouseX, int p_MouseY, float p_PartialTicks, DrawContext context) {
        super.onRender(p_MouseX, p_MouseY, p_PartialTicks, context);
        if (mc.world != null) {

            if (Wrapper.GetMC().player != null && Wrapper.GetMC().world != null) {
                BLOCK_POS.set(Wrapper.GetMC().player.getX(), Wrapper.GetMC().player.getY(), Wrapper.GetMC().player.getZ());
                Identifier id = Wrapper.GetMC().world.getRegistryManager().get(RegistryKeys.BIOME).getId(Wrapper.GetMC().world.getBiome(BLOCK_POS).value());

                final String biome = "Biome: " + Formatting.WHITE + Arrays.stream(id.getPath().split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));

                setWidth(Wrapper.GetMC().textRenderer.getWidth(biome));
                setHeight(Wrapper.GetMC().textRenderer.fontHeight);

                if (HudModule.customFont.getValue()) {
                    FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), biome, (int) (getPositionX()), (int) (getPositionY()), hud.rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : getTextColor(), true);
                } else {
                    context.drawTextWithShadow(mc.textRenderer, biome, (int) getPositionX(), (int) getPositionY(), hud.rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : getTextColor());
                }
            }
        }
    }
}