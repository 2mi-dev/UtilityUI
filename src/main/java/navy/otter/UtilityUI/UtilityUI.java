package navy.otter.UtilityUI;

import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class UtilityUI extends JavaPlugin implements CommandExecutor {

  private final HashMap<UUID, Inventory> brewingMap = new HashMap<>();
  private final HashMap<UUID, HashMap<String, Inventory>> playerCustomChests = new HashMap<>();

  @Override
  public void onEnable() {
    this.getCommand("uui").setExecutor(this);
    this.getCommand("uui").setTabCompleter(new UtiltiyTabCompleter());
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    if (!(sender instanceof Player)) {
      return false;
    }

    final Player player = (Player) sender;

    Iterator<String> arg = Arrays.asList(args).iterator();
    String option = arg.hasNext() ? arg.next() : "";
    String argument = arg.hasNext() ? arg.next() : "";
    String detail = arg.hasNext() ? arg.next() : "";

    boolean result = false;

    switch (option.toLowerCase()) {
      case "":
        result = false;
        break;
      case "debug":
        if (player.hasPermission("UtilityUI.debugstick")) {
          result = giveDebugStick(player);
        }
        break;
      case "cmd":
        if (player.hasPermission("UtilityUI.commandblock")) {
          result = giveCommandBlock(player);
        }
        break;
      case "brew":
        if (player.hasPermission("UtilityUI.brewing")) {
          result = showBrewingGui(player);
        }
        break;
      case "wb":
        if (player.hasPermission("UtilityUI.workbench")) {
          result = showWorkbenchGui(player);
        }
        break;
      case "chests":
        if (player.hasPermission("UtilityUI.chests")) {
          result = chestManager(player, argument, detail);
        }
        break;
      default:
        player.sendMessage(ChatColor.RED + "Der eingegebene Befehl existiert nicht!");
    }
    return result;
  }

  public boolean giveDebugStick(Player player) {
    PlayerInventory playerinventory = player.getInventory();
    if (hasFreeSlot(playerinventory)) {
      playerinventory.addItem(new ItemStack(Material.DEBUG_STICK, 1));
      return true;
    } else {
      String sb =
          ChatColor.DARK_GRAY + "["
              + ChatColor.AQUA + "UtilityUI"
              + ChatColor.DARK_GRAY + "] "
              + ChatColor.RED
              + "Dein Inventar ist voll!";
      player.sendMessage(sb);
      return false;
    }
  }

  public boolean giveCommandBlock(Player player) {
    PlayerInventory playerinventory = player.getInventory();
    if (hasFreeSlot(playerinventory)) {
      playerinventory.addItem(new ItemStack(Material.COMMAND_BLOCK, 1));
      return true;
    } else {
      String sb =
          ChatColor.DARK_GRAY + "["
              + ChatColor.AQUA + "UtilityUI"
              + ChatColor.DARK_GRAY + "] "
              + ChatColor.RED
              + "Dein Inventar ist voll!";
      player.sendMessage(sb);
      return false;
    }
  }
  //todo: uuid statt player
  public boolean chestManager(Player player, String argument, String detail) {
    switch (argument.toLowerCase()) {
      case "add":
        if (detail.isEmpty()) {
          return false;
        }
        createNewChest(this.playerCustomChests, player, detail);
        player.openInventory(this.playerCustomChests.get(player.getUniqueId()).get(detail));
        break;
      case "open":
        if (detail.isEmpty()) {
          return false;
        }
        if (!this.playerCustomChests.containsKey(player.getUniqueId())) {
          String sb =
              ChatColor.DARK_GRAY + "["
                  + ChatColor.AQUA + "UtilityUI"
                  + ChatColor.DARK_GRAY + "] "
                  + ChatColor.RED
                  + "Du hast noch keine Kisten erstellt.";
          player.sendMessage(sb);
          return false;
        }
        if (!this.playerCustomChests.get(player.getUniqueId()).containsKey(detail)) {
          String sb =
              ChatColor.DARK_GRAY + "["
                  + ChatColor.AQUA + "UtilityUI"
                  + ChatColor.DARK_GRAY + "] "
                  + ChatColor.RED
                  + "Diese Kiste existiert nicht.";
          player.sendMessage(sb);
          return false;
        }
        player.openInventory(this.playerCustomChests.get(player.getUniqueId()).get(detail));
        break;
      case "list":
        if (!this.playerCustomChests.containsKey(player.getUniqueId())) {
          String sb =
              ChatColor.DARK_GRAY + "["
                  + ChatColor.AQUA + "UtilityUI"
                  + ChatColor.DARK_GRAY + "] "
                  + ChatColor.RED
                  + "Du hast noch keine Kisten erstellt.";
          player.sendMessage(sb);
          return false;
        }
        Set<String> keys = this.playerCustomChests.get(player.getUniqueId()).keySet();
        String str =
            ChatColor.DARK_GRAY + "["
                + ChatColor.AQUA + "UtilityUI"
                + ChatColor.DARK_GRAY + "] "
                + ChatColor.GRAY + "Deine temp. Chests:";
        player.sendMessage(str);
        player.sendMessage(ChatColor.YELLOW + "------------------------");
        for (String key : keys) {
          player.sendMessage(ChatColor.GRAY + key);
        }
        player.sendMessage(ChatColor.YELLOW + "------------------------");
        break;
      default:
        String sb =
            ChatColor.DARK_GRAY + "["
                + ChatColor.AQUA + "UtilityUI"
                + ChatColor.DARK_GRAY + "] "
                + ChatColor.RED
                + "Argumente ungültig. Nutze /ui chests <open|add|list> <name>!";
        player.sendMessage(sb);
        return false;
    }
    return true;
  }

  private void createNewChest(HashMap<UUID, HashMap<String, Inventory>> chestMap, Player player,
      String chestName) {
    Inventory chest = Bukkit.createInventory(player, InventoryType.CHEST, chestName);
    if (!chestMap.containsKey(player.getUniqueId())) {
      chestMap.put(player.getUniqueId(), new HashMap<>());
    }
    chestMap.get(player.getUniqueId()).put(chestName, chest);
  }

  public boolean showWorkbenchGui(Player player) {
    player.openWorkbench(null, true);
    return true;
  }

  public boolean showBrewingGui(Player player) {
    if (brewingMap.get(player.getUniqueId()) == null) {
      Inventory brewing = Bukkit.createInventory(player, InventoryType.BREWING);
      player.openInventory(brewing);
      brewingMap.put(player.getUniqueId(), brewing);
    } else {
      Inventory brewing = brewingMap.get(player.getUniqueId());
      player.openInventory(brewing);
    }
    return true;
  }

  public boolean hasFreeSlot(PlayerInventory inventory) {
    return inventory.firstEmpty() != -1;
  }
}
