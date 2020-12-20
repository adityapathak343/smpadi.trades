package me.smpadi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Hashtable;

public class trades extends JavaPlugin {
    @Override
    public void onEnable(){
        //on enable of server
    }

    @Override
    public void onDisable(){
        //on disable of server
    }
    Hashtable<Player, Player> senders = new Hashtable<>();
    Hashtable<Player, Player> receivers = new Hashtable<>();
    Hashtable<Player, ItemStack> items = new Hashtable<>();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if(label.equalsIgnoreCase("trade")) {
            if (sender instanceof Player) {
                Player s = (Player) sender;
                ItemStack itemToTrade = s.getInventory().getItemInMainHand();
                s.getInventory().removeItem(itemToTrade);
                Player r = Bukkit.getPlayer(args[0]);
                String message = "";
                if (!(args.length == 1)){
                    message = String.join(" ", Arrays.asList(args).subList(1, args.length).toArray(new String[]{}));
                }

                r.sendMessage(ChatColor.YELLOW+"Player " + s.getName() + " wants to trade " + itemToTrade.getItemMeta().getDisplayName() +
                        '['+itemToTrade.getType().toString()+']'+'(' + itemToTrade.getAmount() + ')' + " with the message " + message);
                r.sendMessage(ChatColor.YELLOW+ "Hold item to trade and type /yestrade to accept or /notrade to reject...");
                senders.put(s, r);
                receivers.put(r, s);
                items.put(s, itemToTrade);
            }
        }
        if(receivers.containsKey(player) && senders.containsKey(player)){
            player.sendMessage(ChatColor.RED+"You have a trade in session");
        }
        else {
            if (label.equalsIgnoreCase("yestrade") && receivers.containsKey(player)) {
                Player r = (Player) sender;
                ItemStack trading = r.getInventory().getItemInMainHand();
                r.getInventory().removeItem(trading);
                Player s = receivers.get(r);
                s.sendMessage(ChatColor.YELLOW+"Player " + r.getName() + " wants to trade " + trading.getItemMeta().getDisplayName() +
                        '['+trading.getData().toString()+']' + '(' +
                        trading.getAmount() + ')');
                s.sendMessage("Type /accept to accept, or /reject to reject");
                items.put(r, trading);
            }

            else if (label.equalsIgnoreCase("notrade") && receivers.containsKey(player)) {
                Player r = (Player) sender;
                Player s = receivers.get(r);
                ItemStack itemToTrade = items.get(s);
                s.sendMessage(ChatColor.RED+"Trade Rejected!");
                s.getInventory().addItem(itemToTrade);
                senders.remove(s);
                receivers.remove(r);
                items.remove(s);
            }
            else if (label.equalsIgnoreCase("accept") && senders.containsKey(player)) {
                Player s = (Player) sender;
                Player r = senders.get(s);
                r.sendMessage(ChatColor.GREEN+"Trade Complete!");
                ItemStack trading = items.get(r);
                ItemStack itemToTrade = items.get(s);
                s.getInventory().addItem(trading);
                r.getInventory().addItem(itemToTrade);
                senders.remove(s);
                receivers.remove(r);
                items.remove(s);
            }
            else if (label.equalsIgnoreCase("reject") && senders.containsKey(player)) {
                Player s = (Player) sender;
                Player r = senders.get(s);
                r.sendMessage(ChatColor.RED+"Trade Rejected!");
                ItemStack trading = items.get(r);
                ItemStack itemToTrade = items.get(s);
                r.getInventory().addItem(trading);
                s.getInventory().addItem(itemToTrade);
                senders.remove(s);
                receivers.remove(r);
                items.remove(s);
            }
            else if (label.equalsIgnoreCase("trade")){
                //ignore
            }
            else{
                //hopefully only triggered when the player is not trading but still attempts to issue a command
                player.sendMessage("You do not have any trades in progress!");
            }
        }
        return false;
    }
}
