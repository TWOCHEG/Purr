package pon.purr.gui.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import pon.purr.utils.RGB;
import pon.purr.utils.math.Hover;
import pon.purr.utils.math.MathUtils;

import java.util.LinkedList;
import java.util.List;

public abstract class RenderArea {
    public int x = 0;
    public int y = 0;
    public int width = 0;
    public int height = 0;

    public List<RenderArea> areas = new LinkedList<>();

    public static final MinecraftClient mc = MinecraftClient.getInstance();
    public final TextRenderer textRenderer = mc.textRenderer;

    private final int lightColor = RGB.getColor(
        MathUtils.randomInt(0, 255),
        MathUtils.randomInt(0, 255),
        MathUtils.randomInt(0, 255),
        100
    );

    public RenderArea() {}

    public void render(DrawContext context, int startX, int startY, int width, int height, double mouseX, double mouseY) {
        this.x = startX;
        this.y = startY;
        this.width = width;
        this.height = height;

        this.animHandler();
    }

    public void lightArea(DrawContext context) {
        context.fill(
            x,
            y,
            x + width,
            y + height,
            lightColor
        );
    }

    public boolean checkHovered(double mouseX, double mouseY) {
        return Hover.hoverCheck(x, y, width, height, mouseX, mouseY);
    }

    public static boolean checkHovered(int x, int y, int width, int height, double mouseX, double mouseY) {
        return Hover.hoverCheck(x, y, width, height, mouseX, mouseY);
    }
    public static boolean checkHovered(RenderArea ra, double mouseX, double mouseY) {
        return Hover.hoverCheck(ra.x, ra.y, ra.width, ra.height, mouseX, mouseY);
    }

    public void animHandler() {}

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (RenderArea a : areas) {
            if (a.mouseClicked(mouseX, mouseY, button)) return true;
        }
        return false;
    }
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (RenderArea a : areas) {
            if (a.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }
    public boolean charTyped(char chr, int modifiers) {
        for (RenderArea area : areas) {
            if (area.charTyped(chr, modifiers)) return true;
        }
        return false;
    }
}
