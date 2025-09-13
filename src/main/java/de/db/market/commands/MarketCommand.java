package de.db.market.commands;

import de.db.market.MarketPlugin;
import de.db.market.data.Region;
import de.db.market.data.Shop;
import de.db.market.managers.RegionManager;
import de.db.market.managers.ShopManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.HashMap;

public class MarketCommand implements CommandExecutor {

    private final MarketPlugin plugin;
    private final HashMap<String, Location> pos1Selections = new HashMap<>();
    private final HashMap<String, Location> pos2Selections = new HashMap<>();
    private final RegionManager regionManager;
    private final ShopManager shopManager;

    public MarketCommand(MarketPlugin plugin, RegionManager regionManager, ShopManager shopManager) {
        this.plugin = plugin;
        this.regionManager = regionManager;
        this.shopManager = shopManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgefuehrt werden.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help":
                sendHelpMessage(player);
                break;
            case "region":
                if (!player.isOp()) {
                    player.sendMessage("§c[MarketSystem] §fDafuer hast du keine Rechte!");
                    return true;
                }
                handleRegionCommand(player, args);
                break;
            case "shop":
                if (!player.isOp()) {
                    player.sendMessage("§c[MarketSystem] §fDafuer hast du keine Rechte!");
                    return true;
                }
                handleShopCommand(player, args);
                break;
            default:
                player.sendMessage("§c[MarketSystem] §fUnbekannter Befehl. Nutze /market help.");
                break;
        }
        return true;
    }

    // --- NEU EINGEFÜGTE (ALTE) METHODEN ---

    private void handleRegionCommand(Player player, String[] args) {
        if (args.length < 2) {
            sendHelpMessage(player);
            return;
        }

        String regionAction = args[1].toLowerCase();
        String playerName = player.getName();

        switch (regionAction) {
            case "pos1":
                pos1Selections.put(playerName, player.getLocation());
                player.sendMessage("§a[MarketSystem] §fPosition 1 gesetzt!");
                break;
            case "pos2":
                pos2Selections.put(playerName, player.getLocation());
                player.sendMessage("§a[MarketSystem] §fPosition 2 gesetzt!");
                break;
            case "create":
                if (args.length < 3) {
                    player.sendMessage("§c[MarketSystem] §fBitte gib einen Namen an: /market region create <name>");
                    return;
                }
                if (!pos1Selections.containsKey(playerName) || !pos2Selections.containsKey(playerName)) {
                    player.sendMessage("§c[MarketSystem] §fDu musst zuerst beide Positionen setzen!");
                    return;
                }

                String regionName = args[2];
                if (regionManager.regionExists(regionName)) {
                    player.sendMessage("§c[MarketSystem] §fEine Region mit diesem Namen existiert bereits!");
                    return;
                }

                Region newRegion = new Region(regionName, pos1Selections.get(playerName), pos2Selections.get(playerName));
                regionManager.addRegion(newRegion);

                player.sendMessage("§a[MarketSystem] §fRegion '" + regionName + "' erfolgreich erstellt und gespeichert!");
                pos1Selections.remove(playerName);
                pos2Selections.remove(playerName);
                break;
            case "delete":
                if (args.length < 3) {
                    player.sendMessage("§c[MarketSystem] §fBitte gib einen Namen an: /market region delete <name>");
                    return;
                }
                String regionToDelete = args[2];
                if (!regionManager.regionExists(regionToDelete)) {
                    player.sendMessage("§c[MarketSystem] §fEine Region mit diesem Namen wurde nicht gefunden.");
                    return;
                }

                regionManager.removeRegion(regionToDelete);
                player.sendMessage("§a[MarketSystem] §fRegion '" + regionToDelete + "' wurde geloescht.");
                break;
            default:
                player.sendMessage("§c[MarketSystem] §fUnbekannter Region-Befehl. Nutze pos1, pos2, create, delete.");
                break;
        }
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage("§e--- [MarketSystem Hilfe] ---");
        player.sendMessage("§6/market help §f- Zeigt diese Hilfe an.");
        if (player.isOp()) {
            player.sendMessage("§c--- Admin: Regionen ---");
            player.sendMessage("§6/market region pos1 §f- Setzt Eckpunkt 1.");
            player.sendMessage("§6/market region pos2 §f- Setzt Eckpunkt 2.");
            player.sendMessage("§6/market region create <Name> §f- Erstellt die Marktregion.");
            player.sendMessage("§6/market region delete <Name> §f- Loescht eine Marktregion.");
            player.sendMessage("§c--- Admin: Shops ---");
            player.sendMessage("§6/market shop pos1 §f- Setzt Eckpunkt 1 des Shops.");
            player.sendMessage("§6/market shop pos2 §f- Setzt Eckpunkt 2 des Shops.");
            player.sendMessage("§6/market shop create §f- Erstellt den Shop-Plot.");
        }
    }

    // --- BESTEHENDE SHOP-METHODEN ---

    private void handleShopCommand(Player player, String[] args) {
        if (args.length < 2) {
            sendHelpMessage(player);
            return;
        }
        String shopAction = args[1].toLowerCase();
        String playerName = player.getName();

        switch (shopAction) {
            case "pos1":
                pos1Selections.put(playerName, player.getLocation());
                player.sendMessage("§a[MarketSystem] §fShop-Position 1 gesetzt!");
                break;
            case "pos2":
                pos2Selections.put(playerName, player.getLocation());
                player.sendMessage("§a[MarketSystem] §fShop-Position 2 gesetzt!");
                break;
            case "create":
                if (!pos1Selections.containsKey(playerName) || !pos2Selections.containsKey(playerName)) {
                    player.sendMessage("§c[MarketSystem] §fDu musst zuerst beide Shop-Positionen setzen!");
                    return;
                }
                Location loc1 = pos1Selections.get(playerName);
                Location loc2 = pos2Selections.get(playerName);

                Region parentRegion = regionManager.getRegionAt(loc1);
                if (parentRegion == null || regionManager.getRegionAt(loc2) != parentRegion) {
                    player.sendMessage("§c[MarketSystem] §fBeide Eckpunkte muessen in der gleichen Marktregion liegen!");
                    return;
                }

                Shop newShop = new Shop(parentRegion.getName(), loc1, loc2);
                shopManager.addShop(newShop);
                clearShopAreaAndPlaceSign(newShop);
                player.sendMessage("§a[MarketSystem] §fShop erfolgreich in Region '" + parentRegion.getName() + "' erstellt!");
                pos1Selections.remove(playerName);
                pos2Selections.remove(playerName);
                break;
            default:
                player.sendMessage("§c[MarketSystem] §fUnbekannter Shop-Befehl.");
                break;
        }
    }

    // Ersetze diese Methode in MarketCommand.java
    private void clearShopAreaAndPlaceSign(Shop shop) {
        Location corner = new Location(
                plugin.getServer().getWorld(shop.getWorldName()),
                (double) shop.getX1(),
                (double) shop.getGroundY(), // Benutze die neue Bodenhoehe
                (double) shop.getZ1()
        );

        // Leere den Bereich 10 Bloecke hoch vom Boden aus
        for (int x = shop.getX1(); x <= shop.getX2(); x++) {
            for (int z = shop.getZ1(); z <= shop.getZ2(); z++) {
                for (int y = shop.getGroundY(); y < shop.getGroundY() + 10; y++) {
                    // Sicherheitscheck, damit wir nicht ueber Y=127 hinaus bauen
                    if (y > 127) break;
                    corner.getWorld().getBlockAt(x, y, z).setType(Material.AIR);
                }
            }
        }

        // Platziere ein Schild auf dem Boden
        Block signBlock = corner.getWorld().getBlockAt(shop.getX1(), shop.getGroundY(), shop.getZ1());
        signBlock.setType(Material.SIGN_POST);
        Sign sign = (Sign) signBlock.getState();
        sign.setLine(0, "[Market]");
        sign.setLine(1, "§aFor Sale");
        sign.update();
    }
}