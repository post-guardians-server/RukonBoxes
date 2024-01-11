package me.rukon0621.boxes.mybox;

import me.rukon0621.guardians.helper.ItemClass;
import me.rukon0621.gui.buttons.Button;
import me.rukon0621.gui.buttons.Icon;
import me.rukon0621.gui.windows.Window;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class MysteryBoxWindow extends Window {
    public MysteryBoxWindow(Player player, MysteryBox box) {
        super(player, "&f\uF000\uF02B", 3);
        map.put(11, new Button() {
            @Override
            public void execute(Player player, ClickType clickType) {
                disable();
                player.playSound(player, Sound.UI_BUTTON_CLICK, 1, 1.5f);
                new MysteryBoxItemShowWindow(player, box, (box.getData().size() - 1) / 9 + 1);
            }

            @Override
            public ItemStack getIcon() {
                ItemClass item = new ItemClass(new ItemStack(Material.SCUTE), "&6보상 및 확률표 확인하기");
                item.setCustomModelData(7);
                item.addLore("&f클릭하여 등장하는 아이템과 확률을 확인합니다.");
                return item.getItem();
            }
        });

        map.put(13, new Icon() {
            @Override
            public ItemStack getIcon() {
                return box.getBoxItem();
            }
        });

        map.put(15, new Button() {
            @Override
            public void execute(Player player, ClickType clickType) {
                disable();
                player.playSound(player, Sound.UI_BUTTON_CLICK, 1, 1.5f);
                new MysteryBoxOpenWindow(player, box, clickType.equals(ClickType.SHIFT_LEFT));
            }

            @Override
            public ItemStack getIcon() {
                ItemClass item = new ItemClass(new ItemStack(Material.SCUTE), "&6박스 열기");
                item.setCustomModelData(7);
                item.addLore("&f박스를 사용하고 아이템을 뽑습니다.");
                item.addLore(" ");
                item.addLore("&7쉬프트 좌클릭으로 애니메이션을 스킵할 수 있습니다.");
                return item.getItem();
            }
        });
        reloadGUI();
        open();
    }

    @Override
    public void close(boolean b) {
        disable();
        if(b) player.closeInventory();
    }
}
