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

public class MarketPlugin extends JavaPlugin {

    private RegionManager regionManager;
    private ShopManager shopManager;
    private ConfirmationManager confirmationManager;

    @Override
    public void onEnable() {
        System.out.println("[MarketSystem] Plugin wird aktiviert!");
        if (!getDataFolder().exists()) { getDataFolder().mkdir(); }

        this.regionManager = new RegionManager(this);
        this.shopManager = new ShopManager(this);
        this.confirmationManager = new ConfirmationManager(this);

        getCommand("market").setExecutor(new MarketCommand(this, regionManager, shopManager));
        ConfirmationCommand confCommand = new ConfirmationCommand(this, confirmationManager, shopManager); // Wichtig: 'this' übergeben
        getCommand("yes").setExecutor(confCommand);
        getCommand("no").setExecutor(confCommand);

        registerListeners();
        startExpirationTask();
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();

        PlayerProtectionListener protectionListener = new PlayerProtectionListener(regionManager, shopManager);
        pm.registerEvent(Event.Type.BLOCK_PLACE, protectionListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, protectionListener, Event.Priority.Normal, this);

        PlayerInteractListener interactListener = new PlayerInteractListener(this, shopManager, confirmationManager); // Wichtig: 'this' übergeben
        pm.registerEvent(Event.Type.PLAYER_INTERACT, interactListener, Event.Priority.Normal, this);
    }

    private void startExpirationTask() {
        // Starte einen Task, der alle 30 Sekunden läuft (zum Testen)
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                // Wir erstellen eine Kopie der Liste, um sie sicher zu durchlaufen
                for (Shop shop : shopManager.getAllShops()) {
                    // Prüfen, ob der Shop einen Besitzer hat UND die Zeit abgelaufen ist
                    if (shop.getOwner() != null && shop.getExpirationTimestamp() != 0 && System.currentTimeMillis() >= shop.getExpirationTimestamp()) {
                        System.out.println("[MarketSystem] Shop von " + shop.getOwner() + " ist abgelaufen. Wird zurueckgesetzt.");
                        shopManager.resetShop(shop);
                    }
                }
            }
        }, 20L * 60, 20L * 60 * 5); // Start nach 1 Minute, dann alle 5 Minuten
    }

    @Override
    public void onDisable() {
        if (this.regionManager != null) regionManager.saveRegions();
        if (this.shopManager != null) shopManager.saveShops();
        System.out.println("[MarketSystem] Plugin wird deaktiviert!");
    }
}