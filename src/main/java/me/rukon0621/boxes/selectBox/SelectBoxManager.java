package me.rukon0621.boxes.selectBox;

import me.rukon0621.boxes.RukonBoxes;
import me.rukon0621.guardians.data.ItemData;
import me.rukon0621.guardians.events.ItemClickEvent;
import me.rukon0621.guardians.helper.Configure;
import me.rukon0621.guardians.helper.Couple;
import me.rukon0621.guardians.helper.ItemClass;
import me.rukon0621.guardians.helper.Msg;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static me.rukon0621.guardians.main.pfix;

public class SelectBoxManager implements Listener {
    private final RukonBoxes plugin = RukonBoxes.inst();
    private final Map<String, Map<Integer, Couple<String, Integer>>> boxMap = new HashMap<>();
    private static final String BOX_TYPE = "선택형 미스터리 박스";

    public SelectBoxManager()  {
        getSourceFolder().mkdir();
        reloadBoxes();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private File getSourceFolder() {
        return new File(plugin.getDataFolder() +"/selectBoxes");
    }

    private Configure getConfig(String boxName) {
        if(!boxName.endsWith(".yml")) boxName += ".yml";
        return new Configure(boxName, getSourceFolder().getPath());
    }

    public void reloadBoxes() {
        boxMap.clear();
        for(File file : getSourceFolder().listFiles()) {
            String name = file.getName();
            name = name.replaceAll(".yml", "");
            try {
                Configure config = getConfig(name);
                Map<Integer, Couple<String, Integer>> map = new HashMap<>();
                for(String key : config.getConfig().getKeys(false)) {
                    int slot = Integer.parseInt(key);

                    String[] data = config.getConfig().getString(key).split(":");
                    String itemSaver = data[0].trim();
                    int amount = data.length >= 2 ? Integer.parseInt(data[1]) : 1;
                    map.put(slot, new Couple<>(itemSaver, amount));
                }
                boxMap.put(name, map);
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getLogger().warning(name + "을 로드하는 중 오류가 발생했습니다.");
            }
        }

    }

    public Set<String> getBoxNames() {
        return boxMap.keySet();
    }

    public void createBox(Player player, String name) {
        if(boxMap.containsKey(name)) {
            Msg.warn(player, "이미 존재하는 이름의 박스입니다.");
            return;
        }
        Configure config = getConfig(name);
        config.getConfig().set("slot", "ItemSaver");
        config.saveConfig();
        Msg.send(player, "박스를 생성했습니다.", pfix);
        reloadBoxes();
    }

    public void deleteBox(Player player, String name) {
        if(!boxMap.containsKey(name)) {
            Msg.warn(player, "없는 이름의 박스입니다.");
            return;
        }
        Msg.send(player, "박스를 삭제했습니다.", pfix);
        reloadBoxes();
    }

    public void giveBox(Player player, String name) {
        ItemClass item = new ItemClass(new ItemStack(Material.SCUTE), "&e" + name);
        item.setCustomModelData(1000);
        item.addLore("&f특정 아이템을 선택해 지급 받을 수 있는 선택형 미스터리 박스다.");
        ItemData id = new ItemData(item);
        id.setType(BOX_TYPE);
        id.setLevel(0);
        player.getInventory().addItem(id.getItemStack());
    }

    @EventHandler
    public void onClickSelectBox(ItemClickEvent e) {
        if(!e.getItemData().getType().equals(BOX_TYPE)) return;
        String name = Msg.uncolor(e.getItemData().getName());
        if(!boxMap.containsKey(name)) return;
        new SelectBoxWindow(e.getPlayer(), e.getPlayer().getInventory().getItemInMainHand(), boxMap.get(name));
    }

}
