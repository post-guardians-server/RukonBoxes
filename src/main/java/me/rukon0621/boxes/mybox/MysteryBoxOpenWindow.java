package me.rukon0621.boxes.mybox;

import me.rukon0621.boxes.RukonBoxes;
import me.rukon0621.callback.LogManager;
import me.rukon0621.guardians.commands.EntireBroadcastCommand;
import me.rukon0621.guardians.data.ItemData;
import me.rukon0621.guardians.data.PlayerData;
import me.rukon0621.guardians.helper.Broadcaster;
import me.rukon0621.guardians.helper.Couple;
import me.rukon0621.guardians.helper.Msg;
import me.rukon0621.guardians.helper.Rand;
import me.rukon0621.guardians.mailbox.MailBoxManager;
import me.rukon0621.gui.buttons.Icon;
import me.rukon0621.gui.windows.Window;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import static me.rukon0621.guardians.main.pfix;

public class MysteryBoxOpenWindow extends Window {
    private final MysteryBox box;
    private final MysteryBoxManager manager =  RukonBoxes.inst().getMysteryBoxManager();
    private String result;
    private boolean isEnd = false;
    private Timer timer;
    private double chance;

    public MysteryBoxOpenWindow(Player player, MysteryBox box, boolean skipAnimation) {
        super(player, "&f\uF000\uF02C", 3);
        this.box = box;
        manager.setBlockOpen(player, true);
        double num = Rand.randDouble(0, 100);
        double total = 0;
        for(Couple<String, Double> c : box.getData()) {
            if(num > total && num <= c.getSecond() + total) {
                result = c.getFirst();
                chance = c.getSecond();
                break;
            }
            total += c.getSecond();
        }
        if(result==null) {
            Msg.warn(player, "예기치 못한 오류가 발생했습니다. 운영자에게 문의하십시오.");
            player.closeInventory();
            return;
        }
        if(skipAnimation) {
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 0.5f);
            map.put(13, new Icon() {
                @Override
                public ItemStack getIcon() {
                    return manager.parsedItemSaver(result);
                }
            });
            reloadGUI();
            open();
            end();
            return;
        }
        reloadGUI();
        open();
        timer = new Timer();
        timer.runTaskTimer(RukonBoxes.inst(), 0, 1);
    }

    private void end() {
        if(!ItemData.removeItem(player, new ItemData(box.getBoxItem()), true)) {
            Msg.warn(player, "미스터리 박스를 가지고 있지 않아 뽑기에 실패했습니다.");
            return;
        }
        ItemStack item = manager.parsedItemSaver(result);

        String[] data = result.split("::");
        if(data.length==3) {
            ItemData itemData = new ItemData(item);
            itemData.setLevel(Math.max(new PlayerData(player).getLevel() - Integer.parseInt(data[2]), 1));
            item = itemData.getItemStack();
        }

        Msg.send(player, "아이템을 뽑았습니다! &7 - " + item.getItemMeta().getDisplayName(), pfix);
        if(box.getSpecialList().contains(result)) {
            EntireBroadcastCommand.entireBroadcast(player, String.format("&6%s님&e이 %.2f%%의 확률을 뚫고 진귀한 아이템을 획득하셨습니다! &7- &f%s", player.getName(), chance, item.getItemMeta().getDisplayName()), false);
        }
        MailBoxManager.giveOrMail(player, item, true);
        new BukkitRunnable() {
            @Override
            public void run() {
                isEnd = true;
                manager.setBlockOpen(player, false);
                String sb = box.getBoxItem().getItemMeta().getDisplayName() + " - " + result;

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        LogManager.log(player, "박스", sb);
                    }
                }.runTask(RukonBoxes.inst());
            }
        }.runTaskLaterAsynchronously(RukonBoxes.inst(), 10);
    }

    class Timer extends BukkitRunnable {
        private int tick = 0;
        private int tickPer = 1;
        private int nextUp = 20;

        @Override
        public void run() {
            if(tick==nextUp) {
                tickPer++;
                nextUp = tick + 20;
            }

            if(tickPer==10) {
                map.put(13, new Icon() {
                    @Override
                    public ItemStack getIcon() {
                        return manager.parsedItemSaver(result);
                    }
                });
                player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 0.5f);
                cancel();
                reloadGUI();
                end();
                return;
            }

            if(tick%tickPer==0) {
                player.playSound(player.getLocation(), "a.type", 1, Rand.randFloat(0.7, 1.2));
                map.put(13, new Icon() {
                    @Override
                    public ItemStack getIcon() {
                        int i = Rand.randInt(0, box.getData().size() - 1);
                        return manager.parsedItemSaver(box.getData().get(i).getFirst());
                    }
                });
                reloadGUI();
            }
            tick++;
        }
    }

    @Override
    public void close(boolean b) {
        if(b) {
            if(!timer.isCancelled()) timer.cancel();
            disable();
            manager.setBlockOpen(player, false);
            player.closeInventory();
            return;
        }

        if(isEnd) {
            disable();
            player.closeInventory();
        }
        else {
            if(timer != null && !timer.isCancelled()) timer.cancel();
            disable();
            manager.setBlockOpen(player, false);
            player.closeInventory();
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if(!e.getPlayer().equals(player)) return;
        manager.setBlockOpen(player, false);
        timer.cancel();
    }

}
