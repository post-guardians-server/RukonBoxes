package me.rukon0621.boxes.windows;

import me.rukon0621.boxes.RandomBox;
import me.rukon0621.guardians.helper.Msg;
import me.rukon0621.guardians.helper.Rand;
import me.rukon0621.guardians.mailbox.MailBoxManager;
import me.rukon0621.gui.buttons.Icon;
import me.rukon0621.gui.windows.Window;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BoxOpenWindow extends Window {
    public BoxOpenWindow(Player player, ArrayList<ItemStack> results, RandomBox box) {
        super(player, "&6보상을 가져가십시오.", (results.size() - 1) / 9 + 1);
        int slot = 0;
        for(ItemStack item : results) {
            map.put(slot, new Icon() {
                @Override
                public ItemStack getIcon() {
                    return item;
                }
            });
            slot++;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        try {
            if(!item.getItemMeta().getDisplayName().equals(box.getIcon().getItemMeta().getDisplayName())) {
                Msg.warn(player, "손에 박스를 들어주세요.");
                return;
            }
        } catch (Exception e) {
            Msg.warn(player, "손에 박스를 들어주세요.");
            return;
        }

        item.setAmount(item.getAmount() - 1);
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, Rand.randFloat(0.8, 1.3));
        player.playSound(player, Sound.BLOCK_ENDER_CHEST_OPEN, 1, Rand.randFloat(0.8, 1.3));
        player.playSound(player, Sound.BLOCK_ENDER_CHEST_CLOSE, 1, Rand.randFloat(0.8, 1.3));
        reloadGUI();
        open();
    }

    @Override
    public void close(boolean click) {
        disable();
        List<ItemStack> items = new ArrayList<>();
        for(int i = 0; i < rows * 9; i++) {
            if(inv.getSlot(i)==null) break;
            items.add(inv.getSlot(i));
        }
        MailBoxManager.giveAllOrMailAll(player, items);
        if(click) player.closeInventory();
    }
}
