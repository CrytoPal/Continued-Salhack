package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.managers.ModuleManager;
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

    private final HudModule hud = (HudModule) ModuleManager.Get().GetMod(HudModule.class);

    private final SalRainbowUtil Rainbow = new SalRainbowUtil(9);
    public BiomeComponent() {
        super("Biome", 2, 123);
    }

    @Override
    public void render(int p_MouseX, int p_MouseY, float p_PartialTicks, DrawContext context) {
        super.render(p_MouseX, p_MouseY, p_PartialTicks, context);
        if (mc.world != null) {

            if (mc.player != null && mc.world != null) {
                BLOCK_POS.set(mc.player.getX(), mc.player.getY(), mc.player.getZ());
                Identifier id = mc.world.getRegistryManager().get(RegistryKeys.BIOME).getId(mc.world.getBiome(BLOCK_POS).value());

                final String biome = "Biome: " + Formatting.WHITE + Arrays.stream(id.getPath().split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));

                SetWidth(mc.textRenderer.getWidth(biome));
                SetHeight(mc.textRenderer.fontHeight);

                if (HudModule.CustomFont.getValue()) {
                    FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), biome, (int) (GetX()), (int) (GetY()), hud.Rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor(), true);
                } else {
                    context.drawTextWithShadow(mc.textRenderer, biome, (int) GetX(), (int) GetY(), hud.Rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor());
                }
            }
        }
    }
}