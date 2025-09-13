package de.db.market.managers;

import de.db.market.MarketPlugin;
import de.db.market.data.Shop;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

public class ConfirmationManager {

    private final MarketPlugin plugin;
    // Speichert, welcher Spieler (Name) gerade welchen Shop mieten will.
    private final Map<String, Shop> pendingConfirmations = new HashMap<>();

    public ConfirmationManager(MarketPlugin plugin) {
        this.plugin = plugin;
    }

    public void addPendingConfirmation(Player player, Shop shop) {
        final String playerName = player.getName();
        pendingConfirmations.put(playerName, shop);
        player.sendMessage("§e[MarketSystem] §fMoechtest du diesen Shop mieten?");
        player.sendMessage("§fGib §a/yes§f ein, um zu bestaetigen, oder §c/no§f zum Abbrechen. (20s Zeit)");

        // Ein "Task", der nach 20 Sekunden die Anfrage automatisch entfernt.
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                // Pruefen, ob die Anfrage immer noch existiert, bevor wir sie entfernen.
                if (pendingConfirmations.containsKey(playerName)) {
                    pendingConfirmations.remove(playerName);
                    Player p = plugin.getServer().getPlayer(playerName);
                    if (p != null) {
                        p.sendMessage("§c[MarketSystem] §fDeine Anfrage zum Mieten des Shops ist abgelaufen.");
                    }
                }
            }
        }, 20 * 20L); // 20 Ticks pro Sekunde * 20 Sekunden
    }

    public boolean hasPendingConfirmation(Player player) {
        return pendingConfirmations.containsKey(player.getName());
    }

    public Shop getPendingShop(Player player) {
        return pendingConfirmations.get(player.getName());
    }

    public void removePendingConfirmation(Player player) {
        pendingConfirmations.remove(player.getName());
    }
}