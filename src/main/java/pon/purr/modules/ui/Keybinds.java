package pon.purr.modules.ui;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.TitleScreen;
import pon.purr.Purr;
import pon.purr.config.ConfigManager;
import pon.purr.events.impl.EventKeyPress;
import pon.purr.modules.Parent;

import java.util.HashMap;
import java.util.Map;

public class Keybinds extends Parent {
    private static Map<Integer, Boolean> callbacks = new HashMap<>();
    private static ConfigManager config = new ConfigManager();

    public Keybinds() {
        super(null, null);
    }

    @EventHandler
    private void keyPress(EventKeyPress e) {
        if (
            mc.currentScreen != null &&
            !(mc.currentScreen instanceof TitleScreen) &&
            e.getKey() != Purr.moduleManager.getModuleByClass(Gui.class).getKeybind()
        ) return;
        if (e.getModifiers() == 5) return;  // смена языка

        if (Purr.moduleManager == null) return;
        Parent module = Purr.moduleManager.getModuleByKey(e.getKey());
        if (module == null) return;

        module.toggle();
    }
}
