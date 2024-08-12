package i.mrhua269.moliatopiabot;

import org.bukkit.plugin.java.JavaPlugin;

public class MoliatopiaBot extends JavaPlugin {
    @Override
    public void onEnable() {
        Bootstrapper.INSTANCE.runBot();
    }

    @Override
    public void onDisable() {
        Bootstrapper.INSTANCE.shutdownBot();
    }
}
