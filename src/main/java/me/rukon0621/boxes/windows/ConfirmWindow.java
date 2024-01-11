package me.rukon0621.boxes.windows;

import me.rukon0621.boxes.RandomBox;
import me.rukon0621.callback.LogManager;
import me.rukon0621.guardians.data.ItemData;
import me.rukon0621.guardians.data.LevelData;
import me.rukon0621.guardians.data.PlayerData;
import me.rukon0621.guardians.helper.ItemClass;
import me.rukon0621.guardians.helper.ItemSaver;
import me.rukon0621.guardians.helper.Rand;
import me.rukon0621.gui.buttons.Button;
import me.rukon0621.gui.windows.Window;
import me.rukon0621.pay.RukonPayment;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class ConfirmWindow extends Window {

    public ConfirmWindow(Player player, RandomBox box) {
        super(player, "&6랜덤 박스를 사용하시겠습니까?", 1);

        map.put(3, new Button() {
            @Override
            public void execute(Player player, ClickType clickType) {
                close(true);
                ArrayList<ItemStack> results = new ArrayList<>();

                StringBuilder sb = new StringBuilder(box.getBoxName());
                for(String str : box.getChanceMap().keySet()) {
                    if(!Rand.chanceOf(box.getChanceMap().get(str))) continue;
                    sb.append(" ,");
                    if(str.startsWith("경험치")) {
                        results.add(LevelData.getEquipmentExpBook(Integer.parseInt(str.split(":")[1])));
                    }
                    else if(str.startsWith("수표")) {
                        results.add(LevelData.getDinarItem(Integer.parseInt(str.split(":")[1])));
                    }
                    else if(str.startsWith("루나르")) {
                        results.add(RukonPayment.inst().getPaymentManager().getRunarItem(Integer.parseInt(str.split(":")[1])));
                    }
                    else  {
                        String[] stData = str.split(";");
                        PlayerData pdc = new PlayerData(player);
                        ItemData itemData = new ItemData(ItemSaver.getItem(stData[0]));
                        if (!stData[1].equals("-")) {
                            int r1, r2;
                            r1 = pdc.getLevel() - Integer.parseInt(stData[1]);
                            r2 = pdc.getLevel() - Integer.parseInt(stData[2]);
                            itemData.setLevel(Math.max(0, Rand.randInt(r1, r2)));
                        }
                        ItemStack it = itemData.getItemStack();

                        if(stData.length >= 5) {
                            if(stData[4].equals("original")) {
                                it = ItemSaver.getItem(stData[0]).getItem();
                            }
                        }
                        if(stData.length >= 4) {
                            it.setAmount(Integer.parseInt(stData[3]));
                        }
                        sb.append(it.getItemMeta().displayName());

                        results.add(it);
                    }
                }

                sb.append(" - ");
                LogManager.log(player, "랜덤박스", sb.toString());
                new BoxOpenWindow(player, results, box);
            }

            @Override
            public ItemStack getIcon() {
                ItemClass item = new ItemClass(new ItemStack(Material.SCUTE), "&a뽑기");
                item.setCustomModelData(24);
                item.addLore("&f랜덤 박스를 사용해 아이템을 뽑습니다.");
                return item.getItem();
            }
        });
        map.put(5, new Button() {
            @Override
            public void execute(Player player, ClickType clickType) {
                player.closeInventory();
                player.playSound(player, Sound.UI_BUTTON_CLICK, 1, 1.5f);
            }

            @Override
            public ItemStack getIcon() {
                ItemClass item = new ItemClass(new ItemStack(Material.BARRIER), "&c취소");
                item.addLore("&f뽑기 창을 닫습니다.");
                return item.getItem();
            }
        });

        reloadGUI();
        open();
    }

    @Override
    public void close(boolean click) {
        player.closeInventory();
        disable();
    }
}
