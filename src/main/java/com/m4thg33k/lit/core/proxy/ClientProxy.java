package com.m4thg33k.lit.core.proxy;

import com.m4thg33k.lit.LIT;
import com.m4thg33k.lit.client.render.ModRenders;
import com.m4thg33k.lit.client.render.registers.BlockRenderRegister;
import com.m4thg33k.lit.client.render.registers.ItemRenderRegister;
import com.m4thg33k.lit.core.event.LITClientEvents;
import com.m4thg33k.lit.core.event.LITCommonEvents;
import com.m4thg33k.lit.core.util.LogHelper;
import com.m4thg33k.lit.network.LITNetwork;
import com.m4thg33k.lit.network.packets.BasePacket;
import com.m4thg33k.lit.network.packets.PacketNBT;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        ItemRenderRegister.registerItemRenderer();
        OBJLoader.INSTANCE.addDomain(LIT.MODID);
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);

        BlockRenderRegister.registerBlockRenderer();
        ModRenders.init();
        MinecraftForge.EVENT_BUS.register(new LITClientEvents());
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }

    @Override
    public void handleNBTPacket(PacketNBT pkt) {
        World world = Minecraft.getMinecraft().theWorld;
        try {
            Minecraft.getMinecraft().theWorld.getTileEntity(pkt.pos).readFromNBT(pkt.compound);
            Minecraft.getMinecraft().theWorld.notifyBlockUpdate(pkt.pos, world.getBlockState(pkt.pos), world.getBlockState(pkt.pos), 0);
        } catch (NullPointerException e)
        {
            LogHelper.error("Null pointer issue with tile packet!");
        }
    }

    @Override
    public void sendPacketToServerOnly(BasePacket packet) {
        LITNetwork.sendToServer(packet);
    }
}
