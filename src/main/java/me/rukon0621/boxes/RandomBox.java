package me.rukon0621.boxes;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class RandomBox implements ConfigurationSerializable {
    private final String boxName;
    private final HashMap<String, Integer> chanceMap;
    private ItemStack icon;

    public RandomBox(String name, HashMap<String, Integer> chanceMap, ItemStack icon) {
        boxName = name;
        this.chanceMap = chanceMap;
        this.icon = icon;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", boxName);
        map.put("chanceMap", chanceMap);
        map.put("icon", icon);
        return map;
    }

    public static RandomBox deserialize(Map<String, Object> map) {
        return new RandomBox((String) map.get("name"), (HashMap<String, Integer>) map.get("chanceMap"), (ItemStack) map.get("icon"));
    }

    public String getBoxName() {
        return boxName;
    }

    public HashMap<String, Integer> getChanceMap() {
        return chanceMap;
    }

    public ItemStack getIcon() {
        return icon;
    }
}
