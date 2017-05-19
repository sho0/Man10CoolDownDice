package cooldown.dice;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public final class Dice extends JavaPlugin implements Listener {

    long lastDiceTime = System.currentTimeMillis();
    long coolTime = 5000;
    String prefix = "§l[§d§lM§f§la§a§ln§f§l10§5§lDice§f§l]";


    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();
        coolTime = this.getConfig().getLong("config.DiceCoolDown");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
        Player p = (Player) sender;
        if(cmd.getName().equalsIgnoreCase("mdice")) {
            if (args.length == 0) {
                help(p);
                return true;
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("help")) {
                    help(p);
                    return true;
                }
                try {
                    if (!p.hasPermission("man10.mdice.roll")) {
                        p.sendMessage(prefix + "§4あなたには権限がありません");
                        return true;
                    }
                    if (!p.isOp()) {
                        if (System.currentTimeMillis() - lastDiceTime < coolTime) {
                            p.sendMessage(prefix + "現在クールダウン中です");
                            return false;
                        }
                        lastDiceTime = System.currentTimeMillis();
                    }
                    int i = Integer.parseInt(args[0]);
                    if (i < 1) {
                        p.sendMessage(prefix + "§4§l１以上の数を設定してください");
                        return false;
                    }
                    Random r = new Random();
                    int result = r.nextInt(i) + 1;
                    Bukkit.getServer().broadcastMessage(prefix + "§3§l" + p.getDisplayName() + "§3§lは§l" + ChatColor.YELLOW + "§l" + i + "§3§l面サイコロを振って" + ChatColor.YELLOW + "§l" + result + "§3§lが出た");
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    return true;
                } catch (NumberFormatException e) {
                    p.sendMessage(prefix + "§4§lDiceは2147483646以下の自然数を入力するか、ヘルプを参照してください");
                    return false;
                }
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("setcooltime")) {
                    if (!p.hasPermission("man10.dice.setcooltime")) {
                        p.sendMessage(prefix + "あなたには権限がありません");
                        return false;
                    }
                    try {
                        int i = Integer.parseInt(args[1]);
                        this.getConfig().set("config.DiceCoolDown", i);
                        p.sendMessage(prefix + "クールタイムを設定しました");
                        coolTime = this.getConfig().getLong("config.DiceCoolDown");
                        return true;
                    } catch (NumberFormatException e) {
                        p.sendMessage(prefix + "CoolTimeは整数を入れてください");
                        return true;
                    }
                }
            }
            help(p);
        }
        return false;
    }
    void help(Player p){
        p.sendMessage(prefix);
        p.sendMessage("§3§l/mdice <数字>:§2指定した面のサイコロを回します");
        p.sendMessage("§3§l/mdice help:§2あなたが今見ているものです");
        p.sendMessage("§3§l/mdice setcooltime <MillSeconds>:§2サイコロのクールタイムを設定します");
        p.sendMessage("§6§lcreated by Sho0");
    }
}
