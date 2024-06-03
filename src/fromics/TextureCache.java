package fromics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class TextureCache {
    private static Map<String, BufferedImage> cache;

    public static void add(String name, BufferedImage tex) {
        if(cache == null) cache = new HashMap<>();
        cache.put(name, tex);
    }

    public static void load(String name, String path) throws IOException {
        BufferedImage img = ImageIO.read(new File(path));
        if(cache == null) cache = new HashMap<>();
        cache.put(name, img);
    }

    public static void rem(String name) {
        if(cache != null) {
            cache.remove(name);
        }
    }

    public static void clear() {
        cache = null;
    }

    public static BufferedImage get(String name) {
        if(cache != null) {
            return cache.get(name);
        } else {
            return null;
        }
    }
}
