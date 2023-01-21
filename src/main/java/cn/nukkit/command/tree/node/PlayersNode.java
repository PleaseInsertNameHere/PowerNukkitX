package cn.nukkit.command.tree.node;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.utils.EntitySelector;

import java.util.Collections;
import java.util.stream.Collectors;

/**
 * 解析为{@code List<Player>}值
 * <p>
 * 不会默认使用，需要手动指定
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public class PlayersNode extends TargetNode<Player> {
    //todo 支持uuid 或者 xuid
    @Override
    public void fill(String arg) {
        if (arg.isBlank()) {
            this.error();
        } else if (EntitySelector.hasArguments(arg)) {
            var entities = EntitySelector.matchEntities(this.parent.parent.getSender(), arg);
            if (entities != null)
                this.value = entities.stream().filter(entity -> entity instanceof Player).map(entity -> (Player) entity).collect(Collectors.toList());
            else error("commands.generic.noTargetMatch");
        } else {
            Player player = Server.getInstance().getPlayer(arg);
            if (player != null) {
                this.value = Collections.singletonList(player);
            } else error("commands.generic.player.notFound");
        }
    }
}
