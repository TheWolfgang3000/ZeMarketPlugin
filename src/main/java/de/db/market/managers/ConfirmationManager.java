package de.db.market.managers;

import de.db.market.MarketPlugin;
import de.db.market.data.Shop;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages pending /yes or /no confirmations from players for shop rentals.
 * This acts as a short-term memory to prevent players from confirming actions they didn't initiate.
 */
public class ConfirmationManager {

    private final MarketPlugin plugin;
    // Maps a player's name to the shop they are currently attempting to rent.
    private final Map<String, Shop> pendingConfirmations = new HashMap<>();

    public ConfirmationManager(MarketPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Adds a new pending confirmation for a player and starts a 20-second timeout.
     * @param player The player who initiated the rental.
     * @param shop The shop they want to rent.
     */
    public void addPendingConfirmation(Player player, Shop shop) {
        final String playerName = player.getName();
        pendingConfirmations.put(playerName, shop);
        player.sendMessage("§e[MarketSystem] §fDo you want to rent this shop?");
        player.sendMessage("§fType §a/yes§f to confirm, or §c/no§f to cancel. (20 seconds)");

        // Schedule a task to automatically cancel the request after 20 seconds.
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                // Only remove the request if it still exists (i.e., wasn't already confirmed/cancelled).
                if (pendingConfirmations.containsKey(playerName)) {
                    pendingConfirmations.remove(playerName);
                    Player p = plugin.getServer().getPlayer(playerName);
                    if (p != null) {
                        p.sendMessage("§c[MarketSystem] §fYour request to rent the shop has expired.");
                    }
                }
            }
        }, 20 * 20L); // 20 server ticks per second * 20 seconds
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