package me.ionar.salhack.managers;
// DO NOT TOUCH THESE THEY MAY BREAK OPENING THE GUI
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.ionar.salhack.gui.hud.GuiHudEditor;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.gui.hud.components.*;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.module.ValueListeners;

public class HudManager {
    public HudManager() {
    }

    public void init() {
        add(new WatermarkComponent());
	    add(new WelcomerHudComponent());
        add(new CoordsHudComponent());
        add(new TimeComponent());
        add(new FPSComponent());
        add(new DirectionComponent());
        add(new TPSComponent());
        add(new PingComponent());
        add(new ResourcesComponent());
        add(new SpeedComponent());
        add(new RotationComponent());
        add(new TrueDurabilityComponent());
        add(new InventoryComponent());
        add(new ArmorHudComponent());
        add(new BiomeComponent());
        add(new PlayerCountComponent());
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
        add(new SelectorMenuComponent());

        canSave = false;

        componentItems.forEach(p_Item ->
        {
            p_Item.loadSettings();
        });

        canSave = true;
    }

    public ArrayList<HudComponentItem> componentItems = new ArrayList<HudComponentItem>();
    private boolean canSave = false;

    public void add(HudComponentItem p_Item) {
        try {
            for (Field field : p_Item.getClass().getDeclaredFields()) {
                if (Value.class.isAssignableFrom(field.getType())) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }

                    final Value val = (Value) field.get(p_Item);

                    ValueListeners listener = new ValueListeners() {
                        @Override
                        public void OnValueChange(Value p_Val)
                        {
                            scheduleSave(p_Item);
                        }
                    };

                    val.Listener = listener;
                    p_Item.values.add(val);
                }
            }
            componentItems.add(p_Item);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onRender(float p_PartialTicks, DrawContext context) {
        Screen l_CurrScreen = Wrapper.GetMC().currentScreen;

        if (l_CurrScreen != null) {
            if (l_CurrScreen instanceof GuiHudEditor) {
                return;
            }
        }

        context.getMatrices().push();

        componentItems.forEach(p_Item -> {
            if (!p_Item.isHidden() && !p_Item.HasFlag(HudComponentItem.OnlyVisibleInHudEditor)) {
                try {
                    p_Item.onRender(0, 0, p_PartialTicks, context);
                }
                catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        });

        context.getMatrices().pop();
    }


    public void scheduleSave(HudComponentItem p_Item) {
        if (!canSave)
            return;

        try {
            GsonBuilder builder = new GsonBuilder();

            Gson gson = builder.setPrettyPrinting().create();

            Writer writer = Files.newBufferedWriter(Paths.get("SalHack/HUD/" + p_Item.getDisplayName() + ".json"));
            Map<String, String> map = new HashMap<>();

            map.put("displayname", p_Item.getDisplayName());
            map.put("visible", !p_Item.isHidden() ? "true" : "false");
            map.put("PositionX", String.valueOf(p_Item.getPositionX()));
            map.put("PositionY", String.valueOf(p_Item.getPositionY()));
            map.put("ClampLevel", String.valueOf(p_Item.GetClampLevel()));
            map.put("ClampPositionX", String.valueOf(p_Item.getPositionX()));
            map.put("ClampPositionY", String.valueOf(p_Item.getPositionY()));
            map.put("Side", String.valueOf(p_Item.GetSide()));

            for (Value l_Val : p_Item.values)
            {
                map.put(l_Val.getName().toString(), l_Val.getValue().toString());
            }

            gson.toJson(map, writer);
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
