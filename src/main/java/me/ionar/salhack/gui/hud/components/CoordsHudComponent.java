package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.module.ui.HudModule;
import me.ionar.salhack.module.world.CoordsSpoofer;
import me.ionar.salhack.util.color.SalRainbowUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.text.DecimalFormat;


public class CoordsHudComponent extends HudComponentItem {
    public final Value<Modes> Mode = new Value<>("Mode", new String[]{"Mode"}, "Mode of displaying coordinates", Modes.Inline);
    public final Value<Boolean> netherCoords = new Value<>("Nether Cords", new String[]{"NC"}, "Include Nether Cords", true);
    public final Value<Boolean> overWorldCoords = new Value<>("Over World Cords", new String[]{"NC"}, "Include Over World Cords (In nether)", true);
    private final HudModule hud = (HudModule) SalHack.getModuleManager().getMod(HudModule.class);
    private final SalRainbowUtil rainbow = new SalRainbowUtil(9);
    final DecimalFormat formatter = new DecimalFormat("#.#");
    private final CoordsSpoofer coordsSpoofer = (CoordsSpoofer) SalHack.getModuleManager().getMod(CoordsSpoofer.class);

    public CoordsHudComponent() {
        super("Coords", 3, 517);
        setHidden(false);
    }

    public String format(double input) {
        String result = formatter.format(input);
        if (!result.contains(".")) result += ".0";
        return result;
    }

    @Override
    public void onRender(int mouseX, int mouseY, float partialTicks, DrawContext context) {
        super.onRender(mouseX, mouseY, partialTicks, context);
        if (mc.world == null) return;
        String coords;
        if (netherCoords.getValue()) coords = "XYZ: " + Formatting.WHITE + format(getPositionX()) + " , " + format(getPositionY()) + " , " + format(getZ()) + " (" + format(netherGetX()) + ", " + format(netherGetZ()) + ")";
        else coords = "XYZ: " + Formatting.WHITE + format(getPositionX()) + " , " + format(getPositionY()) + " , " + format(getZ());
        if (mc.world.getDimension().respawnAnchorWorks()) {
            if (overWorldCoords.getValue()) coords = "XYZ: " + Formatting.WHITE + format(netherGetX()) + " , " + format(getPositionY()) + " , " + format(netherGetZ()) + " (" + format(getPositionX()) + ", " + format(getZ()) + ")";
            else coords = "XYZ: " + Formatting.WHITE + format(netherGetX()) + " , " + format(getPositionY()) + " , " + format(netherGetZ());
        }
        if (Mode.getValue() == Modes.Inline) {
            if (HudModule.customFont.getValue()) FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), coords, (int) (this.getPositionX()), (int) (this.getPositionY()), hud.rainbow.getValue() ? rainbow.getRainbowColorAt(rainbow.getRainbowColorNumber()) : getTextColor(), true);
            else context.drawTextWithShadow(mc.textRenderer, Text.of(coords), (int) this.getPositionX(), (int) this.getPositionY(), hud.rainbow.getValue() ? rainbow.getRainbowColorAt(rainbow.getRainbowColorNumber()) : getTextColor());
            rainbow.onRender();
            setWidth(mc.textRenderer.getWidth(coords));
            setHeight(mc.textRenderer.fontHeight);
        }

    }

    private Boolean getCoordSpoofer() {
        return coordsSpoofer != null && coordsSpoofer.isEnabled();
    }
    private Boolean getRandom() {
        return coordsSpoofer != null && coordsSpoofer.random.getValue();
    }



    private int randX() {
        int i = (int) (Math.random() * 2) + 1;
        if (i == 1) i = (int) ((Math.random() * 30000000) + 0) * -1;
        else i = (int) ((Math.random() * 30000000) + 0);
        return i;
    }

    private int randZ() {
        int i = (int) (Math.random() * 2) + 1;
        if (i == 1) i = (int) ((Math.random() * 30000000) + 0) * -1;
        else i = (int) ((Math.random() * 30000000) + 0);
        return i;
    }

    private double getX() {
        if (mc.world == null || mc.player == null) return -1.0;
        if (mc.world.getDimension().respawnAnchorWorks()) {
            if (getCoordSpoofer()) {
                if (getRandom()) return mc.player.getX() * 8 + randX() * 8;
                if (!getRandom() && coordsSpoofer != null) return mc.player.getX() * 8 + coordsSpoofer.coordsX.getValue() * 8 + coordsSpoofer.coordsNegativeX.getValue() * 8;
            }
            return mc.player.getX() * 8;
        } else {
            if (getCoordSpoofer()) {
                if (getRandom()) return mc.player.getX() + randX();
                if (!getRandom() && coordsSpoofer != null) return mc.player.getX() + coordsSpoofer.coordsX.getValue() + coordsSpoofer.coordsNegativeX.getValue();
            }
            return mc.player.getX();
        }
    }

    private double netherGetX() {
        if (mc.world == null || mc.player == null) return -1.0;
        if (mc.world.getDimension().respawnAnchorWorks()) {
            if (getCoordSpoofer()) {
                if (getRandom()) return mc.player.getX() + randX();
                if (!getRandom() && coordsSpoofer != null) return mc.player.getX() + coordsSpoofer.coordsX.getValue() + coordsSpoofer.coordsNegativeX.getValue();
            }
            return mc.player.getX();
        } else {
            if (getCoordSpoofer()) {
                if (getRandom()) return mc.player.getX() / 8 + (double) randX() / 8;
                if (!getRandom() && coordsSpoofer != null) return mc.player.getX() / 8 + (double) coordsSpoofer.coordsX.getValue() / 8 + (double) coordsSpoofer.coordsNegativeX.getValue() / 8;
            }
            return mc.player.getX() / 8;
        }
    }

    private double netherGetZ() {
        if (mc.world == null || mc.player == null) return -1.0;
        if (mc.world.getDimension().respawnAnchorWorks()) {
            if (getCoordSpoofer()) {
                if (getRandom()) return mc.player.getZ() + randZ();
                if (!getRandom() && coordsSpoofer != null) return mc.player.getZ() + coordsSpoofer.coordsZ.getValue() + coordsSpoofer.coordsNegativeZ.getValue();
            }
            return mc.player.getZ();
        } else {
            if (getCoordSpoofer()) {
                if (getRandom()) return mc.player.getZ() / 8 + (double) randZ() / 8;
                if (!getRandom() && coordsSpoofer != null) return mc.player.getZ() / 8 + (double) coordsSpoofer.coordsZ.getValue() / 8 + (double) coordsSpoofer.coordsNegativeZ.getValue() / 8;
            }
            return mc.player.getZ() / 8;
        }
    }

    private double getZ() {
        if (mc.world == null || mc.player == null) return -1.0;
        if (mc.world.getDimension().respawnAnchorWorks()) {
            if (getCoordSpoofer()) {
                if (getRandom()) return mc.player.getZ() * 8 + randZ() * 8;
                if (!getRandom() && coordsSpoofer != null) return mc.player.getZ() * 8 + coordsSpoofer.coordsZ.getValue() * 8 + coordsSpoofer.coordsNegativeZ.getValue() * 8;
            }
            return mc.player.getZ() * 8;
        } else {
            if (getCoordSpoofer()) {
                if (getRandom()) return mc.player.getZ() + randZ();
                if (!getRandom() && coordsSpoofer != null) return mc.player.getZ() + coordsSpoofer.coordsZ.getValue() + coordsSpoofer.coordsNegativeZ.getValue();
            }
            return mc.player.getZ();
        }
    }

    private double getY() {
        if (mc.player == null) return -1.0;
        return  mc.player.getY();
    }

    public enum Modes {
        Inline
    }
}
