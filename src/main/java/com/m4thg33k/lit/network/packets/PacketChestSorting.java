package com.m4thg33k.lit.network.packets;

import com.m4thg33k.lit.tiles.TileImprovedChest;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class PacketChestSorting extends BaseThreadsafePacket{

    BlockPos pos;
    boolean forward = true;
    int sortType = 0;

    public PacketChestSorting()
    {}

    public PacketChestSorting(BlockPos pos,int sortType,boolean forward)
    {
        this.pos = pos;
        this.sortType = sortType;
        this.forward = forward;
    }

    @Override
    public void handleClientSafe(NetHandlerPlayClient netHandler) {
        throw new UnsupportedOperationException("Server-side only!");
    }

    @Override
    public void handleServerSafe(NetHandlerPlayServer netHandler) {
        TileEntity tileEntity = netHandler.player.world.getTileEntity(pos);
//        TileEntity tileEntity = netHandler.playerEntity.worldObj.getTileEntity(pos);
        if (tileEntity != null && tileEntity instanceof TileImprovedChest)
        {
            ((TileImprovedChest)tileEntity).performSort(sortType,forward);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = new BlockPos(buf.readInt(),buf.readInt(),buf.readInt());
        forward = buf.readBoolean();
        sortType = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeBoolean(forward);
        buf.writeInt(sortType);
    }
}
