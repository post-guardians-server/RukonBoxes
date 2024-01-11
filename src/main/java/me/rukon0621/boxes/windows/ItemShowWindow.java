package me.rukon0621.boxes.windows;

import me.rukon0621.boxes.RandomBox;
import me.rukon0621.guardians.data.LevelData;
import me.rukon0621.guardians.data.PlayerData;
import me.rukon0621.guardians.helper.ItemClass;
import me.rukon0621.guardians.helper.ItemSaver;
import me.rukon0621.gui.buttons.Icon;
import me.rukon0621.gui.windows.Window;
import me.rukon0621.pay.RukonPayment;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class ItemShowWindow extends Window {
    private static final String title = "&e등장하는 아이템과 확률을 확인해보세요!";
    private final RandomBox box;
    public ItemShowWindow(Player player, RandomBox box) {
        super(player, title, (box.getChanceMap().size()-1)/9 + 1);
        this.box = box;
        HashMap<String, Integer> chanceMap = box.getChanceMap();
        int slot = 0;
        for(String str : chanceMap.keySet()) {
            map.put(slot, new Icon() {
                @Override
                public ItemStack getIcon() {
                    ItemClass item;
                    if(str.startsWith("경험치")) {
                        item = new ItemClass(LevelData.getEquipmentExpBook(Integer.parseInt(str.split(":")[1])));
                    }
                    else if(str.startsWith("수표")) {
                        int value = Integer.parseInt(str.split(":")[1]);
                        item = new ItemClass(LevelData.getDinarItem(value));
                    }
                    else if(str.startsWith("루나르")) {
                        int value = Integer.parseInt(str.split(":")[1]);
                        item = new ItemClass(RukonPayment.inst().getPaymentManager().getRunarItem(value));
                    }
                    else  {
                        String[] stData = str.split(";");
                        item = ItemSaver.getItem(stData[0]);
                        PlayerData pdc = new PlayerData(player);
                        if(!stData[1].equals("-")) {
                            int r1, r2;
                            r1 = pdc.getLevel() - Integer.parseInt(stData[2]);
                            r2 = pdc.getLevel() - Integer.parseInt(stData[1]);
                            if(r2>0)  {
                                if(r1<0) r1 = 0;
                                item.addLore(" ");
                                item.addLore(String.format("&7등장 레벨 범위: %d ~ %d", r1, r2));
                            }
                        }
                        if(stData.length == 4) {
                            item.setAmount(Integer.parseInt(stData[3]));
                        }
                    }
                    item.addLore(" ");
                    item.addLore(String.format("&b※ 등장 확률: %d%%", chanceMap.get(str)));
                    return item.getItem();
                }
            });
            slot++;
        }
        reloadGUI();
        open();
    }

    @Override
    protected void executeButton(InventoryClickEvent e) {
        close(true);
    }

    @Override
    public void close(boolean click) {
        disable();
        if(click) {
            new ConfirmWindow(player, box);
            player.playSound(player, Sound.UI_BUTTON_CLICK, 1, 0.8f);
        }
    }
}
