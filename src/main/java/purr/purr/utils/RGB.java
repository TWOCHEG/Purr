package purr.purr.utils;

public class RGB {
    public static int getColor(int r, int g, int b, int a) {
        a = Math.clamp(a, 4, 255); // я хуй знает почему
        r = Math.clamp(r, 0, 255);
        g = Math.clamp(g, 0, 255);
        b = Math.clamp(b, 0, 255);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
    public static int getColor(int r, int g, int b) {
        return getColor(r, g, b, 255);
    }
}
