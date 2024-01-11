package me.rukon0621.boxes.mybox;

import me.rukon0621.boxes.RukonBoxes;
import me.rukon0621.guardians.commands.AbstractCommand;
import me.rukon0621.guardians.helper.ArgHelper;
import me.rukon0621.guardians.helper.Msg;
import me.rukon0621.guardians.helper.TabCompleteUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static me.rukon0621.guardians.main.pfix;

public class MysteryBoxCommand extends AbstractCommand {
    private final MysteryBoxManager manager = RukonBoxes.inst().getMysteryBoxManager();

    public MysteryBoxCommand() {
        super("mysterybox", RukonBoxes.inst());
        arguments.add("생성");
        arguments.add("삭제");
        arguments.add("지급");
        arguments.add("아이템설정");
        arguments.add("리로드");
    }

    @Override
    protected void usage(Player player, String s) {
        if(s.equals("생성")) {
            Msg.send(player, "&6/미박 생성 <이름>");
        }
        else if(s.equals("삭제")) {
            Msg.send(player, "&6/미박 삭제 <이름>");
        }
        else if(s.equals("지급")) {
            Msg.send(player, "&6/미박 지급 <이름>");
        }
        else if(s.equals("아이템설정")) {
            Msg.send(player, "&6/미박 아이템설정 <이름>");
        }
        else if(s.equals("리로드")) {
            Msg.send(player, "&6/미박 리로드");
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(sender instanceof Player player)) return false;

        if(args.length==0) {
            usages(player);
        }
        else if (args[0].equals("생성")) {
            if(args.length < 2) {
                usage(player, args[0], true);
                return true;
            }
            manager.createNewMysteryBox(player, ArgHelper.sumArg(args, 1));
        }
        else if (args[0].equals("삭제")) {
            if(args.length < 2) {
                usage(player, args[0], true);
                return true;
            }
            manager.deleteMysteryBox(player, ArgHelper.sumArg(args, 1));
        }
        else if (args[0].equals("지급")) {
            if(args.length < 2) {
                usage(player, args[0], true);
                return true;
            }
            manager.giveBoxItem(player, ArgHelper.sumArg(args, 1));
        }
        else if (args[0].endsWith("설정")) {
            if(args.length < 2) {
                usage(player, args[0], true);
                return true;
            }
            manager.setBoxItem(player, ArgHelper.sumArg(args, 1));
        }
        else if (args[0].equals("리로드")) {
            manager.reloadAllBoxes();
            Msg.send(player, "모든 미스터리 박스를 리로드합니다.", pfix);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length==0) return new ArrayList<>();
        else if(args.length==1) return TabCompleteUtils.search(arguments, args[0]);
        else if(args.length==2) {
            if (args[0].equals("생성") || args[0].equals("삭제") || args[0].endsWith("설정") || args[0].equals("지급")) {
                return TabCompleteUtils.search(manager.getBoxNames(), args[1]);
            }
        }
        return new ArrayList<>();
    }
}
