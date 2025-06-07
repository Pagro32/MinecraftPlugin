package org.example.com.serverPerformance;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.example.com.serverPerformance.WorldControlling.EventListener;
import org.example.com.serverPerformance.commands.Commands;

import java.util.ArrayList;

public final class ServerPerformance extends JavaPlugin implements Listener {

    public static ArrayList<String> PlayerAbilityOne = new ArrayList<>();
    public static ArrayList<String> PlayerAbilityTwo = new ArrayList<>();
    public static ArrayList<String> PlayerAbilityThree = new ArrayList<>();
    public static ArrayList<String> PlayerAbilityFour = new ArrayList<>();
    public static ArrayList<String> PlayerAbilityFive = new ArrayList<>();
    public static ArrayList<String> PlayerAbilitySix = new ArrayList<>();
    public static ArrayList<String> PlayerAbilitySeven = new ArrayList<>();
    @Override
    public void onEnable() {

        // config
        PlayerAbilityOne = new ArrayList<>(getConfig().getStringList("PlayerAbilityOne"));
        PlayerAbilityTwo = new ArrayList<>(getConfig().getStringList("PlayerAbilityTwo"));
        PlayerAbilityThree = new ArrayList<>(getConfig().getStringList("PlayerAbilityThree"));
        PlayerAbilityFour = new ArrayList<>(getConfig().getStringList("PlayerAbilityFour"));
        PlayerAbilityFive = new ArrayList<>(getConfig().getStringList("PlayerAbilityFive"));
        PlayerAbilitySix = new ArrayList<>(getConfig().getStringList("PlayerAbilitySix"));
        PlayerAbilitySeven = new ArrayList<>(getConfig().getStringList("PlayerAbilitySeven"));

        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getPluginManager().registerEvents(new EventListener(), this);

        getCommand("setPerformance").setExecutor(new Commands());
        new Commands();

    }

    @Override
    public void onDisable() {
        getConfig().set("PlayerAbilityOne", PlayerAbilityOne);
        getConfig().set("PlayerAbilityTwo", PlayerAbilityTwo);
        getConfig().set("PlayerAbilityThree", PlayerAbilityThree);
        getConfig().set("PlayerAbilityFour", PlayerAbilityFour);
        getConfig().set("PlayerAbilityFive", PlayerAbilityFive);
        getConfig().set("PlayerAbilitySix", PlayerAbilitySix);
        getConfig().set("PlayerAbilitySeven", PlayerAbilitySeven);

        saveConfig();
    }
}
