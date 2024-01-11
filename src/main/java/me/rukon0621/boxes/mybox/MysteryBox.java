package me.rukon0621.boxes.mybox;

import me.rukon0621.guardians.helper.Configure;
import me.rukon0621.guardians.helper.Couple;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MysteryBox {
    private final ArrayList<Couple<String, Double>> data;
    private final List<String> specialList;
    private ItemStack boxItem;

    public MysteryBox(Configure config) {
        data = new ArrayList<>();
        specialList = config.getConfig().getStringList("specialList");
        for(String key : config.getConfig().getKeys(false)) {
            if(key.equals("boxItem")) {
                boxItem = config.getConfig().getItemStack(key, new ItemStack(Material.BARRIER));
                continue;
            }
            else if(key.equals("specialList")) continue;
            data.add(new Couple<>(key, config.getConfig().getDouble(key)));
        }
    }

    public List<String> getSpecialList() {
        return specialList;
    }

    public void openWindow(Player player) {
        new MysteryBoxWindow(player, this);
        player.playSound(player, Sound.BLOCK_ENDER_CHEST_OPEN, 1, 1.5f);
    }

    public ArrayList<Couple<String, Double>> getData() {
        return data;
    }

    public ItemStack getBoxItem() {
        return boxItem;
    }
}
