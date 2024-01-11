package me.rukon0621.boxes.commands;

import me.rukon0621.boxes.RandomBoxManager;
import me.rukon0621.boxes.RukonBoxes;
import me.rukon0621.guardians.commands.AbstractCommand;
import me.rukon0621.guardians.helper.ArgHelper;
import me.rukon0621.guardians.helper.ItemClass;
import me.rukon0621.guardians.helper.Msg;
import me.rukon0621.guardians.helper.TabCompleteUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.rukon0621.guardians.main.pfix;

public class RandomBoxCommand extends AbstractCommand {
    private final RandomBoxManager randomBoxManager = RukonBoxes.inst().getRandomBoxManager();

    public RandomBoxCommand() {
        super("randomBox", RukonBoxes.inst());
        arguments.add("생성");
        arguments.add("삭제");
        arguments.add("아이템설정");
        arguments.add("지급");
        arguments.add("리로드");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)) return false;
        if(args.length==0) {
            usages(player);
            return true;
        }

        if(args[0].equals("생성")) {
            if(args.length<2) {
                usage(player, args[0], true);
                return true;
            }
            randomBoxManager.createNewRandomBox(player, ArgHelper.sumArg(args, 1));
        }
        else if(args[0].equals("삭제")) {
            if(args.length<2) {
                usage(player, args[0], true);
                return true;
            }
            randomBoxManager.deleteRandomBox(player, ArgHelper.sumArg(args, 1));
        }
        else if(args[0].equals("아이템설정")) {
            if(args.length<2) {
                usage(player, args[0], true);
                return true;
            }
            if(player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                Msg.warn(player, "손에 아이템을 들어주세요.");
                return true;
            }
            randomBoxManager.setIconOfBox(player, ArgHelper.sumArg(args, 1), new ItemClass(player.getInventory().getItemInMainHand()));
        }
        else if(args[0].equals("지급")) {
            if(args.length<2) {
                usage(player, args[0], true);
                return true;
            }
            randomBoxManager.giveRandomBox(player, ArgHelper.sumArg(args, 1));
        }
        else if(args[0].equals("리로드")) {
            randomBoxManager.reloadAllBoxes();
            Msg.send(player, "모든 박스를 리로드했습니다.", pfix);
        }
        else {
            usages(player);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(args.length==1) return TabCompleteUtils.searchAtList(arguments, args[0]);
        if(args[0].equals("삭제")||args[0].equals("아이템설정")||args[0].equals("지급")) {
            if(args.length==2) return TabCompleteUtils.search(randomBoxManager.getBoxNames(), args[1]);
        }
        return new ArrayList<>();
    }

    @Override
    protected void usage(Player player, String s) {
        switch (s) {
            case "생성" -> Msg.send(player, "&6/랜덤박스 생성 <이름>");
            case "삭제" -> Msg.send(player, "&6/랜덤박스 삭제 <이름>");
            case "아이템설정" -> Msg.send(player, "&6/랜덤박스 아이템설정 <이름>");
            case "지급" -> Msg.send(player, "&6/랜덤박스 지급 <이름>");
            case "리로드" -> Msg.send(player, "&6/랜덤박스 리로드");
        }

    }
}
