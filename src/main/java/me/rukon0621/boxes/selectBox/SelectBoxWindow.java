package me.rukon0621.boxes.selectBox;

import me.rukon0621.callback.LogManager;
import me.rukon0621.guardians.helper.Couple;
import me.rukon0621.guardians.helper.ItemClass;
import me.rukon0621.guardians.helper.ItemSaver;
import me.rukon0621.guardians.helper.Msg;
import me.rukon0621.guardians.mailbox.MailBoxManager;
import me.rukon0621.gui.buttons.Button;
import me.rukon0621.gui.windows.Window;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class SelectBoxWindow extends Window {

    private static int getRow(Map<Integer, Couple<String, Integer>> data) {
        int max = 0;
        for(int i : data.keySet()) {
            max = Math.max(i, max);
        }
        return (max - 1) / 9 + 1;
    }

    private final ItemStack boxItem;

    public SelectBoxWindow(Player player, ItemStack boxItem, Map<Integer, Couple<String, Integer>> data) {
        super(player, "&f\uF000", getRow(data));
        this.boxItem = boxItem;
        for(int i : data.keySet()) {
            ItemStack it = ItemSaver.getItem(data.get(i).getFirst()).getItem().clone();
            it.setAmount(data.get(i).getSecond());
            map.put(i, new SelectBoxButton(it));
        }
        player.playSound(player, Sound.BLOCK_ENDER_CHEST_OPEN, 1, 1.5f);
        reloadGUI();
        open();
    }

    @Override
    public void close(boolean b) {
        disable();
        if(b) player.closeInventory();
    }

    class SelectBoxButton extends Button {
        private final ItemStack item;

        public SelectBoxButton(ItemStack item) {
            this.item = item;
        }

        @Override
        public void execute(Player player, ClickType clickType) {
            player.closeInventory();

            ItemStack item = player.getInventory().getItemInMainHand();
            if(item.getType().equals(Material.AIR) || !item.getItemMeta().getDisplayName().equals(boxItem.getItemMeta().getDisplayName())) {
                Msg.warn(player, "박스를 들고 있지 않아 뽑기에 실패했습니다.");
                return;
            }

            item.setAmount(item.getAmount() - 1);
            LogManager.log(player, "박스",  boxItem.getItemMeta().getDisplayName() + " - " + item.getItemMeta().getDisplayName());
            MailBoxManager.giveOrMail(player, this.item);
            player.playSound(player, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1, 0.8f);
            player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1.5f);
        }

        @Override
        public ItemStack getIcon() {
            ItemClass icon = new ItemClass(item.clone());
            icon.addLore(" ");
            icon.addLore("&a\uE011\uE00C\uE00C아이템을 선택하려면 클릭하십시오.");
            return icon.getItem();
        }
    }

}
