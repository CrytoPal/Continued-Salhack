package me.ionar.salhack.font;


import net.minecraft.util.Identifier;

public class Texture extends Identifier {
    public Texture(String path) {
        super("coffee", validatePath(path));
    }

    public Texture(Identifier identifier) {
        super(identifier.getNamespace(), identifier.getPath());
    }

    static String validatePath(String path) {
        if (isValid(path)) return path;
        StringBuilder returnString = new StringBuilder();
        for (char c : path.toLowerCase().toCharArray()) if (isPathCharacterValid(c)) returnString.append(c);
        return returnString.toString();
    }
}