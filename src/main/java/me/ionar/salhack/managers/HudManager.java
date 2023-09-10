package me.ionar.salhack.managers;

import me.ionar.salhack.gui.hud.GuiHudEditor;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.gui.hud.components.*;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.module.Value;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class HudManager {
    public HudManager() {}

    public void Init() {
        Add(new WatermarkComponent());
	    Add(new WelcomerHudComponent());
        Add(new CoordsHudComponent());
        Add(new TimeComponent());
        Add(new FPSComponent());
        Add(new DirectionComponent());
        Add(new TPSComponent());
        Add(new PingComponent());
        Add(new ResourcesComponent());
        Add(new SpeedComponent());
        Add(new RotationComponent());
        Add(new TrueDurabilityComponent());
        Add(new InventoryComponent());
        Add(new ArmorHudComponent());
        Add(new BiomeComponent());
        Add(new PlayerCountComponent());
        /*
        Add(new ArrayListComponent());
        Add(new InventoryComponent());
        Add(new TabGUIComponent());
        Add(new TextRadar());
        Add(new NotificationComponent());
        Add(new BiomeComponent());
        Add(new TimeComponent());
        Add(new TPSComponent());
        Add(new FPSComponent());
        Add(new DirectionComponent());
        Add(new TooltipComponent());
        Add(new ArmorComponent());
        Add(new KeyStrokesComponent());
        Add(new HoleInfoComponent());
        Add(new PlayerCountComponent());
        Add(new PlayerFrameComponent());
        Add(new NearestEntityFrameComponent());
        Add(new YawComponent());
        Add(new TotemCountComponent());
        Add(new PingComponent());
        Add(new ChestCountComponent());
        Add(new StopwatchComponent());
        Add(new PvPInfoComponent());
        Add(new SchematicaMaterialInfoComponent());

         */

        // MUST be last in list
        Add(new SelectorMenuComponent());

        CanSave = false;

        ComponentItems.forEach(HudComponentItem::LoadSettings);

        CanSave = true;
    }

    public ArrayList<HudComponentItem> ComponentItems = new ArrayList<>();
    private boolean CanSave = false;

    public void Add(HudComponentItem componentItem) {
        try {
            for (Field field : componentItem.getClass().getDeclaredFields()) {
                if (Value.class.isAssignableFrom(field.getType())) {
                    if (!field.canAccess(null)) field.setAccessible(true);
                    final Value val = (Value) field.get(componentItem);
                    val.Listener = p_Val -> ScheduleSave(componentItem);
                    componentItem.ValueList.add(val);
                }
            }
            ComponentItems.add(componentItem);
        } catch (Exception ignored) {}
    }

    public void OnRender(float partialTicks, DrawContext context) {
        Screen currentScreen = Wrapper.GetMC().currentScreen;
        if (currentScreen instanceof GuiHudEditor) return;
        context.getMatrices().push();
        ComponentItems.forEach(componentItem -> {
            if (!componentItem.IsHidden() && !componentItem.HasFlag(HudComponentItem.OnlyVisibleInHudEditor)) componentItem.render(0, 0, partialTicks, context);
        });
        context.getMatrices().pop();
    }

    public static HudManager Get() {
        return SalHack.GetHudManager();
    }

    public void ScheduleSave(HudComponentItem componentItem) {
        if (!CanSave) return;
        Map<String, String> map = new HashMap<>();
        map.put("displayname", componentItem.GetDisplayName());
        map.put("visible", !componentItem.IsHidden() ? "true" : "false");
        map.put("PositionX", String.valueOf(componentItem.GetX()));
        map.put("PositionY", String.valueOf(componentItem.GetY()));
        map.put("ClampLevel", String.valueOf(componentItem.GetClampLevel()));
        map.put("ClampPositionX", String.valueOf(componentItem.GetX()));
        map.put("ClampPositionY", String.valueOf(componentItem.GetY()));
        map.put("Side", String.valueOf(componentItem.GetSide()));
        for (Value value : componentItem.ValueList) map.put(value.getName(), value.getValue().toString());
        FilesManager.Get().write("SalHack/HUD/"+componentItem.GetDisplayName()+".json", SalHack.gson.toJson(map, map.getClass()));
    }
}
