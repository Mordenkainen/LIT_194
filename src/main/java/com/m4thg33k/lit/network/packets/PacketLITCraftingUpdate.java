package com.m4thg33k.lit.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.inventory.Container;
import net.minecraft.network.NetHandlerPlayServer;

public class PacketLITCraftingUpdate extends BaseThreadsafePacket {

    @Override
    public void handleClientSafe(NetHandlerPlayClient netHandler) {
        throw new UnsupportedOperationException("Server-side only!");
    }

    @Override
    public void handleServerSafe(NetHandlerPlayServer netHandler) {
        Container container = netHandler.player.openContainer;
        if (container != null)
        {
            container.onCraftMatrixChanged(null);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        // do nothing
    }

    @Override
    public void toBytes(ByteBuf buf) {
        // do nothing
    }
}
