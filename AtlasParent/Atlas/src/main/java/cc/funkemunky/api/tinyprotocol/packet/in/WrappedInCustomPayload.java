package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.events.impl.PacketReceiveEvent;
import cc.funkemunky.api.events.impl.PacketSendEvent;
import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.WrappedPacketDataSerializer;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedInCustomPayload extends NMSObject {

    public WrappedInCustomPayload(Object object) {
        super(object);
    }

    public WrappedInCustomPayload(Object object, Player player) {
        super(object, player);
    }

    public WrappedInCustomPayload(PacketReceiveEvent event) {
        super(event);
    }

    public WrappedInCustomPayload(PacketSendEvent event) {
        super(event);
    }

    private static WrappedClass wrapped = Reflections.getNMSClass(Client.CUSTOM_PAYLOAD);

    private static WrappedField tagField;

    //1.7.10
    private static WrappedField lengthField;
    private static WrappedField dataField;

    //1.8
    private static WrappedClass wrappedPDS = Reflections.getNMSClass("PacketDataSerializer");
    private static WrappedField dataSerializer;

    //1.13+
    private static WrappedClass minecraftKeyWrapper;
    private static WrappedField keyOne, keyTwo;
    private static WrappedField mkField;

    private String tag;
    private int length;
    private byte[] data;

    @Override
    public void process(Player player, ProtocolVersion version) {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            tag = tagField.get(getObject());
            length = lengthField.get(getObject());
            data = dataField.get(getObject());
        } else {
            WrappedPacketDataSerializer wpds = new WrappedPacketDataSerializer(dataSerializer.get(getObject()));

            data = wpds.getData();
            length = data.length;

            if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) tag = tagField.get(getObject());
            else {
                Object mk = mkField.get(getObject());
                tag = keyOne.get(mk) + ":" + keyTwo.get(mk);
            }
        }
    }

    static {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            lengthField = wrapped.getFieldByType(int.class, 0);
            dataField = wrapped.getFieldByType(byte.class, 0);
        } else {
            wrappedPDS = Reflections.getNMSClass("PacketDataSerializer");
            dataSerializer = wrapped.getFieldByType(Object.class, 0);
        }

        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13)) {
            minecraftKeyWrapper = Reflections.getNMSClass("MinecraftKey");
            keyOne = minecraftKeyWrapper.getFieldByType(String.class, 0);
            keyTwo = minecraftKeyWrapper.getFieldByType(String.class, 1);
            mkField = wrapped.getFieldByType(minecraftKeyWrapper.getParent(), 0);
        } else tagField = wrapped.getFieldByType(String.class, 0);
    }
}
