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

/**
 * Command executor for the /market command and its subcommands.
 * Handles all administrative actions for regions and shops.
 */
public class MarketCommand implements CommandExecutor {

    private final MarketPlugin plugin;
    // Temporary storage for admin's corner selections. Maps player name to selected location.
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
            sender.sendMessage("This command can only be run by a player.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }

        // Route to the correct handler based on the first argument.
        switch (args[0].toLowerCase()) {
            case "help":
                sendHelpMessage(player);
                break;
            case "region":
                if (!player.isOp()) { player.sendMessage("§c[MarketSystem] §fYou do not have permission for this."); return true; }
                handleRegionCommand(player, args);
                break;

            case "shop":
                if (!player.isOp()) { player.sendMessage("§c[MarketSystem] §fYou do not have permission for this."); return true; }
                handleShopCommand(player, args);
                break;

            default:
                player.sendMessage("§c[MarketSystem] §fUnknown command. Use /market help.");
                break;
        }
        return true;
    }

    /**
     * Handles subcommands for /market shop (pos1, pos2, create, delete).
     */
    private void handleShopCommand(Player player, String[] args) {
        if (args.length < 2) { sendHelpMessage(player); return; }
        String shopAction = args[1].toLowerCase();
        String playerName = player.getName();

        switch (shopAction) {
            case "pos1":
                pos1Selections.put(playerName, player.getLocation());
                player.sendMessage("§a[MarketSystem] §fShop position 1 set!");
                break;
            case "pos2":
                pos2Selections.put(playerName, player.getLocation());
                player.sendMessage("§a[MarketSystem] §fShop position 2 set!");
                break;
            case "create":
                if (!pos1Selections.containsKey(playerName) || !pos2Selections.containsKey(playerName)) {
                    player.sendMessage("§c[MarketSystem] §fYou must set both shop positions first!");
                    return;
                }
                Location loc1 = pos1Selections.get(playerName);
                Location loc2 = pos2Selections.get(playerName);
                Region parentRegion = regionManager.getRegionAt(loc1);
                if (parentRegion == null || regionManager.getRegionAt(loc2) != parentRegion) {
                    player.sendMessage("§c[MarketSystem] §fBoth corner points must be inside the same market region!");
                    return;
                }
                Shop newShop = new Shop(parentRegion.getName(), loc1, loc2);
                shopManager.addShop(newShop);
                clearShopAreaAndPlaceSign(newShop);
                player.sendMessage("§a[MarketSystem] §fShop successfully created in region '" + parentRegion.getName() + "'!");
                pos1Selections.remove(playerName);
                pos2Selections.remove(playerName);
                break;

            case "delete":
                Shop shopToDelete = shopManager.getShopAt(player.getLocation());
                if (shopToDelete == null) {
                    player.sendMessage("§c[MarketSystem] §fYou are not standing inside a deletable shop plot.");
                    return;
                }
                shopManager.removeShop(shopToDelete);
                player.sendMessage("§a[MarketSystem] §fShop plot successfully deleted.");
                break;

            default:
                player.sendMessage("§c[MarketSystem] §fUnknown shop command.");
                break;
        }
    }

    /**
     * Handles subcommands for /market region (pos1, pos2, create, delete).
     */
    private void handleRegionCommand(Player player, String[] args) {
        if (args.length < 2) { sendHelpMessage(player); return; }
        String regionAction = args[1].toLowerCase();
        String playerName = player.getName();

        switch (regionAction) {
            case "pos1":
                pos1Selections.put(playerName, player.getLocation());
                player.sendMessage("§a[MarketSystem] §fRegion position 1 set!");
                break;
            case "pos2":
                pos2Selections.put(playerName, player.getLocation());
                player.sendMessage("§a[MarketSystem] §fRegion position 2 set!");
                break;
            case "create":
                if (args.length < 3) { player.sendMessage("§c[MarketSystem] §fPlease specify a name: /market region create <name>"); return; }
                if (!pos1Selections.containsKey(playerName) || !pos2Selections.containsKey(playerName)) { player.sendMessage("§c[MarketSystem] §fYou must set both positions first!"); return; }
                String regionName = args[2];
                if (regionManager.regionExists(regionName)) { player.sendMessage("§c[MarketSystem] §fA region with this name already exists!"); return; }
                Region newRegion = new Region(regionName, pos1Selections.get(playerName), pos2Selections.get(playerName));
                regionManager.addRegion(newRegion);
                player.sendMessage("§a[MarketSystem] §fRegion '" + regionName + "' successfully created and saved!");
                pos1Selections.remove(playerName);
                pos2Selections.remove(playerName);
                break;

            case "delete":
                if (args.length < 3) { player.sendMessage("§c[MarketSystem] §fPlease specify a name: /market region delete <name>"); return; }
                String regionToDelete = args[2];
                if (!regionManager.regionExists(regionToDelete)) { player.sendMessage("§c[MarketSystem] §fA region with this name was not found."); return; }

                // First, delete all shops within this region.
                shopManager.removeShopsInRegion(regionToDelete);

                // Then, delete the region itself.
                regionManager.removeRegion(regionToDelete);

                player.sendMessage("§a[MarketSystem] §fRegion '" + regionToDelete + "' and all shops within it have been deleted.");
                break;
            default:
                player.sendMessage("§c[MarketSystem] §fUnknown region command.");
                break;
        }
    }

    /**
     * Sends a formatted help message to the player.
     */
    private void sendHelpMessage(Player player) {
        player.sendMessage("§e--- [MarketSystem Help] ---");
        player.sendMessage("§6/market help §f- Shows this help message.");
        if (player.isOp()) {
            player.sendMessage("§c--- Admin: Regions ---");
            player.sendMessage("§6/market region pos1 §f- Set corner 1.");
            player.sendMessage("§6/market region pos2 §f- Set corner 2.");
            player.sendMessage("§6/market region create <Name> §f- Creates the market region.");
            player.sendMessage("§6/market region delete <Name> §f- Deletes a market region.");
            player.sendMessage("§c--- Admin: Shops ---");
            player.sendMessage("§6/market shop pos1 §f- Set shop corner 1.");
            player.sendMessage("§6/market shop pos2 §f- Set shop corner 2.");
            player.sendMessage("§6/market shop create §f- Creates the shop plot.");
            player.sendMessage("§6/market shop delete §f- Deletes the shop you are standing in.");
        }
    }

    /**
     * Clears a 10-block high area for a new shop and places the sign and its base block.
     * @param shop The newly created shop.
     */
    private void clearShopAreaAndPlaceSign(Shop shop) {
        Location corner = new Location(plugin.getServer().getWorld(shop.getWorldName()), (double) shop.getX1(), (double) shop.getGroundY(), (double) shop.getZ1());
        for (int x = shop.getX1(); x <= shop.getX2(); x++) {
            for (int z = shop.getZ1(); z <= shop.getZ2(); z++) {
                for (int y = shop.getGroundY(); y < shop.getGroundY() + 10; y++) {
                    if (y > 127) break;
                    corner.getWorld().getBlockAt(x, y, z).setType(Material.AIR);
                }
            }
        }
        Block baseBlock = corner.getWorld().getBlockAt(shop.getX1(), shop.getGroundY() - 1, shop.getZ1());
        baseBlock.setType(Material.STONE);
        Block signBlock = corner.getWorld().getBlockAt(shop.getX1(), shop.getGroundY(), shop.getZ1());
        signBlock.setType(Material.SIGN_POST);
        Sign sign = (Sign) signBlock.getState();
        sign.setLine(0, "[Market]");
        sign.setLine(1, "§aFor Sale");
        sign.update();
    }
}