package de.db.market.commands;

import de.db.market.MarketPlugin; // Importieren
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

    private final MarketPlugin plugin; // NEU: Eine Referenz zur Hauptklasse
    private final HashMap<String, Location> pos1Selections = new HashMap<>();
    private final HashMap<String, Location> pos2Selections = new HashMap<>();
    private final RegionManager regionManager;
    private final ShopManager shopManager;

    // NEU: Der Konstruktor akzeptiert jetzt auch das Plugin
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
        if (args.length == 0) { sendHelpMessage(player); return true; }

        switch (args[0].toLowerCase()) {
            case "help": sendHelpMessage(player); break;
            case "region":
                if (!player.isOp()) { player.sendMessage("§c[MarketSystem] §fDafuer hast du keine Rechte!"); return true; }
                handleRegionCommand(player, args);
                break;
            case "shop":
                if (!player.isOp()) { player.sendMessage("§c[MarketSystem] §fDafuer hast du keine Rechte!"); return true; }
                handleShopCommand(player, args);
                break;
            default:
                player.sendMessage("§c[MarketSystem] §fUnbekannter Befehl. Nutze /market help."); break;
        }
        return true;
    }

    private void handleShopCommand(Player player, String[] args) {
        // ... dieser Teil bleibt unveraendert ...
        if (args.length < 2) { sendHelpMessage(player); return; }
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

    // KORRIGIERTE METHODE
    private void clearShopAreaAndPlaceSign(Shop shop) {
        // Wir holen uns die Welt jetzt ueber die Plugin-Instanz
        Location corner = new Location(
                plugin.getServer().getWorld(shop.getWorldName()),
                (double) shop.getX1(), // KORREKTUR: int zu double umwandeln
                (double) shop.getY1(), // KORREKTUR: int zu double umwandeln
                (double) shop.getZ1()  // KORREKTUR: int zu double umwandeln
        );

        // Leere den Bereich 10 Bloecke hoch
        for (int x = shop.getX1(); x <= shop.getX2(); x++) {
            for (int z = shop.getZ1(); z <= shop.getZ2(); z++) {
                // Wir starten am Boden des Shops und gehen 10 Blöcke hoch
                for (int y = shop.getY1(); y < shop.getY1() + 10; y++) {
                    corner.getWorld().getBlockAt(x, y, z).setType(Material.AIR);
                }
            }
        }

        // Platziere ein Schild
        Block signBlock = corner.getWorld().getBlockAt(shop.getX1(), shop.getY1(), shop.getZ1());
        signBlock.setType(Material.SIGN_POST);
        Sign sign = (Sign) signBlock.getState(); // Wichtig: Zustand des Blocks holen
        sign.setLine(0, "[Market]");
        sign.setLine(1, "§aFor Sale");
        sign.update(); // Wichtig: Aenderungen auf das Schild anwenden
    }

    // Unveraenderte Methoden (gekürzt)
    private void handleRegionCommand(Player player, String[] args) { /* ... bleibt gleich ... */ }
    private void sendHelpMessage(Player player) { /* ... bleibt gleich, aber ergänze Shop-Befehle ... */ }
}