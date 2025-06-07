package org.example.com.serverPerformance.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static org.example.com.serverPerformance.ServerPerformance.*;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!commandSender.isOp()) return false; // nur wenn Spieler op hat (basically nur ich)

        // Message format: /setperformance [ID] [Name]
        Player player = null;
        if (strings.length < 2) {
            commandSender.sendMessage("Zu wenige Parameter");
            return false;
        } else if (strings.length > 2) {
            commandSender.sendMessage("Zu viele Parameter");
            return false;
        }

        boolean isAPlayer = false;
        if (Bukkit.getPlayer(strings[1]) != null) {
            isAPlayer = true;
            player = Bukkit.getPlayer(strings[1]);
        } else {
            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                if (offlinePlayer.getName() != null) {
                    if (offlinePlayer.getName().equals(strings[1])) {
                        isAPlayer = true;
                    }
                }
            }

        }

        if (!isAPlayer) {
            commandSender.sendMessage("kein existierender Spieler");
            return false;
        }

        int ability = 0;
        // -----------------------------------------------------------
        switch(strings[0]) {
            case "0": // Liste an Abilities und zugeordnete Spieler - command wäre hier /setperformance 0 Pagro21, braucht halt auch einen random Namen
                commandSender.sendMessage("1: " + PlayerAbilityOne);
                commandSender.sendMessage("2: " + PlayerAbilityTwo);
                commandSender.sendMessage("3: " + PlayerAbilityThree);
                commandSender.sendMessage("4: " + PlayerAbilityFour);
                commandSender.sendMessage("5: " + PlayerAbilityFive);
                commandSender.sendMessage("6: " + PlayerAbilitySix);
                commandSender.sendMessage("7: " + PlayerAbilitySeven);
                break;
            case "1":
                if (!PlayerAbilityOne.contains(strings[1])) {PlayerAbilityOne.add(strings[1]);
                ability = 1;}
                break;
            case "-1":
                if (PlayerAbilityOne.contains(strings[1])) {
                    PlayerAbilityOne.remove(strings[1]);
                    ability = -1;
                }
                break;
            case "2":
                if (!PlayerAbilityTwo.contains(strings[1])) PlayerAbilityTwo.add(strings[1]);
                ability = 2;
                break;
            case "-2":
                PlayerAbilityTwo.remove(strings[1]);
                ability = -2;
                break;
            case "3":
                if (!PlayerAbilityThree.contains(strings[1])) PlayerAbilityThree.add(strings[1]);
                ability = 3;
                break;
            case "-3":
                PlayerAbilityThree.remove(strings[1]);
                ability = -3;
                break;
            case "4":
                if (!PlayerAbilityFour.contains(strings[1])) PlayerAbilityFour.add(strings[1]);
                ability = 4;
                break;
            case "-4":
                PlayerAbilityFour.remove(strings[1]);
                ability = -4;
                break;
            case "5":
                if (!PlayerAbilityFive.contains(strings[1])) PlayerAbilityFive.add(strings[1]);
                ability = 5;
                break;
            case "-5":
                PlayerAbilityFive.remove(strings[1]);
                ability = -5;
                break;
            case "6":
                if (!PlayerAbilitySix.contains(strings[1])) PlayerAbilitySix.add(strings[1]);
                ability = 6;
                break;
            case "-6":
                PlayerAbilitySix.remove(strings[1]);
                ability = -6;
                break;
            case "7":
                if (!PlayerAbilitySeven.contains(strings[1])) PlayerAbilitySeven.add(strings[1]);
                ability = 7;
                break;
            case "-7":
                PlayerAbilitySeven.remove(strings[1]);
                ability = -7;
                break;
            default:
                commandSender.sendMessage("keine richtige Methode");
                return false;
        }


        if (player == null) {
            commandSender.sendMessage("Fähigkeit erfolgreich zugeteilt");
            return false;
        }

        switch(ability) {
            case 1:
                abilityOne(player);
                break;
            case -1:
                abilityOneUndo(player);
                break;
            case 5:
                abilityFive(player);
                break;
            case -5:
                abilityFiveUndo(player);
                break;
        }

        commandSender.sendMessage("Fähigkeit erfolgreich zugeteilt");
        return false;
    }

    public static void abilityOne(Player player) {
        Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(15);
        player.setHealth(player.getHealth() / 20 * 15);
        player.setHealthScale(20);
        player.setHealthScaled(true);
    }
    public static void abilityOneUndo(Player player) {
        Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(20);
        player.setHealth(player.getHealth() / 15 * 20);
        player.setHealthScaled(false);
    }
    public static void abilityFive(Player player) {
        player.setWalkSpeed((player.getWalkSpeed()) * 0.95f);
    }
    public static void abilityFiveUndo(Player player) {
        //Objects.requireNonNull(player.getAttribute(Attribute.MOVEMENT_SPEED)).setBaseValue(0.1);
        player.setWalkSpeed(0.2f);
    }

}
