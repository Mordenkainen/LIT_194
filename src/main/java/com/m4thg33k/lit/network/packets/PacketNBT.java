package com.m4thg33k.lit.network.packets;

import com.m4thg33k.lit.LIT;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketNBT extends BaseThreadsafePacket {

    public BlockPos pos;
    public NBTTagCompound compound;

    public PacketNBT()
    {
        this(new BlockPos(0,0,0),new NBTTagCompound());
    }

    public PacketNBT(BlockPos pos, NBTTagCompound tagCompound)
    {
        this.pos = pos;
        this.compound = tagCompound;
    }


    @Override
    public void handleClientSafe(NetHandlerPlayClient netHandler) {
        LIT.proxy.handleNBTPacket(this);
    }

    @Override
    public void handleServerSafe(NetHandlerPlayServer netHandler) {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = readPos(buf);
        compound = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writePos(pos, buf);
        ByteBufUtils.writeTag(buf, compound);
    }

}
