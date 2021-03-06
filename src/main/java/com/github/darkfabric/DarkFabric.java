package com.github.darkfabric;

import com.github.darkfabric.addon.AbstractAddon;
import com.github.darkfabric.addon.AddonRegistry;
import com.github.darkfabric.command.CommandRegistry;
import com.github.darkfabric.config.ConfigRegistry;
import com.github.darkfabric.event.EventCaller;
import com.github.darkfabric.module.ModuleRegistry;
import com.github.darkfabric.util.LogHelper;
import lombok.Getter;
import me.zero.alpine.bus.EventBus;
import me.zero.alpine.bus.EventManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Getter
public class DarkFabric {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final DarkFabric INSTANCE = new DarkFabric();

    private final ConfigRegistry configRegistry = new ConfigRegistry();
    private final AddonRegistry addonRegistry = new AddonRegistry();
    private final ModuleRegistry moduleRegistry = new ModuleRegistry();
    private final CommandRegistry commandRegistry = new CommandRegistry();
    private final char commandPrefix = '-';

    private final EventBus eventBus = new EventManager();

    public static DarkFabric getInstance() {
        return INSTANCE;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public void initialize() {
        addonRegistry.getObjects().forEach(AbstractAddon::preInit);
        LogHelper.introduce();
        LogHelper.section(String.format("Initializing %s", getName()));
        LogHelper.section("loading configs");
        configRegistry.loadAll();
        LogHelper.section("loading modules");
        moduleRegistry.initialize();
        LogHelper.section("loading commands");
        commandRegistry.initialize();
        LogHelper.section("loading addons");
        addonRegistry.initialize();
        addonRegistry.getObjects().forEach(AbstractAddon::init);
        LogHelper.section(String.format("Done with %s", getName()));
        eventBus.subscribe(new EventCaller());
        addonRegistry.getObjects().forEach(AbstractAddon::postInit);
    }

    public void terminate() {
        LogHelper.section(String.format("Terminating %s", getName()));
        LogHelper.section("saving all configs");
        configRegistry.saveAll();
        LogHelper.section(String.format("Done with %s", getName()));
    }

    public String getName() {
        return "DarkFabric";
    }

}