package i.mrhua269.moliatopiabot.paper;

import i.mrhua269.moliatopiabot.Bootstrapper;
import org.bukkit.plugin.java.JavaPlugin;

public final class MoliatopiaBot extends JavaPlugin {

    @Override
    public void onEnable() {
        Bootstrapper.INSTANCE.runBot();
    }

    @Override
    public void onDisable() {
        Bootstrapper.INSTANCE.shutdownBot();
    }
}
