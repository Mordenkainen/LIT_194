package com.m4thg33k.lit.network.packets;

import com.m4thg33k.lit.LIT;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

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

    //    public BlockPos pos;
//    public NBTTagCompound compound;
//
//    public PacketNBT()
//    {
//
//    }
//
//    public PacketNBT(BlockPos pos, NBTTagCompound tagCompound)
//    {
//        this.pos = pos;
//        compound = tagCompound;
//    }
//
//    @Override
//    public void fromBytes(ByteBuf buf) {
//        pos = new BlockPos(ByteBufUtils.readVarInt(buf,5),ByteBufUtils.readVarInt(buf,5),ByteBufUtils.readVarInt(buf,5));
//        compound = ByteBufUtils.readTag(buf);
//    }
//
//    @Override
//    public void toBytes(ByteBuf buf) {
//        ByteBufUtils.writeVarInt(buf,pos.getX(),5);
//        ByteBufUtils.writeVarInt(buf,pos.getY(),5);
//        ByteBufUtils.writeVarInt(buf,pos.getZ(),5);
//        ByteBufUtils.writeTag(buf,compound);
//    }
}
