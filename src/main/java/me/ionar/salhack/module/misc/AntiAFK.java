package me.ionar.salhack.module.misc;

import me.ionar.salhack.module.Module;
import java.util.Timer;
import java.util.TimerTask;

public final class AntiAFK extends Module
{
    public AntiAFK()
    {
        super("AntiAFK", new String[]
                { "BuildH", "BHeight" }, "Makes sure you dont get kicked for afking", 0, 0xDB24C4, ModuleType.MISC);
    }

    private Timer timer = new Timer();

    @Override
    public void onEnable()
    {
        super.onEnable();

        if (mc.player == null)
        {
            toggle(true);
            return;
        }

        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                mc.player.networkHandler.sendCommand("stats");
            }
        }, 0, 120000);
    }

    @Override
    public void onDisable()
    {
        super.onDisable();

        if (timer != null)
            timer.cancel();
    }
}