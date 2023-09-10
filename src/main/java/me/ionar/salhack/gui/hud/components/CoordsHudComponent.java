package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.module.ui.HudModule;
import me.ionar.salhack.module.world.CoordsSpooferModule;
import me.ionar.salhack.util.color.SalRainbowUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.text.DecimalFormat;


public class CoordsHudComponent extends HudComponentItem {

    public final Value<Modes> Mode = new Value<Modes>("Mode", new String[]
            {"Mode"}, "Mode of displaying coordinates", Modes.Inline);

    public final Value<Boolean> NetherCords = new Value<Boolean>("Nether Cords", new String[]{ "NC" }, "Include Nether Cords", true);
    public final Value<Boolean> OverWorldCoords = new Value<Boolean>("Over World Cords", new String[]{ "NC" }, "Include Over World Cords (In nether)", true);
    private final HudModule hud = (HudModule) SalHack.getModuleManager().getMod(HudModule.class);

    private final SalRainbowUtil Rainbow = new SalRainbowUtil(9);

    final DecimalFormat Formatter = new DecimalFormat("#.#");

    private final CoordsSpooferModule _getCoords = (CoordsSpooferModule) SalHack.getModuleManager().getMod(CoordsSpooferModule.class);

    private static String coords;
    public CoordsHudComponent() {
        super("Coords", 3, 517);
        setHidden(false);
    }

    public String format(double input) {
        String result = Formatter.format(input);

        if (!result.contains("."))
            result += ".0";

        return result;
    }

    @Override
    public void onRender(int p_MouseX, int p_MouseY, float p_PartialTicks, DrawContext context) {
        super.onRender(p_MouseX, p_MouseY, p_PartialTicks, context);

        if (NetherCords.getValue()) {
            coords = "XYZ: " + Formatting.WHITE + format(getPositionX()) + " , " + format(getPositionY()) + " , " + format(getZ()) + " (" + format(NethergetX()) + ", " + format(NethergetZ()) + ")";
        } else {
            coords = "XYZ: " + Formatting.WHITE + format(getPositionX()) + " , " + format(getPositionY()) + " , " + format(getZ());
        }
        if (mc.world.getDimension().respawnAnchorWorks()) {
            if (OverWorldCoords.getValue()) {
                coords = "XYZ: " + Formatting.WHITE + format(NethergetX()) + " , " + format(getPositionY()) + " , " + format(NethergetZ()) + " (" + format(getPositionX()) + ", " + format(getZ()) + ")";
            } else {
                coords = "XYZ: " + Formatting.WHITE + format(NethergetX()) + " , " + format(getPositionY()) + " , " + format(NethergetZ());
            }

        }

        switch (Mode.getValue()) {

            case Inline:
                if (HudModule.customFont.getValue()) {
                    FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), coords, (int) (this.getPositionX()), (int) (this.getPositionY()), hud.rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : getTextColor(), true);
                } else {
                    context.drawTextWithShadow(mc.textRenderer, Text.of(coords), (int) this.getPositionX(), (int) this.getPositionY(), hud.rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : getTextColor());
                }
                Rainbow.onRender();
                setWidth(Wrapper.GetMC().textRenderer.getWidth(coords));
                setHeight(Wrapper.GetMC().textRenderer.fontHeight);

                break;
        }

    }

    private Boolean getCoordSpoofer() {
        return _getCoords != null && _getCoords.isEnabled();
    }
    private Boolean GetRandom() {
        return _getCoords != null && _getCoords.random.getValue();
    }



    private int randX() {
        int i = (int) (Math.random() * 2) + 1;
        if (i == 1) {
            i = (int) ((Math.random() * 30000000) + 0) * -1;
        } else {
            i = (int) ((Math.random() * 30000000) + 0);
        }
        return i;
    }

    private int randZ() {
        int i = (int) (Math.random() * 2) + 1;
        if (i == 1) {
            i = (int) ((Math.random() * 30000000) + 0) * -1;
        } else {
            i = (int) ((Math.random() * 30000000) + 0);
        }
        return i;
    }

    private double getX() {
        if (mc.world.getDimension().respawnAnchorWorks()) {
            if (getCoordSpoofer()) {
                if (GetRandom()) {
                    return mc.player.getX() * 8 + randX() * 8;
                }
                if (!GetRandom() && _getCoords != null) {
                    return mc.player.getX() * 8 + _getCoords.coordsX.getValue() * 8 + _getCoords.coordsNegativeX.getValue() * 8;
                }
            }
            return mc.player.getX() * 8;
        } else {
            if (getCoordSpoofer()) {
                if (GetRandom()) {
                    return mc.player.getX() + randX();
                }
                if (!GetRandom() && _getCoords != null) {
                    return mc.player.getX() + _getCoords.coordsX.getValue() + _getCoords.coordsNegativeX.getValue();
                }
            }
            return mc.player.getX();
        }
    }

    private double NethergetX() {
        if (mc.world.getDimension().respawnAnchorWorks()) {
            if (getCoordSpoofer()) {
                if (GetRandom()) {
                    return mc.player.getX() + randX();
                }
                if (!GetRandom() && _getCoords != null) {
                    return mc.player.getX() + _getCoords.coordsX.getValue() + _getCoords.coordsNegativeX.getValue();
                }
            }
            return mc.player.getX();
        } else {
            if (getCoordSpoofer()) {
                if (GetRandom()) {
                    return mc.player.getX() / 8 + randX() / 8;
                }
                if (!GetRandom() && _getCoords != null) {
                    return mc.player.getX() / 8 + _getCoords.coordsX.getValue() / 8 + _getCoords.coordsNegativeX.getValue() / 8;
                }
            }
            return mc.player.getX() / 8;
        }
    }

    private double NethergetZ() {
        if (mc.world.getDimension().respawnAnchorWorks()) {
            if (getCoordSpoofer()) {
                if (GetRandom()) {
                    return mc.player.getZ() + randZ();
                }
                if (!GetRandom() && _getCoords != null) {
                    return mc.player.getZ() + _getCoords.coordsZ.getValue() + _getCoords.coordsNegativeZ.getValue();
                }
            }
            return mc.player.getZ();
        } else {
            if (getCoordSpoofer()) {
                if (GetRandom()) {
                    return mc.player.getZ() / 8 + randZ() / 8;
                }
                if (!GetRandom() && _getCoords != null) {
                    return mc.player.getZ() / 8 + _getCoords.coordsZ.getValue() / 8 + _getCoords.coordsNegativeZ.getValue() / 8;
                }
            }
            return mc.player.getZ() / 8;
        }
    }

    private double getZ() {
        if (mc.world.getDimension().respawnAnchorWorks()) {
            if (getCoordSpoofer()) {
                if (GetRandom()) {
                    return mc.player.getZ() * 8 + randZ() * 8;
                }
                if (!GetRandom() && _getCoords != null) {
                    return mc.player.getZ() * 8 + _getCoords.coordsZ.getValue() * 8 + _getCoords.coordsNegativeZ.getValue() * 8;
                }
            }
            return mc.player.getZ() * 8;
        } else {
            if (getCoordSpoofer()) {
                if (GetRandom()) {
                    return mc.player.getZ() + randZ();
                }
                if (!GetRandom() && _getCoords != null) {
                    return mc.player.getZ() + _getCoords.coordsZ.getValue() + _getCoords.coordsNegativeZ.getValue();
                }
            }
            return mc.player.getZ();
        }
    }

    private double getY() {
        return  mc.player.getY();
    }

    public enum Modes {
        Inline
    }
}
