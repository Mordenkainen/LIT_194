package com.m4thg33k.lit.core.event;

import com.m4thg33k.lit.api.chest.ChestTypes;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LITClientEvents {

    @SubscribeEvent
    public void stitchTextures(TextureStitchEvent.Pre pre)
    {
        pre.getMap().registerSprite(ChestTypes.sortButtonLocation);
    }
}
