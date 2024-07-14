package fr.elytra.example.plugin;

import org.bukkit.plugin.java.JavaPlugin;

import fr.elytra.implementation.spigot.annotations.SpigotPlugin;
import fr.elytra.processor.annotations.Plugin;

@Plugin(name = "TestPlugin", version = "1.0", description = "A simple test plugin", authors = "Fabcc")
@SpigotPlugin(apiVersion = "1.17")
public class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Plugin enabled !");
    }

}
