package me.rukon0621.boxes.mybox;

import me.rukon0621.boxes.RukonBoxes;
import me.rukon0621.guardians.data.ItemData;
import me.rukon0621.guardians.data.LevelData;
import me.rukon0621.guardians.events.ItemClickEvent;
import me.rukon0621.guardians.helper.Configure;
import me.rukon0621.guardians.helper.ItemClass;
import me.rukon0621.guardians.helper.ItemSaver;
import me.rukon0621.guardians.helper.Msg;
import me.rukon0621.guardians.main;
import me.rukon0621.pay.RukonPayment;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static me.rukon0621.guardians.main.pfix;

public class MysteryBoxManager implements Listener {
    private final RukonBoxes plugin = RukonBoxes.inst();
    private final Map<String, MysteryBox> boxMap = new HashMap<>();
    private final Set<Player> blockOpen = new HashSet<>();

    private Configure getConfig(String name) {
        return new Configure(name+".yml", plugin.getDataFolder() +"/mysteryBoxes");
    }

    public MysteryBoxManager() {
        new File(plugin.getDataFolder()+"/mysteryBoxes").mkdir();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        reloadAllBoxes();
    }

    public void reloadAllBoxes() {
        boxMap.clear();
        for(File file : new File(plugin.getDataFolder()+"/mysteryBoxes").listFiles()) {
            String name = file.getName().replaceAll(".yml", "");
            Configure config = getConfig(name);
            double total = 0;
            Map<String, Double> map = new HashMap<>();
            for(String key : config.getConfig().getKeys(false)) {
                if(key.equals("boxItem")) continue;
                else if(key.equals("specialList")) continue;
                total += config.getConfig().getDouble(key);
                map.put(key, config.getConfig().getDouble(key));
            }
            for(String key : map.keySet()) {
                config.getConfig().set(key, Math.round(map.get(key) / total * 10000)/100.0);
            }
            config.saveConfig();
            boxMap.put(name, new MysteryBox(config));
        }
    }

    public ItemStack parsedItemSaver(String s) {
        return parsedItemSaver(s, 0);
    }

    //아이템::양::렙다운::
    public ItemStack parsedItemSaver(String s, int playerLevel) {
        if(s.startsWith("청사진:")) return main.getPlugin().getBluePrintManager().getBlueprintItem(s.replaceFirst("청사진:", "").trim());
        //else if (s.startsWith("경험치:")) return LevelData.getExpBook(Integer.parseInt(s.replaceFirst("경험치:", "")));
        else if (s.startsWith("경험치:")) return LevelData.getEquipmentExpBook(Integer.parseInt(s.replaceFirst("강화경험치:", "")));
        else if (s.startsWith("강화경험치:")) return LevelData.getEquipmentExpBook(Integer.parseInt(s.replaceFirst("강화경험치:", "")));
        else if (s.startsWith("돈:")) return LevelData.getDinarItem(Integer.parseInt(s.replaceFirst("돈:", "")));
        else if (s.startsWith("루나르:")) return RukonPayment.inst().getPaymentManager().getRunarItem(Integer.parseInt(s.replaceFirst("루나르:", "")));

        String[] data = s.split("::");
        ItemData itemData = ItemSaver.getItemDataParsed(data[0], 0);
        if(data.length >= 2) {
            itemData.setAmount(Integer.parseInt(data[1]));
        }
        if(data.length >= 3) {
            itemData.setLevel(Math.max(1, playerLevel -Integer.parseInt(data[2])));
        }
        return itemData.getItemStack();
    }

    public Set<String> getBoxNames() {
        return boxMap.keySet();
    }

    public void createNewMysteryBox(Player player, String name) {
        if(getBoxNames().contains(name)) {
            Msg.warn(player, "해당 이름은 이미 존재합니다.");
            return;
        }
        Configure config = getConfig(name);
        ItemClass item = new ItemClass(new ItemStack(Material.CHEST), "&c랜덤 박스");
        item.addLore("&7우클릭하여 이 아이템을 사용할 수 있습니다.");
        ItemData itemData = new ItemData(item);
        itemData.setType("미스터리 박스");
        itemData.setLevel(0);
        config.getConfig().set("boxItem", itemData.getItemStack());
        config.getConfig().set("ItemSaverName", 12.5D);
        config.getConfig().set("ItemSaverName::Amount", 12.5D);
        config.getConfig().set("ItemSaverName::Amount::Level", 12.5D);
        config.getConfig().set("루나르:500", 12.5D);
        config.getConfig().set("디나르:500", 12.5D);
        config.getConfig().set("경험치:5", 12.5D);
        config.getConfig().set("강화경험치:5", 12.5D);
        config.getConfig().set("청사진:blueprintName", 12.5D);
        config.saveConfig();
        player.getInventory().addItem(itemData.getItemStack());
        reloadAllBoxes();
        Msg.send(player, "미스터리 박스를 생성했습니다.", pfix);
    }

    public void deleteMysteryBox(Player player, String name) {
        if(!getBoxNames().contains(name)) {
            Msg.warn(player, "해당 이름의 박스는 존재하지 않습니다.");
            return;
        }
        Configure config = getConfig(name);
        config.delete("mysteryBoxes");
        reloadAllBoxes();
        Msg.send(player, "성공적으로 박스를 삭제했습니다.", pfix);
    }

    public void setBoxItem(Player player, String name) {
        if(!getBoxNames().contains(name)) {
            Msg.warn(player, "해당 이름의 박스는 존재하지 않습니다.");
            return;
        }
        if(player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
            Msg.warn(player, "손에 아이템을 들어주세요.");
            return;
        }
        ItemStack i = player.getInventory().getItemInMainHand();
        Configure config = getConfig(name);
        player.getInventory().setItemInMainHand(config.getConfig().getItemStack("boxItem", new ItemStack(Material.CHEST)));
        config.getConfig().set("boxItem", i);
        config.saveConfig();
        Msg.send(player, "아이템을 설정하고 기존 아이템을 되돌려 받았습니다.", pfix);
    }
    public void giveBoxItem(Player player, String name) {
        if(!getBoxNames().contains(name)) {
            Msg.warn(player, "해당 이름의 박스는 존재하지 않습니다.");
            return;
        }
        player.getInventory().addItem(boxMap.get(name).getBoxItem());
        Msg.send(player, "아이템을 지급 받았습니다.", pfix);
    }

    public ItemStack getBoxItem(String name) {
        return boxMap.get(name).getBoxItem();
    }

    public void setBlockOpen(Player player, boolean bool) {
        if(bool) blockOpen.add(player);
        else blockOpen.remove(player);
    }

    @EventHandler
    public void onClickBox(ItemClickEvent e) {
        if(!e.getItemData().getType().equals("미스터리 박스")) return;
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        String name = Msg.uncolor(item.getItemMeta().getDisplayName());

        if(blockOpen.contains(e.getPlayer())) {
            Msg.warn(e.getPlayer(), "이미 미스터리 박스를 오픈하고 있습니다.");
            return;
        }

        for(MysteryBox box : boxMap.values()) {
            if(box.getBoxItem().isSimilar(item)) {
                box.openWindow(e.getPlayer());
                return;
            }
            else if(Msg.uncolor(box.getBoxItem().getItemMeta().getDisplayName()).equals(name)) {
                box.openWindow(e.getPlayer());
                return;
            }
        }
    }
}
