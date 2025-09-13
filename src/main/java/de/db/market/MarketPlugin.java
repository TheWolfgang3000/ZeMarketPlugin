package de.db.market;

import de.db.market.commands.ConfirmationCommand;
import de.db.market.commands.MarketCommand;
import de.db.market.data.Shop;
import de.db.market.listeners.PlayerInteractListener;
import de.db.market.listeners.PlayerProtectionListener;
import de.db.market.managers.ConfirmationManager;
import de.db.market.managers.RegionManager;
import de.db.market.managers.ShopManager;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class for the ZeMarketSystem plugin.
 * Handles startup, shutdown, and registration of all components.
 */
public class MarketPlugin extends JavaPlugin {

    private RegionManager regionManager;
    private ShopManager shopManager;
    private ConfirmationManager confirmationManager;

    @Override
    public void onEnable() {
        System.out.println("[MarketSystem] Enabling plugin...");

        // Ensure the plugin's data folder exists for configuration files.
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        // Initialize all manager classes
        this.regionManager = new RegionManager(this);
        this.shopManager = new ShopManager(this);
        this.confirmationManager = new ConfirmationManager(this);

        // Register command executors
        getCommand("market").setExecutor(new MarketCommand(this, regionManager, shopManager));
        ConfirmationCommand confCommand = new ConfirmationCommand(this, confirmationManager, shopManager);
        getCommand("yes").setExecutor(confCommand);
        getCommand("no").setExecutor(confCommand);

        // Register event listeners
        registerListeners();
        // Start the repeating task to check for expired shops
        startExpirationTask();
    }

    /**
     * Registers all necessary event listeners for the plugin.
     */
    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();

        PlayerProtectionListener protectionListener = new PlayerProtectionListener(regionManager, shopManager);
        pm.registerEvent(Event.Type.BLOCK_PLACE, protectionListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, protectionListener, Event.Priority.Normal, this);

        PlayerInteractListener interactListener = new PlayerInteractListener(this, shopManager, confirmationManager);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, interactListener, Event.Priority.Normal, this);
    }

    /**
     * Starts a repeating task that periodically checks for and resets expired shops.
     */
    private void startExpirationTask() {
        // Schedule a synchronous repeating task.
        // The task starts after a short delay and then runs at a fixed interval.
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                // Iterate over a copy of the list to prevent concurrent modification issues.
                for (Shop shop : shopManager.getAllShops()) {
                    // Check if the shop is owned and if its expiration time has passed.
                    if (shop.getOwner() != null && shop.getExpirationTimestamp() != 0 && System.currentTimeMillis() >= shop.getExpirationTimestamp()) {
                        System.out.println("[MarketSystem] Shop owned by " + shop.getOwner() + " has expired. Resetting.");
                        shopManager.resetShop(shop);
                    }
                }
            }
        }, 20L * 60, 20L * 60 * 5); // Start after 1 minute, then run every 5 minutes.
    }

    @Override
    public void onDisable() {
        // Ensure all data is saved when the plugin is disabled.
        if (this.regionManager != null) regionManager.saveRegions();
        if (this.shopManager != null) shopManager.saveShops();
        System.out.println("[MarketSystem] Plugin disabled.");
    }
}