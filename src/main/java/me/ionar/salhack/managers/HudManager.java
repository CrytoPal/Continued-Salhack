package me.ionar.salhack.managers;

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
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.module.ValueListeners;

import static me.ionar.salhack.main.Wrapper.mc;

public class HudManager {
    public HudManager() {
    }

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

        Items.forEach(p_Item ->
        {
            p_Item.LoadSettings();
        });

        CanSave = true;
    }

    public ArrayList<HudComponentItem> Items = new ArrayList<HudComponentItem>();
    private boolean CanSave = false;

    public void Add(HudComponentItem p_Item) {
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
                            ScheduleSave(p_Item);
                        }
                    };

                    val.Listener = listener;
                    p_Item.ValueList.add(val);
                }
            }
            Items.add(p_Item);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void OnRender(float p_PartialTicks, DrawContext context) {
        Screen l_CurrScreen = mc.currentScreen;

        if (l_CurrScreen != null) {
            if (l_CurrScreen instanceof GuiHudEditor) {
                return;
            }
        }

        context.getMatrices().push();

        Items.forEach(p_Item -> {
            if (!p_Item.IsHidden() && !p_Item.HasFlag(HudComponentItem.OnlyVisibleInHudEditor)) {
                try {
                    p_Item.render(0, 0, p_PartialTicks, context);
                }
                catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        });

        context.getMatrices().pop();
    }

    public static HudManager Get() {
        return SalHack.GetHudManager();
    }

    public void ScheduleSave(HudComponentItem p_Item) {
        if (!CanSave)
            return;

        try {
            GsonBuilder builder = new GsonBuilder();

            Gson gson = builder.setPrettyPrinting().create();

            Writer writer = Files.newBufferedWriter(Paths.get("SalHack/HUD/" + p_Item.GetDisplayName() + ".json"));
            Map<String, String> map = new HashMap<>();

            map.put("displayname", p_Item.GetDisplayName());
            map.put("visible", !p_Item.IsHidden() ? "true" : "false");
            map.put("PositionX", String.valueOf(p_Item.GetX()));
            map.put("PositionY", String.valueOf(p_Item.GetY()));
            map.put("ClampLevel", String.valueOf(p_Item.GetClampLevel()));
            map.put("ClampPositionX", String.valueOf(p_Item.GetX()));
            map.put("ClampPositionY", String.valueOf(p_Item.GetY()));
            map.put("Side", String.valueOf(p_Item.GetSide()));

            for (Value l_Val : p_Item.ValueList)
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
