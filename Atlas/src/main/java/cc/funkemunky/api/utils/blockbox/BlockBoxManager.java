package cc.funkemunky.api.utils.blockbox;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.reflection.Reflection;
import lombok.Getter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class BlockBoxManager {
    private BlockBox blockBox;
    private ExecutorService executor;

    public BlockBoxManager() {
        executor = Executors.newSingleThreadExecutor();
        try {
            blockBox = (BlockBox) Reflection.getClass("cc.funkemunky.api.utils.blockbox.boxes.BlockBox" + ProtocolVersion.getGameVersion().getServerVersion().replaceAll("v", "")).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}