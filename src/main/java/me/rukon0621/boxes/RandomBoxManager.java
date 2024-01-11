package me.rukon0621.boxes;

import me.rukon0621.boxes.windows.ItemShowWindow;
import me.rukon0621.guardians.data.ItemData;
import me.rukon0621.guardians.events.ItemClickEvent;
import me.rukon0621.guardians.helper.Configure;
import me.rukon0621.guardians.helper.ItemClass;
import me.rukon0621.guardians.helper.Msg;
import me.rukon0621.guardians.helper.Rand;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static me.rukon0621.guardians.main.pfix;

public class RandomBoxManager implements Listener {
    private final HashMap<String, RandomBox> boxData = new HashMap<>();
    private final RukonBoxes plugin = RukonBoxes.inst();
    private static final String TYPE_NAME = "랜덤박스";

    public Configure getConfig(String boxName) {
        return new Configure(boxName+".yml", plugin.getDataFolder() +"/randomBoxes");
    }

    public RandomBoxManager() {
        reloadAllBoxes();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public HashMap<String, RandomBox> getBoxData() {
        return boxData;
    }

    public Set<String> getBoxNames() {
        Set<String> set = new HashSet<>();
        for(RandomBox randomBox : boxData.values()) {
            set.add(randomBox.getBoxName());
        }
        return set;
    }

    public void reloadAllBoxes() {
        boxData.clear();
        for(File file : new File(plugin.getDataFolder()+"/randomBoxes").listFiles()) {
            String boxName = file.getName().replaceAll(".yml", "");
            Configure config = new Configure(file);
            RandomBox randomBox = (RandomBox) config.getConfig().get(boxName);
            boxData.put(randomBox.getBoxName(), randomBox);
        }
    }

    public void createNewRandomBox(Player player, String boxName) {
        if(boxData.containsKey(boxName)) {
            Msg.warn(player, "이미 존재하는 이름의 랜덤 박스입니다.");
            return;
        }
        HashMap<String, Integer> chanceMap = new HashMap<>();
        chanceMap.put("ItemSaver:MinMinusLv:MaxMinusLv", 60);
        chanceMap.put("경험치:10", 50);
        chanceMap.put("수표:10", 30);
        ItemClass item = new ItemClass(new ItemStack(Material.SCUTE), "&e" + boxName);
        item.addLore("&f여러 아이템이 등장하는 랜덤 박스이다.");
        item.addLore("&6상자를 들고 우클릭&e해서 사용할 수 있습니다.");
        item.setCustomModelData(1000);
        ItemData itemData = new ItemData(item);
        itemData.setLevel(0);
        itemData.setType("랜덤박스");
        itemData.setUntradable(true);
        RandomBox box = new RandomBox(boxName, chanceMap, itemData.getItemStack());
        Configure config = getConfig(boxName);
        config.getConfig().set(boxName, box);
        config.saveConfig();
        boxData.put(boxName, box);
        Msg.send(player, "성공적으로 생성했습니다.", pfix);
    }

    public void deleteRandomBox(Player player, String boxName) {
        if(!boxData.containsKey(boxName)) {
            Msg.warn(player, "존재하지 않는 이름의 랜덤 박스입니다.");
            return;
        }
        Configure config = getConfig(boxName);
        config.delete();
        boxData.remove(boxName);
        Msg.send(player, "랜덤 박스를 삭제했습니다.", pfix);
    }

    public void setIconOfBox(Player player, String boxName, ItemClass item) {
        if(!boxData.containsKey(boxName)) {
            Msg.warn(player, "존재하지 않는 이름의 랜덤 박스입니다.");
            return;
        }
        RandomBox box = boxData.get(boxName);
        ItemData itemData = new ItemData(item);
        itemData.setType(TYPE_NAME);
        itemData.setUntradable(true);
        itemData.setLevel(0);
        ItemClass it = new ItemClass(itemData.getItemStack());
        box.setIcon(it.getItem());
        boxData.put(boxName, box);
        Configure config = getConfig(boxName);
        config.getConfig().set(boxName, box);
        config.saveConfig();
        Msg.send(player, "성공적으로 설정했습니다.", pfix);
    }

    public void giveRandomBox(Player player, String boxName) {
        if(!boxData.containsKey(boxName)) {
            Msg.warn(player, "존재하지 않는 이름의 랜덤 박스입니다.");
            return;
        }
        RandomBox box = boxData.get(boxName);
        player.getInventory().addItem(box.getIcon());
        Msg.send(player, "아이템을 지급 받았습니다.", pfix);
    }

    @EventHandler
    public void onClickBox(ItemClickEvent e) {
        if(!e.getItemData().getType().equals(TYPE_NAME)) return;
        String name = Msg.uncolor(e.getItemData().getName());
        if(!boxData.containsKey(name)) return;
        RandomBox box = boxData.get(name);
        Player player = e.getPlayer();
        new ItemShowWindow(player, box);
        player.playSound(player, Sound.BLOCK_ENDER_CHEST_OPEN, 1, Rand.randFloat(0.8, 1.3));
    }
}
