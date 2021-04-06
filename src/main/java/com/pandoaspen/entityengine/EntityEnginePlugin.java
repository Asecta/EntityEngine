package com.pandoaspen.entityengine;

import co.aikar.commands.PaperCommandManager;
import com.pandoaspen.entityengine.engine.EntityEngine;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

@Getter
public class EntityEnginePlugin extends JavaPlugin implements Listener {

    private PaperCommandManager commandManager;

    private EntityEngine entityEngine;

    @Override
    public void onEnable() {
        this.entityEngine = new EntityEngine();

        this.commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new EntityEngineCommand());

        getServer().getPluginManager().registerEvents(this, this);

        getServer().getScheduler().scheduleSyncRepeatingTask(this, entityEngine::tick, 1, 1);
    }

    @Override
    public void onDisable() {
        entityEngine.destroy();
    }

    @EventHandler
    public void onMOve(PlayerMoveEvent event) {
        Vector direction = event.getTo().getDirection();

    }

}