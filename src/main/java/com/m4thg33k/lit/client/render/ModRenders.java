package com.m4thg33k.lit.client.render;

import com.m4thg33k.lit.client.render.tiles.TileImprovedChestRenderer;
import com.m4thg33k.lit.client.render.tiles.TileImprovedCraftingTableRenderer;
import com.m4thg33k.lit.client.render.tiles.TileImprovedWorktableRenderer;
import com.m4thg33k.lit.tiles.TileImprovedChest;
import com.m4thg33k.lit.tiles.TileImprovedCraftingTable;
import com.m4thg33k.lit.tiles.TileImprovedWorktable;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ModRenders {

    public static void init()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileImprovedChest.class,new TileImprovedChestRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileImprovedCraftingTable.class,new TileImprovedCraftingTableRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileImprovedWorktable.class,new TileImprovedWorktableRenderer());
    }
}