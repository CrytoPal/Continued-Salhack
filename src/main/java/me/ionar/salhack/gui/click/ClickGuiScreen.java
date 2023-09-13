package me.ionar.salhack.gui.click;

import com.mojang.blaze3d.systems.RenderSystem;
import me.ionar.salhack.gui.SalGuiScreen;
import me.ionar.salhack.gui.click.component.MenuComponent;
import me.ionar.salhack.gui.click.component.menus.mods.MenuComponentModList;
import me.ionar.salhack.gui.click.component.menus.mods.menuComponentPresetsList;
import me.ionar.salhack.gui.click.effects.Snow;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.module.Module.ModuleType;
import me.ionar.salhack.module.ui.ClickGuiModule;
import me.ionar.salhack.module.ui.ColorsModule;
import me.ionar.salhack.util.imgs.SalDynamicTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClickGuiScreen extends SalGuiScreen {
    private final ClickGuiModule clickGuiModule;
    private final ArrayList<MenuComponent> menuComponents = new ArrayList<>();
    private final SalDynamicTexture watermark = SalHack.getImageManager().getDynamicTexture("watermark");
    //private SalDynamicTexture BlueBlur = ImageManager.Get().GetDynamicTexture("BlueBlur");
    private final ArrayList<Snow> snows = new ArrayList<>();
    private float offsetY = 0;
    private final MinecraftClient mc = Wrapper.GetMC();

    public ClickGuiScreen(ClickGuiModule guiModule, ColorsModule colors) {
        // COMBAT, EXPLOIT, MOVEMENT, RENDER, WORLD, MISC, HIDDEN, UI
        menuComponents.add(new MenuComponentModList("Combat", ModuleType.COMBAT, 10, 3, "shield", colors, guiModule));
        menuComponents.add(new MenuComponentModList("Exploit", ModuleType.EXPLOIT, 120, 3, "skull", colors, guiModule));
        // MenuComponents.add(new MenuComponentModList("Hidden", ModuleType.HIDDEN, 320,
        // 3));
        menuComponents.add(new MenuComponentModList("Misc", ModuleType.MISC, 230, 3, "questionmark", colors, guiModule));
        menuComponents.add(new MenuComponentModList("Movement", ModuleType.MOVEMENT, 340, 3, "arrow", colors, guiModule));
        menuComponents.add(new MenuComponentModList("Render", ModuleType.RENDER, 450, 3, "eye", colors, guiModule));
        menuComponents.add(new MenuComponentModList("UI", ModuleType.UI, 560, 3, "mouse", colors, guiModule));
        menuComponents.add(new MenuComponentModList("World", ModuleType.WORLD, 670, 3, "blockimg", colors, guiModule));
        //   MenuComponents.add(new MenuComponentModList("Bot", ModuleType.BOT, 780, 3, "robotimg", colors));
        menuComponents.add(new MenuComponentModList("Litematica", ModuleType.LITEMATICA, 10, 203, "robotimg", colors, guiModule));
        menuComponentPresetsList presets;
        menuComponents.add(presets = new menuComponentPresetsList("Presets", 120, 203, "robotimg", colors, guiModule));
        SalHack.getPresetsManager().initializeGUIComponent(presets);
        clickGuiModule = guiModule;

        /// Load settings
        for (MenuComponent menuComponent : menuComponents) {
            File exists = new File("SalHack/GUI/" + menuComponent.getDisplayName() + ".json");
            if (!exists.exists()) continue;
            String content = SalHack.getFilesManager().read(exists.getPath());
            Map<?, ?> map = SalHack.gson.fromJson(content, Map.class);
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                if (key.equals("PosX")) menuComponent.setX(Float.parseFloat(value));
                else if (key.equals("PosY")) menuComponent.setY(Float.parseFloat(value));
            }
        }
        for (int i = 0; i < 100; ++i) {
            for (int y = 0; y < 3; ++y) {
                Snow snow = new Snow(25 * i, y * -50, SalHack.random.nextInt(3) + 1, SalHack.random.nextInt(2)+1);
                snows.add(snow);
            }
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (MenuComponent menuComponent : menuComponents) if (menuComponent.mouseClicked((int) mouseX, (int) mouseY, button, offsetY)) break;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (MenuComponent menuComponent : menuComponents) menuComponent.mouseReleased((int) mouseX, (int) mouseY);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (MenuComponent menuComponent : menuComponents) menuComponent.mouseClickMove((int) mouseX, (int) mouseY, button);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    public static void drawTexture(Identifier icon, float x, float y, int width, int height, DrawContext context) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        RenderSystem.texParameter(3553, 10240, 9729);
        RenderSystem.texParameter(3553, 10241, 9987);
        context.drawTexture(icon, 0, 0, 0.0F, 0.0F, width, height, width, height);
        context.getMatrices().pop();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        final Window res = mc.getWindow();
        if (!snows.isEmpty() && clickGuiModule.snowing.getValue()) snows.forEach(snow -> snow.update(res, context));
        if (watermark != null && clickGuiModule.watermark.getValue()) drawTexture(new Identifier(watermark.getResourceLocation()), 0, res.getScaledHeight() - watermark.getHeight() - 5, watermark.getWidth() / 2, watermark.getHeight() / 2, context);
        MenuComponent lastHovered = null;
        for (MenuComponent menuComponent : menuComponents) if (menuComponent.render(mouseX, mouseY, true, allowsOverflow(), offsetY, context)) lastHovered = menuComponent;
        if (lastHovered != null) {
            menuComponents.remove(lastHovered);
            menuComponents.add(lastHovered);
        }
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (amount > 0) offsetY = Math.max(0, offsetY -1);
        else if (amount < 0) offsetY = Math.min(100, offsetY + 1);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (MenuComponent menuComponent : menuComponents) menuComponent.keyTyped(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        super.close();
        if (clickGuiModule.isEnabled()) clickGuiModule.toggle(true);
        for (MenuComponent menuComponent : menuComponents) {
            Map<String, String> map = new HashMap<>();
            map.put("PosX", String.valueOf(menuComponent.getX()));
            map.put("PosY", String.valueOf(menuComponent.getY()));
            SalHack.getFilesManager().write("SalHack/GUI/" + menuComponent.getDisplayName() + ".json", SalHack.gson.toJson(map, Map.class));
        }
    }

    public boolean allowsOverflow() {
        return clickGuiModule.allowOverflow.getValue();
    }

    public void resetToDefaults() {
        menuComponents.forEach(MenuComponent::reset);
    }
}

