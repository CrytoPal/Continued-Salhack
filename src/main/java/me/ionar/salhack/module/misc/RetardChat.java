package me.ionar.salhack.module.misc;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.network.PacketEvent;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

import static me.ionar.salhack.main.Wrapper.mc;

public final class RetardChat extends Module {
    public final Value<Modes> mode = new Value<>("Mode", new String[] {"M"}, "The retard chat mode", Modes.Spongebob);

    private String last = "";


    public enum Modes {
        Spongebob,
    }

    public RetardChat() {
        super("RetardChat", "Makes your chat retarded", 0, 0xDB2485, ModuleType.MISC);
    }

    @Override
    public String getMetaData()
    {
        return mode.getValue().toString();
    }

    @EventHandler
    public void onPacketSend(PacketEvent.Send p_Event) {
        if(!(p_Event.getPacket() instanceof ChatMessageC2SPacket c_Message))
            return;

        if (c_Message.chatMessage().startsWith("/"))
            return;

        if (c_Message.chatMessage().equals(last))
            return;

        String l_Message = "";

        switch (mode.getValue()) {
            case Spongebob: {
                boolean l_Flag = false;

                for (char l_Char : c_Message.chatMessage().toCharArray()) {
                    String l_Val = String.valueOf(l_Char);

                    l_Message += l_Flag ? l_Val.toUpperCase() : l_Val.toLowerCase();

                    if (l_Char != ' ')
                        l_Flag = !l_Flag;
                }
                break;
            }
        }

        p_Event.cancel();
        last = l_Message;
        mc.player.networkHandler.sendChatMessage(l_Message);
    }
}