package me.ionar.salhack.managers;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Paths;
// DO NOT TOUCH THESE THEY MAY BREAK OPENING THE GUI
@SuppressWarnings("ResultOfMethodCallIgnored")
public class FilesManager {

    public File presets = new File(getCurrentDirectory() + "/SalHack/Presets/");
    public FilesManager() {}

    public void init() {
        /// Create directories as needed
        createDirectory("SalHack");
        createDirectory("SalHack/Modules");
        createDirectory("SalHack/GUI");
        createDirectory("SalHack/HUD");
        createDirectory("SalHack/Locater");
        createDirectory("SalHack/StashFinder");
        createDirectory("SalHack/Config");
        createDirectory("SalHack/Capes");
        createDirectory("SalHack/Music");
        createDirectory("SalHack/CoordExploit");
        createDirectory("SalHack/LogoutSpots");
        createDirectory("SalHack/DeathSpots");
        createDirectory("SalHack/Waypoints");
        createDirectory("SalHack/Fonts");
        createDirectory("SalHack/CustomMods");
        createDirectory("SalHack/Presets");
        createDirectory("SalHack/Presets/Default");
        createDirectory("SalHack/Presets/Default/Modules");
    }

    public void createDirectory(String Path) {
        try {
            new File(Path).mkdirs();
        } catch(Exception ignored) {}
    }

    public void deleteDirectory(String Path) {
        try {
            FileUtils.deleteDirectory(new File(Path));
        } catch(Exception ignored) {}
    }

    public String getCurrentDirectory() {
        return Paths.get("").toAbsolutePath().toString();
    }

    public String read(String path){
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {return "";}
        return stringBuilder.toString();
    }

    public void write(String path, String content){
        FileWriter write;
        try {
            write = new FileWriter(path);
            write.write(content);
            write.close();
        } catch (IOException ignored) {}
    }
}
