package me.ionar.salhack.gui.click;

import com.mojang.blaze3d.systems.RenderSystem;
import me.ionar.salhack.gui.click.component.MenuComponent;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.managers.ImageManager;
import me.ionar.salhack.util.imgs.SalDynamicTexture;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;


import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.ionar.salhack.gui.SalGuiScreen;
import me.ionar.salhack.gui.click.component.menus.mods.MenuComponentModList;
import me.ionar.salhack.gui.click.component.menus.mods.MenuComponentPresetsList;
import me.ionar.salhack.gui.click.effects.Snow;
import me.ionar.salhack.managers.PresetsManager;
import me.ionar.salhack.module.Module.ModuleType;
import me.ionar.salhack.module.ui.ClickGuiModule;
import me.ionar.salhack.module.ui.ColorsModule;
import net.minecraft.util.Identifier;

public class ClickGuiScreen extends SalGuiScreen
{
    private ArrayList<MenuComponent> MenuComponents = new ArrayList<MenuComponent>();
    private SalDynamicTexture Watermark = ImageManager.Get().GetDynamicTexture("watermark");
    //private SalDynamicTexture BlueBlur = ImageManager.Get().GetDynamicTexture("BlueBlur");
    private ArrayList<Snow> _snowList = new ArrayList<Snow>();

    private float OffsetY = 0;

    public ClickGuiScreen(ClickGuiModule p_Mod, ColorsModule p_Colors)
    {
        // COMBAT, EXPLOIT, MOVEMENT, RENDER, WORLD, MISC, HIDDEN, UI
        MenuComponents.add(new MenuComponentModList("Combat", ModuleType.COMBAT, 10, 3, "shield", p_Colors, p_Mod));
        MenuComponents.add(new MenuComponentModList("Exploit", ModuleType.EXPLOIT, 120, 3, "skull", p_Colors, p_Mod));
        // MenuComponents.add(new MenuComponentModList("Hidden", ModuleType.HIDDEN, 320,
        // 3));
        MenuComponents.add(new MenuComponentModList("Misc", ModuleType.MISC, 230, 3, "questionmark", p_Colors, p_Mod));
        MenuComponents.add(new MenuComponentModList("Movement", ModuleType.MOVEMENT, 340, 3, "arrow", p_Colors, p_Mod));
        MenuComponents.add(new MenuComponentModList("Render", ModuleType.RENDER, 450, 3, "eye", p_Colors, p_Mod));
        MenuComponents.add(new MenuComponentModList("UI", ModuleType.UI, 560, 3, "mouse", p_Colors, p_Mod));
        MenuComponents.add(new MenuComponentModList("World", ModuleType.WORLD, 670, 3, "blockimg", p_Colors, p_Mod));
        //   MenuComponents.add(new MenuComponentModList("Bot", ModuleType.BOT, 780, 3, "robotimg", p_Colors));
        MenuComponents.add(new MenuComponentModList("Litematica", ModuleType.LITEMATICA, 10, 203, "robotimg", p_Colors, p_Mod));

        MenuComponentPresetsList presetList = null;

        MenuComponents.add(presetList = new MenuComponentPresetsList("Presets", ModuleType.LITEMATICA, 120, 203, "robotimg", p_Colors, p_Mod));

        PresetsManager.Get().InitalizeGUIComponent(presetList);

        ClickGuiMod = p_Mod;

        /// Load settings
        for (MenuComponent l_Component : MenuComponents)
        {
            File l_Exists = new File("SalHack/GUI/" + l_Component.GetDisplayName() + ".json");
            if (!l_Exists.exists())
                continue;

            try
            {
                // create Gson instance
                Gson gson = new Gson();

                // create a reader
                Reader reader = Files
                        .newBufferedReader(Paths.get("SalHack/GUI/" + l_Component.GetDisplayName() + ".json"));

                // convert JSON file to map
                Map<?, ?> map = gson.fromJson(reader, Map.class);

                for (Map.Entry<?, ?> entry : map.entrySet())
                {
                    String l_Key = (String) entry.getKey();
                    String l_Value = (String) entry.getValue();

                    if (l_Key.equals("PosX"))
                        l_Component.SetX(Float.parseFloat(l_Value));
                    else if (l_Key.equals("PosY"))
                        l_Component.SetY(Float.parseFloat(l_Value));
                }

                reader.close();
            }
            catch (Exception e)
            {

            }
        }

        Random random = new Random();

        for (int i = 0; i < 100; ++i)
        {
            for (int y = 0; y < 3; ++y)
            {
                Snow snow = new Snow(25 * i, y * -50, random.nextInt(3) + 1, random.nextInt(2)+1);
                _snowList.add(snow);
            }
        }
    }

    private ClickGuiModule ClickGuiMod;

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (MenuComponent l_Menu : MenuComponents)
        {
            if (l_Menu.MouseClicked((int) mouseX, (int) mouseY, button, OffsetY))
                break;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (MenuComponent l_Menu : MenuComponents)
        {
            l_Menu.MouseReleased((int) mouseX, (int) mouseY);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (MenuComponent l_Menu : MenuComponents)
        {
            l_Menu.MouseClickMove((int) mouseX, (int) mouseY, button);
        }
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
        final Window res = Wrapper.GetMC().getWindow();

        if (!_snowList.isEmpty() && ClickGuiMod.Snowing.getValue())
        {
            _snowList.forEach(snow -> snow.Update(res, context));
        }

        if (Watermark != null && ClickGuiMod.Watermark.getValue()) {
            drawTexture(new Identifier(Watermark.GetResourceLocation()), 0, res.getScaledHeight() - Watermark.GetHeight() - 5, Watermark.GetWidth() / 2, Watermark.GetHeight() / 2, context);
        }

        MenuComponent l_LastHovered = null;

        for (MenuComponent l_Menu : MenuComponents)
            if (l_Menu.Render(mouseX, mouseY, true, AllowsOverflow(), OffsetY, context))
                l_LastHovered = l_Menu;

        if (l_LastHovered != null)
        {
            /// Add to the back of the list for rendering
            MenuComponents.remove(l_LastHovered);
            MenuComponents.add(l_LastHovered);
        }
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (amount > 0)
        {
            OffsetY = Math.max(0, OffsetY-1);
        }
        /// down
        else if (amount < 0)
        {
            OffsetY = Math.min(100, OffsetY + 1);
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (MenuComponent l_Menu : MenuComponents)
        {
            l_Menu.keyTyped(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        super.close();
        if (ClickGuiMod.isEnabled())
            ClickGuiMod.toggle();

        /// Save Settings

        for (MenuComponent l_Component : MenuComponents)
        {
            try
            {
                GsonBuilder builder = new GsonBuilder();

                Gson gson = builder.setPrettyPrinting().create();

                Writer writer = Files.newBufferedWriter(Paths.get("SalHack/GUI/" + l_Component.GetDisplayName() + ".json"));
                Map<String, String> map = new HashMap<>();

                map.put("PosX", String.valueOf(l_Component.GetX()));
                map.put("PosY", String.valueOf(l_Component.GetY()));

                gson.toJson(map, writer);
                writer.close();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public boolean AllowsOverflow()
    {
        return ClickGuiMod.AllowOverflow.getValue();
    }

    public void ResetToDefaults()
    {
        MenuComponents.forEach(comp -> comp.Default());
    }
}

