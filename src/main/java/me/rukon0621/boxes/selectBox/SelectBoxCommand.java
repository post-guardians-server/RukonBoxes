package me.rukon0621.boxes.selectBox;

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

public class SelectBoxCommand extends AbstractCommand {

    private static final SelectBoxManager manager = RukonBoxes.inst().getSelectBoxManager();

    public SelectBoxCommand() {
        super("selectBox", RukonBoxes.inst());
        arguments.add("리로드");
        arguments.add("생성");
        arguments.add("삭제");
        arguments.add("지급");
    }

    @Override
    protected void usage(Player player, String s) {
        if(arguments.contains(s)) {
            Msg.send(player, "&6/선택형박스 " + s + " <이름>");
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) return false;
        if(args.length==0) {
            usages(player);
            return true;
        }
        if(args[0].equals("리로드")) {
            manager.reloadBoxes();
            Msg.send(player, "모든 박스를 리로드했습니다.");
            return true;
        }
        else if(args.length==1) {
            usage(player, args[0], true);
            return true;
        }
        String name = ArgHelper.sumArg(args, 1);
        switch (args[0]) {
            case "생성" -> manager.createBox(player, name);
            case "삭제" -> manager.deleteBox(player, name);
            case "지급" -> manager.giveBox(player, name);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length==1) return TabCompleteUtils.search(arguments, args[0]);
        else if(args.length==2) return TabCompleteUtils.search(manager.getBoxNames(), args[1]);

        return new ArrayList<>();
    }
}
