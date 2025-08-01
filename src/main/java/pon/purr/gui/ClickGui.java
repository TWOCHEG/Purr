//// местная зона отчуждения, просьба не заходить без подготовки (хотя бы моральной)
//package pon.purr.gui;
//
//import pon.purr.modules.ModuleManager;
//import pon.purr.modules.settings.Group;
//import pon.purr.modules.settings.ListSetting;
//import pon.purr.modules.settings.Setting;
//import pon.purr.modules.ui.Gui;
//import pon.purr.utils.GetAnimDiff;
//import pon.purr.utils.KeyName;
//import pon.purr.utils.RGB;
//import pon.purr.modules.Parent;
//
//import net.minecraft.client.gui.DrawContext;
//import net.minecraft.client.gui.screen.Screen;
//import net.minecraft.text.Text;
//import net.minecraft.util.Formatting;
//import net.minecraft.client.MinecraftClient;
//
//import pon.purr.utils.math.AnimHelper;
//
//import org.lwjgl.glfw.GLFW;
//
//import java.util.*;
//
//public class ClickGui extends Screen {
//    private final Map<String, List<Parent>> modules;
//    private Gui guiModule;
//
//    private static final String hintsText = "← ↑ ↓ → - move gui\nleft shift - percent binds\nmouse middle - bind module";
//
//    private List<Object> moduleAreas = new ArrayList<>();
//
//    private Parent bindingModule = null;
//
//    private Setting inputSet = null;
//    private String inputText = null;
//    private float inputAnim = 0f;
//    private Boolean inputAnimReverse = true;
//
//    private float openAnim = 0;
//    private boolean animReverse = false;
//
//    private float showKeybind = 0;
//
//    private double lastMouseX = 0;
//    private double lastMouseY = 0;
//
//    private float xMove = 0;
//    private float yMove = 0;
//
//    private Object lastModule = null;
//
//    private final Screen previous;
//
//    private final Map<Object, Float> hoverAnim = new HashMap<>();
//    private final Map<Object, Boolean> hoverAnimReverse = new HashMap<>();
//
//    private final Map<Object, Float> bindAnim = new HashMap<>();
//    private final Map<Object, Boolean> bindAnimReverse = new HashMap<>();
//
//    private final Map<Object, Float> enableAnim = new HashMap<>();
//    private final Map<Object, Boolean> enableAnimReverse = new HashMap<>();
//
//    private Map<Object, Float> setAnim = new HashMap<>();
//    private Map<Object, Boolean> setAnimReverse = new HashMap<>();
//
//    private Map<Object, Float> exsAnim = new HashMap<>();
//    private Map<Object, Boolean> exsAnimReverse = new HashMap<>();
//
//    private Map<Object, Float> setVisAnim = new HashMap<>();
//    private Map<Object, Boolean> setVisAnimReverse = new HashMap<>();
//
//    private Map<Parent, List> settings = new HashMap<>();
//
//    public ClickGui(Screen previous, ModuleManager moduleManager, Gui guiModule, List<Map> lastValues) {
//        super(Text.literal("Purr Gui"));
//        this.previous = previous;
//        this.modules = moduleManager.getModules();
//        this.guiModule = guiModule;
//
//        if (!lastValues.isEmpty()) {
//            setAnim = lastValues.getFirst();
//            setAnimReverse = lastValues.get(1);
//
//            exsAnim = lastValues.get(2);
//            exsAnimReverse = lastValues.get(3);
//
//            setVisAnim = lastValues.get(4);
//            setVisAnimReverse = lastValues.get(5);
//
//            settings = lastValues.getLast();
//        }
//    }
//
//    public void closeGui() {
//        closeAll();
//
//        if (!guiModule.clearGui.getValue()) {
//            guiModule.lastValues.add(setAnim);
//            guiModule.lastValues.add(setAnimReverse);
//
//            guiModule.lastValues.add(exsAnim);
//            guiModule.lastValues.add(exsAnimReverse);
//
//            guiModule.lastValues.add(setVisAnim);
//            guiModule.lastValues.add(setVisAnimReverse);
//
//            guiModule.lastValues.add(settings);
//        } else if (!guiModule.lastValues.isEmpty()) {
//            guiModule.lastValues.clear();
//        }
//
//        animReverse = true;
//    }
//
//    public void closeAll() {
//        inputSet = null;
//        inputText = null;
//        inputAnim = 0f;
//    }
//    public boolean checkActive() {
//        return (
//            inputSet != null
//        );
//    }
//
//    @Override
//    public boolean shouldPause() {
//        return false;
//    }
//
//    @Override
//    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
//        MinecraftClient client = MinecraftClient.getInstance();
//        if (previous != null) {
//            previous.render(context, mouseX, mouseY, delta);
//        }
//
//        if (animReverse && openAnim == 0) {
//            client.setScreen(null);
//            return;
//        }
//
//        animHandler();
//
//        moduleAreas.clear();
//        float screenHeight = context.getScaledWindowHeight();
//        float screenWidth = context.getScaledWindowWidth();
//
//        handleGuiImage(context, screenWidth, screenHeight);
//
//        context.getMatrices().pushMatrix();
//        int alphaTop = 200 * (int) openAnim / 100;
//        int alphaBottom = 50 * (int) openAnim / 100;
//        context.getMatrices().translate(0, 0);
//        context.getMatrices().scale(1, 1);
//        context.fillGradient(0, 0, width, height,
//            RGB.getColor(0, 0, 0, alphaTop),
//            RGB.getColor(0, 0, 0, alphaBottom)
//        );
//        context.getMatrices().popMatrix();
//
//        context.getMatrices().pushMatrix();
//
//        String[] lines = hintsText.split("\n");
//        float hintsScale = 0.7f;
//        int alpha = 150 * (int) openAnim / 100;
//        int colorHints = RGB.getColor(255, 255, 255, alpha);
//        int xHints = 5;
//        float yHints = screenHeight - (textRenderer.fontHeight * lines.length);
//        context.getMatrices().translate(xHints, yHints);
//        context.getMatrices().scale(hintsScale, hintsScale);
//        for (int i = 0; i < lines.length; i++) {
//            context.drawText(textRenderer, Text.literal(lines[i]), 0, i * (textRenderer.fontHeight + 2), colorHints, false);
//        }
//
//        context.getMatrices().popMatrix();
//
//        context.getMatrices().pushMatrix();
//        {
//            float yStart = (10 + yMove) * openAnim / 100;
//            int baseTextHeight = textRenderer.fontHeight;
//            int spacing = 10;
//            int spacingColumns = 10;
//            int numCols = modules.size();
//            int columnWidth = Math.min(70, (width - spacingColumns * (numCols - 1)) / numCols);
//            int totalColsWidth = numCols * columnWidth + (numCols - 1) * spacingColumns;
//            float xColStart = ((width + xMove) - totalColsWidth) / 2;
//
//            for (Map.Entry<String, List<Parent>> entry : modules.entrySet()) {
//                String category = entry.getKey();
//                if (category == null) continue;
//                List<Parent> list = entry.getValue();
//
//                float categoryScale = 1.2f;
//                context.getMatrices().translate(xColStart, yStart);
//                context.getMatrices().scale(categoryScale, categoryScale);
//                context.drawText(
//                    textRenderer,
//                    Text.literal(category).formatted(Formatting.BOLD),
//                    0,
//                    0,
//                    RGB.getColor(255, 255, 255, 255 * (int) openAnim / 100),
//                    false
//                );
//                context.getMatrices().scale(1f / categoryScale, 1f / categoryScale);
//                context.getMatrices().translate(-xColStart, -yStart);
//
//                float yOffset = yStart + (baseTextHeight + spacing);
//                float maxWidth = 0;
//
//                for (Parent module : list) {
//                    if (module.getName() == null) continue;
//
//                    if (!enableAnim.containsKey(module) && module.getEnable()) {
//                        System.out.println(module.getName());
//                        enableAnimReverse.put(module, false);
//                        enableAnim.put(module, 0f);
//                    }
//                    boolean hovered = !(
//                        (mouseX >= xColStart && mouseX <= xColStart + columnWidth && mouseY >= yOffset && mouseY <= yOffset + baseTextHeight) ||
//                        (setAnim.get(module) != null)
//                    );
//                    hoverAnimReverse.put(module, hovered);
//                    if (!hoverAnim.containsKey(module)) {
//                        hoverAnim.put(module, 0f);
//                    }
//                    float hoverPercent = hoverAnim.get(module);
//
//                    float maxScaleDelta = 0.2f;
//                    float scale = 1.0f + (maxScaleDelta * hoverPercent / 100f);
//                    float scaledHeight = baseTextHeight * scale;
//
//                    int keyBind = module.getKeybind();
//                    String moduleName = module.getName();
//                    String name = moduleName;
//                    if (showKeybind > 1) {
//                        if (keyBind != -1) {
//                            String key = KeyName.get(keyBind);
//                            name = AnimHelper.getAnimText(moduleName, key, (int) showKeybind);
//                        }
//                    } else if (bindAnim.containsKey(module) && bindAnim.getOrDefault(module, 0f) > 0f) {
//                        name = AnimHelper.getAnimText(moduleName, "...", bindAnim.getOrDefault(module, 0f).intValue());
//                    }
//                    Text display = Text.literal(name);
//
//                    int baseAlpha = 255 * (int) openAnim / 100;
//                    int color = RGB.getColor(255, 255, 255, baseAlpha);
//
//                    if (!settings.containsKey(module)) {
//                        settings.put(module, module.getSettings());
//                    }
//                    float winHeight = 0;
//                    float winWidth = 0;
//                    float xDifference = 0;
//
//                    if (setAnim.containsKey(module) && !module.getSettings().isEmpty()) {
//                        List<Float> drawSettingsResult = drawSettings(module, settings.get(module), context, yOffset, xColStart, scale, spacing, baseTextHeight);
//                        winHeight = drawSettingsResult.getFirst();
//                        winWidth = drawSettingsResult.get(1);
//                    } else if (module.getSettings().isEmpty() && setAnim.containsKey(module)) {
//                        setAnimReverse.put(module, true);
//                        float percent = setAnim.get(module);
//                        if (percent % 2 == 0) xDifference -= 5f;
//                        else xDifference += 5f;
//                        xDifference = xDifference * percent / 100;
//                    }
//
//                    if (guiModule.moduleBg.getValue()) {
//                        float bgSpacing = 3f;
//                        context.getMatrices().pushMatrix();
//                        int bgX1 = (int) (xColStart - bgSpacing);
//                        int bgX2 = (int) (xColStart + (Math.max((textRenderer.getWidth(display) * scale) + bgSpacing, winWidth + (spacing / 2f))));
//                        int bgY1 = (int) (yOffset - bgSpacing);
//                        int bgY2 = (int) (yOffset + (Math.max((textRenderer.fontHeight * scale) + bgSpacing, ((textRenderer.fontHeight * scale) + winHeight + (spacing / 2f)))));
//                        context.fillGradient(
//                            bgX1,
//                            bgY1,
//                            bgX2,
//                            ((bgY2 - bgY1) / 2) + bgY1,
//                            RGB.getColor(0, 0, 0, 0),
//                            RGB.getColor(0, 0, 0, 150 * openAnim / 100)
//                        );
//                        context.fillGradient(
//                            bgX1,
//                            ((bgY2 - bgY1) / 2) + bgY1,
//                            bgX2,
//                            bgY2,
//                            RGB.getColor(0, 0, 0, 150 * openAnim / 100),
//                            RGB.getColor(0, 0, 0, 0)
//                        );
//
//                        context.fillGradient(
//                            bgX1,
//                            bgY1,
//                            bgX1 + 1,
//                            ((bgY2 - bgY1) / 2) + bgY1,
//                            RGB.getColor(0, 0, 0, 0),
//                            RGB.getColor(150, 150, 150, 150 * openAnim / 100)
//                        );
//                        context.fillGradient(
//                            bgX2 - 1,
//                            bgY1,
//                            bgX2,
//                            ((bgY2 - bgY1) / 2) + bgY1,
//                            RGB.getColor(0, 0, 0, 0),
//                            RGB.getColor(150, 150, 150, 150 * openAnim / 100)
//                        );
//                        context.fillGradient(
//                            bgX1,
//                            ((bgY2 - bgY1) / 2) + bgY1,
//                            bgX1 + 1,
//                            bgY2,
//                            RGB.getColor(150, 150, 150, 150 * openAnim / 100),
//                            RGB.getColor(0, 0, 0, 0)
//                        );
//                        context.fillGradient(
//                            bgX2 - 1,
//                            ((bgY2 - bgY1) / 2) + bgY1,
//                            bgX2,
//                            bgY2,
//                            RGB.getColor(150, 150, 150, 150 * openAnim / 100),
//                            RGB.getColor(0, 0, 0, 0)
//                        );
//                        context.getMatrices().popMatrix();
//                    }
//
//                    context.getMatrices().translate(xColStart + xDifference, yOffset);
//                    context.getMatrices().scale(scale, scale);
//
//                    context.drawText(textRenderer, display, 0, 0, color, false);
//
//                    context.getMatrices().scale(1f / scale, 1f / scale);
//                    context.getMatrices().translate(-(xColStart + xDifference), -yOffset);
//
//                    float eAnimP = (enableAnim.getOrDefault(module, 0f) * openAnim / 100) / 100;
//                    boolean eAnimR = enableAnimReverse.getOrDefault(module, true);
//                    if (eAnimP != 0f) {
//                        int x1 = (int) xColStart;
//                        int x2 = (int) xColStart;
//                        if (eAnimR) {
//                            x1 = (int) (x1 + (textRenderer.getWidth(display) * scale) * eAnimP);
//                        } else {
//                            x2 = (int) (x2 + (textRenderer.getWidth(display) * scale) * eAnimP);
//                        }
//                        context.drawHorizontalLine(
//                            x1,
//                            x2,
//                            (int) (yOffset + scaledHeight),
//                            RGB.getColor(255, 255, 255, (int) (255 * openAnim / 100))
//                        );
//                    }
//
//                    moduleAreas.add(new ModuleArea(
//                        module,
//                        xColStart,
//                        yOffset,
//                        (textRenderer.getWidth(name) * scale),
//                        scaledHeight
//                    ));
//
//                    maxWidth = Math.max(maxWidth, textRenderer.getWidth(name) * scale);
//                    maxWidth = Math.max(maxWidth, winWidth);
//                    yOffset += scaledHeight + spacing + winHeight;
//                }
//
//                float finalXOffset = columnWidth;
//                if (finalXOffset < maxWidth) {
//                    finalXOffset = maxWidth;
//                }
//                xColStart += finalXOffset + spacingColumns;
//            }
//        }
//        context.getMatrices().popMatrix();
//    }
//
//    @Override
//    public boolean charTyped(char chr, int modifiers) {
//        if (inputSet != null) {
//            inputText += chr;
//            return true;
//        }
//        return super.charTyped(chr, modifiers);
//    }
//
//    @Override
//    public boolean mouseClicked(double mouseX, double mouseY, int button) {
//        Object obj = getModuleUnderMouse((int) mouseX, (int) mouseY);
//        if (obj == null && checkActive()) {
//            closeAll();
//            return true;
//        }
//
//        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
//            if (obj instanceof ListArea lst) {
//                lst.set();
//                return true;
//            } else if (obj instanceof ListSetting set) {
//                int i = set.getOptions().indexOf(set.getValue());
//                i += 1;
//                if (i > set.getOptions().size() - 1) {
//                    i = 0;
//                }
//                set.setValue(set.getOptions().get(i));
//                return true;
//            } else if (obj instanceof Parent module) {
//                module.toggle();
//                if (module.getEnable()) {
//                    enableAnim.put(module, 0f);
//                    enableAnimReverse.put(module, false);
//                } else {
//                    enableAnimReverse.put(module, true);
//                }
//                return true;
//            }
//        } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
//            if (obj instanceof ListSetting || obj instanceof ListArea) {
//                Setting set = (Setting) (obj instanceof ListArea ? ((ListArea) obj).module : obj);
//                if (!exsAnim.containsKey(set)) {
//                    exsAnim.put(set, 0f);
//                    exsAnimReverse.put(set, false);
//                } else {
//                    exsAnimReverse.put(set, true);
//                }
//                return true;
//            } else if (obj instanceof Parent module) {
//                if (!setAnim.containsKey(module)) {
//                    setAnim.put(module, 0f);
//                    setAnimReverse.put(module, false);
//                } else {
//                    setAnimReverse.put(module, true);
//                }
//                return true;
//            }
//        } else if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
//            if (obj instanceof Parent module) {
//                bindingModule = module;
//                bindAnimReverse.put(bindingModule, false);
//                bindAnim.put(bindingModule, 0f);
//                for (Object key : bindAnim.keySet()) {
//                    if (key.equals(bindingModule)) {
//                        continue;
//                    }
//                    bindAnimReverse.put(key, true);
//                }
//                return true;
//            }
//        }
//
//        if (obj instanceof NumArea area) {
//            float x = ((Double) mouseX).floatValue();
//            area.set(x);
//        }
//
//        if (obj instanceof Group group) {
//            group.setOpen(!group.isOpen());
//            return true;
//        }
//
//        if (obj instanceof Setting set) {
//            if (set.getValue() instanceof Boolean) {
//                set.setValue(!(Boolean) set.getValue());
//            } else if (set.getValue() instanceof String || set.getValue() instanceof Integer || set.getValue() instanceof Float) {
//                inputSet = set;
//                inputText = set.getValue() instanceof String ? (String) set.getValue() : set.getValue().toString();
//            }
//            return true;
//        }
//        return super.mouseClicked(mouseX, mouseY, button);
//    }
//
//    @Override
//    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
//        if (lastModule == null) {
//            lastModule = getModuleUnderMouse(mouseX, mouseY);
//        }
//        if (lastModule instanceof NumArea area) {
//            area.set((float) mouseX);
//        }
//        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
//    }
//
//    @Override
//    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
//        Object obj = getModuleUnderMouse(mouseX, mouseY);
//        if (obj != null) {
//            if (obj instanceof NumArea area) {
//                if (scrollY > 0) {
//                    area.plus();
//                } else {
//                    area.minus();
//                }
//                return true;
//            }
//        }
//        xMove += (float) scrollX * 4;
//        yMove += (float) scrollY * 4;
//        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
//    }
//
//    @Override
//    public boolean mouseReleased(double mouseX, double mouseY, int button) {
//        if (lastModule != null) {
//            lastModule = null;
//        }
//        return super.mouseReleased(mouseX, mouseY, button);
//    }
//
//    @Override
//    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
//        if (checkActive() && keyCode == GLFW.GLFW_KEY_ESCAPE) {
//            closeAll();
//            return true;
//        }
//        if (bindingModule != null) {
//            int value = (keyCode == GLFW.GLFW_KEY_ESCAPE) ? -1 : keyCode;
//            bindingModule.setKeybind(value);
//            bindAnimReverse.put(bindingModule, true);
//            bindingModule = null;
//            return true;
//        }
//        if (inputSet != null && keyCode == GLFW.GLFW_KEY_ENTER) {
//            if (inputSet.getValue() instanceof Integer) {
//                try {
//                    int value = Integer.parseInt(inputText);
//                    inputSet.setValue(Math.clamp(value, (int) inputSet.min, (int) inputSet.max));
//                } catch (NumberFormatException ignored) {}
//            } else if (inputSet.getValue() instanceof Float) {
//                try {
//                    float value = Float.parseFloat(inputText.replace(",", "."));
//                    inputSet.setValue(Math.clamp(value, (float) inputSet.min, (float) inputSet.max));
//                } catch (NumberFormatException ignored) {}
//            } else {
//                inputSet.setValue(inputText);
//            }
//            inputSet = null;
//            inputText = null;
//            inputAnim = 0f;
//            return true;
//        }
//        if (inputSet != null && keyCode == GLFW.GLFW_KEY_V && modifiers != 0) {
//            inputText += client.keyboard.getClipboard();
//        }
//        if (inputSet != null && keyCode == GLFW.GLFW_KEY_BACKSPACE) {
//            if (!inputText.isEmpty()) {
//            inputText = inputText.substring(0, inputText.length() - 1);
//            }
//            return true;
//        }
//
//        if (keyCode == GLFW.GLFW_KEY_UP) {
//            yMove -= 10;
//            return true;
//        } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
//            yMove += 10;
//            return true;
//        } else if (keyCode == GLFW.GLFW_KEY_RIGHT) {
//            xMove += 10;
//            return true;
//        } else if (keyCode == GLFW.GLFW_KEY_LEFT) {
//            xMove -= 10;
//            return true;
//        }
//
//        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
//            closeGui();
//            return true;
//        }
//        return super.keyPressed(keyCode, scanCode, modifiers);
//    }
//
//    @Override
//    public void mouseMoved(double mouseX, double mouseY) {
//        if (client != null && client.player != null && openAnim >= 100 && !animReverse && guiModule.mouseMove.getValue()) {
//            double deltaX = mouseX - lastMouseX;
//            double deltaY = mouseY - lastMouseY;
//
//            if (lastMouseX == 0 && lastMouseY == 0) {
//                lastMouseX = mouseX;
//                lastMouseY = mouseY;
//                return;
//            }
//
//            float sensitivity = 0.05f;
//
//            float yaw = client.player.getYaw() + (float) (deltaX * sensitivity);
//            float pitch = Math.clamp(client.player.getPitch() + (float) (deltaY * sensitivity), -89.0f, 89.0f);
//
//            client.player.setYaw(yaw);
//            client.player.setPitch(pitch);
//
//            lastMouseX = mouseX;
//            lastMouseY = mouseY;
//        }
//
//        super.mouseMoved(mouseX, mouseY);
//    }
//
//    private void animHandler() {
//        openAnim = AnimHelper.handleAnimValue(animReverse, openAnim);
//
//        long window = client.getWindow().getHandle();
//        boolean shiftDown = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS;
//        showKeybind = AnimHelper.handleAnimValue(!shiftDown, showKeybind);
//
//        if (inputSet != null) {
//            inputAnim = AnimHelper.handleAnimValue(inputAnimReverse, inputAnim, GetAnimDiff.get() / 3f, AnimHelper.AnimMode.Linear);
//            if (inputAnim == 100f) {
//                inputAnimReverse = true;
//            } else if (inputAnim == 0f) {
//                inputAnimReverse = false;
//            }
//        }
//
//        AnimHelper.handleMapAnim(hoverAnim, hoverAnimReverse, GetAnimDiff.get() * 2);
//        AnimHelper.handleMapAnim(bindAnim, bindAnimReverse);
//        AnimHelper.handleMapAnim(setAnim, setAnimReverse);
//        AnimHelper.handleMapAnim(exsAnim, exsAnimReverse);
//        AnimHelper.handleMapAnim(setVisAnim, setVisAnimReverse, false);
//        AnimHelper.handleMapAnim(enableAnim, enableAnimReverse, false);
//    }
//
//    public List<Float> drawSettings(
//        Parent module,
//        List<Setting<?>> sets,
//        DrawContext context,
//        float yOffset,
//        float xStart,
//        float scale,
//        float moduleSpacing,
//        int baseTextHeight
//    ) {
//        // параметры
//        int zDepth = 3;
//        float setAnimPercent = openAnim * setAnim.getOrDefault(module, 0f) / 100;
//        int paddingBelowText = 5;
//        int rectY = (int) (yOffset + baseTextHeight * scale + paddingBelowText);
//        float spacing = moduleSpacing / 2;
//        xStart += spacing;
//        float drawOffsetY = 10 * (setAnimPercent - 100) / 100f;
//
//        float ySetOffset = paddingBelowText + rectY;
//
//        float maxWidth = 0f;
//        Group currentGroup;
//        Group lastDrawGroup = null;
//
//        for (Setting set : sets) {
//            float xColStart = xStart;
//            int alphaColor = (int) (255 * setAnimPercent / 100);
//            float textScale = guiModule.settingsScale.getValue();
//            int color;
//            String name;
//            currentGroup = set.getGroup();
//
//            if (currentGroup != null && lastDrawGroup != currentGroup) {
//                Text hearderText = Text.literal(currentGroup.getName() + (currentGroup.isOpen() ? " +" : " -"));
//                context.getMatrices().pushMatrix();
//                context.getMatrices().translate(xColStart, ySetOffset + (10 * (setAnimPercent - 100) / 100f));
//                context.getMatrices().scale(textScale, textScale);
//                int colorE = (int) (175 * setAnimPercent / 100);
//                context.drawText(
//                    textRenderer, hearderText, 0, 0,
//                    RGB.getColor(colorE, colorE, colorE, alphaColor),
//                    false
//                );
//                context.getMatrices().popMatrix();
//
//                moduleAreas.add(new ModuleArea(
//                    currentGroup,
//                    xColStart,
//                    ySetOffset,
//                    textRenderer.getWidth(hearderText),
//                    textRenderer.fontHeight
//                ));
//
//                maxWidth = Math.max(maxWidth, textRenderer.getWidth(hearderText));
//
//                ySetOffset += (textRenderer.fontHeight * textScale) + spacing;
//                lastDrawGroup = currentGroup;
//            }
//            if (!setVisAnim.containsKey(set)) {
//                if (currentGroup != null && !currentGroup.isOpen()) {
//                    setVisAnim.put(set, 0.0f);
//                    setVisAnimReverse.put(set, true);
//                } else {
//                    boolean meetsCondition = set.getVisible();
//                    boolean isGroupOpenOrNoGroup = (currentGroup == null || currentGroup.isOpen());
//                    boolean initialReverse = !(meetsCondition && isGroupOpenOrNoGroup);
//
//                    setVisAnim.put(set, initialReverse ? 0.0f : 100.0f);
//                    setVisAnimReverse.put(set, initialReverse);
//                }
//            }
//            if (currentGroup != null) {
//                if (!currentGroup.isOpen() && !setVisAnimReverse.getOrDefault(set, false)) {
//                    setVisAnimReverse.put(set, true);
//                } else if (currentGroup.isOpen() && setVisAnimReverse.getOrDefault(set, true)) {
//                    setVisAnim.put(set, setVisAnim.getOrDefault(set, 0.0f));
//                    setVisAnimReverse.put(set, false);
//                }
//            }
//            boolean isGroupOpenOrNoGroup = currentGroup == null || currentGroup.isOpen();
//            if (isGroupOpenOrNoGroup) {
//                boolean meetsCondition = set.getVisible();
//
//                if (!meetsCondition && !setVisAnimReverse.getOrDefault(set, false)) {
//                    setVisAnimReverse.put(set, true);
//                } else if (meetsCondition && setVisAnimReverse.getOrDefault(set, true)) {
//                    setVisAnim.put(set, setVisAnim.getOrDefault(set, 0.0f));
//                    setVisAnimReverse.put(set, false);
//                }
//            }
//            float visAnimPercent = setVisAnim.getOrDefault(set, 100.0f) * setAnimPercent / 100;
//            if (visAnimPercent == 0.0f) continue;
//
//            if (currentGroup != null) {
//                xColStart += spacing;
//                context.getMatrices().pushMatrix();
//                context.getMatrices().translate(0, 0);
//                context.fill(
//                    (int) (xStart + 1),
//                    (int) (ySetOffset),
//                    (int) (xStart),
//                    (int) (ySetOffset + textRenderer.fontHeight),
//                    RGB.getColor(175, 175, 175, (int) (200 * visAnimPercent / 100))
//                );
//                context.getMatrices().popMatrix();
//            }
//
//            alphaColor = (int) (alphaColor * visAnimPercent / 100);
//
//            if (set instanceof ListSetting lst) {
//                if (exsAnim.get(lst) != null && exsAnim.get(lst) != 0.0f) {
//                    float exsPercent = visAnimPercent * exsAnim.getOrDefault(lst, 0.0f) / 100;
//                    float drawX = xColStart + spacing;
//                    float headerY = ySetOffset + drawOffsetY;
//                    Text hearderText = Text.literal(set.getName() + ": " + AnimHelper.getAnimText((String) lst.getValue(), "", (int) exsPercent));
//                    maxWidth = Math.max(textRenderer.getWidth(hearderText) * textScale, maxWidth);
//                    context.getMatrices().pushMatrix();
//                    context.getMatrices().translate(drawX - spacing, headerY);
//                    context.getMatrices().scale(textScale, textScale);
//                    int colorE = (int) (255 - (55 * exsPercent / 100));
//                    context.drawText(
//                        textRenderer, hearderText, 0, 0,
//                        RGB.getColor(colorE, colorE, colorE, alphaColor),
//                        false
//                    );
//                    context.getMatrices().popMatrix();
//                    if (visAnimPercent == 100f) {
//                        moduleAreas.add(new ModuleArea(lst, drawX - spacing, headerY, textRenderer.getWidth(hearderText) * textScale, textRenderer.fontHeight));
//                    }
//                    maxWidth = Math.max(maxWidth, (textRenderer.getWidth(hearderText) * textScale) * exsPercent / 100);
//                    float headerHeight = textRenderer.fontHeight * textScale;
//
//                    List<String> options = lst.getOptions();
//                    float optYOffset = headerHeight + spacing;
//                    for (String element : options) {
//                        float drawY = ySetOffset + drawOffsetY + (optYOffset * exsPercent / 100);
//                        float width = textRenderer.getWidth(element) * textScale;
//                        float height = textRenderer.fontHeight * textScale;
//                        Text display = lst.getValue().equals(element) ? Text.literal(element).formatted(Formatting.BOLD) : Text.literal(element);
//
//                        context.getMatrices().pushMatrix();
//                        context.getMatrices().translate(drawX, drawY);
//                        context.getMatrices().scale(textScale, textScale);
//                        int colorE2 = (int) ((alphaColor * openAnim / 100) * exsPercent / 100);
//                        context.drawText(
//                            textRenderer, display, 0, 0, RGB.getColor(255, 255, 255, colorE2), false
//                        );
//                        context.getMatrices().popMatrix();
//
//                        if (exsPercent == 100f) {
//                            moduleAreas.add(new ListArea(lst, element, drawX, drawY, width, height));
//                        }
//
//                        maxWidth = Math.max(maxWidth, width * exsPercent / 100);
//
//                        optYOffset += (textRenderer.fontHeight * textScale) + spacing;
//                    }
//                    ySetOffset += (headerHeight + (((optYOffset) - headerHeight) * exsPercent / 100) + spacing) * visAnimPercent / 100;
//                    continue;
//                } else {
//                    name = set.getName() + ": " + set.getValue();
//                    color = RGB.getColor(255, 255, 255, alphaColor);
//                }
//            } else if (set.getValue() != null) {
//                name = set.getName() + ": " + set.getValue();
//                if (set.getValue() instanceof String && ((String) set.getValue()).isEmpty()) {
//                    name = set.getName() + ": ...";
//                }
//                color = RGB.getColor(255, 255, 255, alphaColor);
//                if (set.getValue() instanceof Boolean) {
//                    name = set.getName() + ": " + ((boolean) set.getValue() ? "1" : "0");
//                    if ((boolean) set.getValue()) {
//                        color = RGB.getColor(210, 255, 230, alphaColor);
//                    } else {
//                        color = RGB.getColor(255, 210, 230, alphaColor);
//                    }
//                }
//                if (inputSet != null && inputSet.equals(set)) {
//                    name = set.getName() + ": " + (inputText != null && !inputText.isEmpty() ? inputText : "...");
//                    color = RGB.getColor(255,  255, 255, (int) (255 - (200 * inputAnim / 100)));
//                }
//            } else {
//                name = set.getName();
//                color = RGB.getColor(175, 175, 175, alphaColor);
//            }
//
//            float drawX = xColStart;
//            float drawY = ySetOffset + drawOffsetY;
//            float width = (textRenderer.getWidth(name) * textScale) + (currentGroup != null ? spacing * 2 : spacing);
//            float height = textRenderer.fontHeight * textScale;
//            Text display = Text.literal(name);
//
//            context.getMatrices().pushMatrix();
//            context.getMatrices().translate(drawX, drawY);
//            context.getMatrices().scale(textScale, textScale);
//            context.drawText(textRenderer, display, 0, 0, color, false);
//            context.getMatrices().popMatrix();
//
//            if (visAnimPercent == 100f) {
//                moduleAreas.add(new ModuleArea(set, drawX, drawY, width, height));
//            }
//
//            drawY += textRenderer.fontHeight;
//
//            if (set.getValue() instanceof Integer || set.getValue() instanceof Float) {
//                double currentValue = set.getValue() instanceof Float
//                        ? ((Float) set.getValue()).doubleValue()
//                        : ((Integer) set.getValue()).doubleValue();
//
//                double currentMin = set.min instanceof Float
//                        ? ((Float) set.min).doubleValue()
//                        : ((Integer) set.min).doubleValue();
//
//                double currentMax = set.max instanceof Float
//                        ? ((Float) set.max).doubleValue()
//                        : ((Integer) set.max).doubleValue();
//                double totalWidth = (xStart + (spacing * 2) + Math.max(textRenderer.getWidth(set.getName()), 50) * textScale) - xColStart;
//                double ratio = 0.0;
//                if (currentMax - currentMin != 0) {
//                    ratio = (currentValue - currentMin) / (currentMax - currentMin);
//                }
//                int filledWidth = (int) (totalWidth * ratio);
//                maxWidth = Math.max(maxWidth, (filledWidth + spacing) * visAnimPercent / 100);
//
//                context.getMatrices().pushMatrix();
//                context.getMatrices().translate(0, 0);
//                context.fill(
//                        (int) xColStart,
//                        (int) (drawY + 2),
//                        (int) (xStart + totalWidth),
//                        (int) (drawY + 3),
//                        RGB.getColor(175, 175, 175, (int) (200 * (visAnimPercent) / 100))
//                );
//                context.fill(
//                        (int) xColStart,
//                        (int) (drawY + 2),
//                        (int) (xColStart + filledWidth),
//                        (int) (drawY + 3),
//                        RGB.getColor(220, 220, 220, (int) (200 * (visAnimPercent) / 100))
//                );
//                context.getMatrices().popMatrix();
//
//                if (visAnimPercent == 100f) {
//                    moduleAreas.add(new NumArea(set, xColStart, drawY, (float) totalWidth, 6));
//                }
//                height += 4;
//            }
//
//            maxWidth = Math.max(maxWidth, width * visAnimPercent / 100);
//
//            ySetOffset += (height + spacing) * visAnimPercent / 100;
//        }
//
//        ySetOffset = (ySetOffset - rectY) * setAnimPercent / 100;
//
//        return new ArrayList<>(List.of(ySetOffset, (maxWidth + spacing) * setAnimPercent / 100));
//    }
//
//    private void handleGuiImage(DrawContext context, float screenWidth, float screenHeight) {
////        if (
////            guiModule != null &&
////            guiModule.image.getValue() != null &&
////            !guiModule.image.getValue().equals("none")
////        ) {
////            String path = guiModule.getImages().get(guiModule.image.getValue());
////            Identifier texture = Identifier.of("purr", path);
////            try {
////                List<Float> size = guiModule.getImageSize(texture);
////                float width = size.getFirst();
////                float height = size.get(1);
////
////                float x = screenWidth - (width * openAnim / 100);
////                float y = (screenHeight - (height * openAnim / 100)) + 1;
////
////                context.getMatrices().pushMatrix();
////                context.getMatrices().translate(0, 0);
////                context.drawTexture(
////                    RenderLayer::getGuiTextured,
////                    texture,
////                    (int) x,
////                    (int) y,
////                    0, 0,
////                    (int) width,
////                    (int) height,
////                    (int) width,
////                    (int) height
////                );
////                context.getMatrices().popMatrix();
////            } catch (Exception ignored) {}
////        }
//    }
//
//    private Object getModuleUnderMouse(float mouseX, float mouseY) {
//        for (Object module : moduleAreas) {
//             if (module instanceof ModuleArea area) {
//                if (mouseX >= area.x
//                        && mouseX <= area.x + area.width
//                        && mouseY >= area.y
//                        && mouseY <= area.y + area.height) {
//                    return (
//                        area instanceof ListArea ||
//                        area instanceof NumArea
//                    ) ? area : area.module;
//                }
//            }
//        }
//        return null;
//    }
//    private Object getModuleUnderMouse(double mouseX, double mouseY) {
//        return getModuleUnderMouse(((Double) mouseX).floatValue(), ((Double) mouseY).floatValue());
//    }
//
//    private static class NumArea extends ModuleArea {
//        public NumArea(Setting set, float x, float y, float width, float height) {
//            super(set, x, y - 2, width, height + 2);
//        }
//
//        public void set(float mouseX) {
//            if (module instanceof Setting set) {
//                float normalizedX = mouseX - x;
//                normalizedX = Math.max(0f, Math.min(normalizedX, width));
//
//                double currentMin = ((Number) set.min).doubleValue();
//                double currentMax = ((Number) set.max).doubleValue();
//
//                double ratio = width > 0 ? normalizedX / width : 0;
//
//                double calculatedValue = currentMin + (currentMax - currentMin) * ratio;
//
//                if (set.getValue() instanceof Integer) {
//                    set.setValue(
//                        Math.clamp((int) Math.round(calculatedValue), (int) set.min, (int) set.max)
//                    );
//                } else if (set.getValue() instanceof Float) {
//                    set.setValue(
//                        Math.clamp((float) (Math.round(calculatedValue * 10.0) / 10.0), (float) set.min, (float) set.max)
//                    );
//                }
//            }
//        }
//
//        public void plus() {
//            if (module instanceof Setting set) {
//                if (set.getValue() instanceof Integer v) {
//                    set.setValue(Math.clamp(v + 1, (int) set.min, (int) set.max));
//                } else if (set.getValue() instanceof Float v) {
//                    float newValue = (float) Math.round((v + 0.1f) * 10) / 10f;
//                    newValue = Math.clamp(newValue, (float) set.min, (float) set.max);
//                    set.setValue(newValue);
//                }
//            }
//        }
//        public void minus() {
//            if (module instanceof Setting set) {
//                if (set.getValue() instanceof Integer v) {
//                    set.setValue(Math.clamp(v - 1, (int) set.min, (int) set.max));
//                } else if (set.getValue() instanceof Float v) {
//                    float newValue = (float) Math.round((v - 0.1f) * 10) / 10f;
//                    newValue = Math.clamp(newValue, (float) set.min, (float) set.max);
//                    set.setValue(newValue);
//                }
//            }
//        }
//    }
//
//    private static class ListArea extends ModuleArea {
//        private final String value;
//
//        public ListArea(ListSetting listClass, String value, float x, float y, float width, float height) {
//            super(listClass, x, y, width, height);
//            this.value = value;
//        }
//
//        public void set() {
//            if (module instanceof ListSetting lst) {
//                lst.setValue(value);
//            }
//        }
//    }
//
//    private static class ModuleArea {
//        public final Object module;
//        public final float x;
//        public final float y;
//        public final float width;
//        public final float height;
//
//        public ModuleArea(Object module, float x, float y, float width, float height) {
//            this.module = module;
//            this.x = x;
//            this.y = y;
//            this.width = width;
//            this.height = height;
//        }
//    }
//}