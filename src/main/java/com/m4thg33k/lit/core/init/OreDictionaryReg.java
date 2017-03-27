package com.m4thg33k.lit.core.init;

import com.m4thg33k.lit.blocks.ModBlocks;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictionaryReg {

    public static void init()
    {
        OreDictionary.registerOre("blockCharcoal", ModBlocks.charcoalBlock);
        OreDictionary.registerOre("furnace", ModBlocks.improvedFurnaceBlock);
        OreDictionary.registerOre("furnace", ModBlocks.betterFurnaceBlock);
        OreDictionary.registerOre("chest", ModBlocks.improvedChestBlock);
        OreDictionary.registerOre("hopper", ModBlocks.improvedHopperBlock);
        OreDictionary.registerOre("workbench", ModBlocks.improvedWorktableBlock);
    }
}
