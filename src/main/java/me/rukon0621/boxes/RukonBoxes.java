package me.rukon0621.boxes;

import me.rukon0621.boxes.commands.RandomBoxCommand;
import me.rukon0621.boxes.mybox.MysteryBoxCommand;
import me.rukon0621.boxes.mybox.MysteryBoxManager;
import me.rukon0621.boxes.selectBox.SelectBoxCommand;
import me.rukon0621.boxes.selectBox.SelectBoxManager;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public class RukonBoxes extends JavaPlugin {

    private static RukonBoxes inst;
    public static RukonBoxes inst() {
        return inst;
    }
    private RandomBoxManager randomBoxManager;
    private MysteryBoxManager MysteryBoxManager;
    private SelectBoxManager selectBoxManager;

    @Override
    public void onLoad() {
        ConfigurationSerialization.registerClass(RandomBox.class);
    }

    @Override
    public void onEnable() {
        getDataFolder().mkdir();
        inst = this;
        randomBoxManager = new RandomBoxManager();
        MysteryBoxManager = new MysteryBoxManager();
        selectBoxManager = new SelectBoxManager();

        new RandomBoxCommand();
        new MysteryBoxCommand();
        new SelectBoxCommand();
    }

    @Override
    public void onDisable() {

    }

    public SelectBoxManager getSelectBoxManager() {
        return selectBoxManager;
    }

    public RandomBoxManager getRandomBoxManager() {
        return randomBoxManager;
    }

    public me.rukon0621.boxes.mybox.MysteryBoxManager getMysteryBoxManager() {
        return MysteryBoxManager;
    }
}
