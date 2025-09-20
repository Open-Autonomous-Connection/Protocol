package github.openautonomousconnection.protocol.versions.v1_0_0.classic;

import github.openautonomousconnection.protocol.packets.v1_0_0.beta.UnsupportedClassicPacket;

public abstract class ClassicHandlerClient {

    public abstract void unsupportedClassicPacket(String classicPacketClassName, Object[] content);

}
