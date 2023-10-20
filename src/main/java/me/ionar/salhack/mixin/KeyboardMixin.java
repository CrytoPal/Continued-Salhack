package me.ionar.salhack.mixin;

import me.ionar.salhack.gui.click.ClickGuiScreen;
import me.ionar.salhack.gui.hud.GuiHudEditor;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.module.ui.ClickGuiModule;
import me.ionar.salhack.module.ui.ColorsModule;
import me.ionar.salhack.module.ui.HudEditorModule;
import net.minecraft.client.Keyboard;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.ionar.salhack.main.Wrapper.mc;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    private ClickGuiScreen ClickGui = new ClickGuiScreen((ClickGuiModule)ModuleManager.Get().GetMod(ClickGuiModule.class), (ColorsModule)ModuleManager.Get().GetMod(ColorsModule.class));

    private GuiHudEditor HudEditor = new GuiHudEditor((HudEditorModule) ModuleManager.Get().GetMod(HudEditorModule.class));

    @Inject(method = "onKey", at = @At("HEAD"))
    public void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo callback) {
        if (action == GLFW.GLFW_PRESS) ModuleManager.OnKeyPress(key);
        if (key == ModuleManager.Get().GetMod(ClickGuiModule.class).getKey()) mc.setScreen(ClickGui);
        if (key == ModuleManager.Get().GetMod(HudEditorModule.class).getKey()) mc.setScreen(HudEditor);
    }
}
