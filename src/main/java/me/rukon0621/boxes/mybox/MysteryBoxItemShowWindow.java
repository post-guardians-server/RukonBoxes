package me.rukon0621.boxes.mybox;

import me.rukon0621.boxes.RukonBoxes;
import me.rukon0621.guardians.data.PlayerData;
import me.rukon0621.guardians.helper.Couple;
import me.rukon0621.guardians.helper.ItemClass;
import me.rukon0621.gui.buttons.Icon;
import me.rukon0621.gui.windows.Window;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MysteryBoxItemShowWindow extends Window {
    private final MysteryBox box;
    public MysteryBoxItemShowWindow(Player player, MysteryBox mysteryBox, int row) {
        super(player, "&f", row);
        this.box = mysteryBox;
        int slot = 0;
        MysteryBoxManager manager = RukonBoxes.inst().getMysteryBoxManager();
        int playerLevel = new PlayerData(player).getLevel();
        for(Couple<String, Double> couple : mysteryBox.getData()) {
            map.put(slot, new Icon() {
                @Override
                public ItemStack getIcon() {
                    ItemClass item = new ItemClass(manager.parsedItemSaver(couple.getFirst(), playerLevel));
                    item.addLore(" ");
                    item.addLore(String.format("&b※ 등장 확률: %.2f%%", couple.getSecond()));
                    return item.getItem();
                }
            });
            slot++;
        }
        reloadGUI();
        open();
    }

    @Override
    public void close(boolean b) {
        disable();
        player.closeInventory();
        if(b) {
            box.openWindow(player);
        }
    }
}
