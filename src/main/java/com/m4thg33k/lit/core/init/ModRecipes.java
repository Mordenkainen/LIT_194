package com.m4thg33k.lit.core.init;

import com.m4thg33k.lit.LIT;
import com.m4thg33k.lit.api.chest.ChestTypes;
import com.m4thg33k.lit.api.furnace.FurnaceTypes;
import com.m4thg33k.lit.blocks.ModBlocks;
import com.m4thg33k.lit.core.crafting.ChestRecipe;
import com.m4thg33k.lit.items.ModItems;
import com.m4thg33k.lit.lib.LITConfigs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class ModRecipes {

    public static void initRecipes()
    {
        RecipeSorter.register(LIT.MODID + ":" + "chestRecipe", ChestRecipe.class, RecipeSorter.Category.SHAPED, "after:forge:shapedore before:minecraft:shapeless");

        FurnaceTypes.registerRecipes();
        ChestTypes.regRecipes();

        if (LITConfigs.USE_ALTERNATE_CRAFTING_TABLE_RECIPE)
        {
//            GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.improvedCraftingTableBlock,1),new ItemStack(Blocks.CRAFTING_TABLE,1),new ItemStack(Items.FLINT,1)); //just in case of recipe conflicts
            GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.improvedWorktableBlock,1),new ItemStack(Blocks.CRAFTING_TABLE,1),new ItemStack(Items.FLINT,1)); //just in case of recipe conflicts
        }
        else
        {
            GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.improvedWorktableBlock,1),new ItemStack(Blocks.CRAFTING_TABLE,1));
//            GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.improvedCraftingTableBlock,1),new ItemStack(Blocks.CRAFTING_TABLE,1));
        }

        GameRegistry.addRecipe(new ItemStack(ModBlocks.solidGeneratorBlock,1),"t","f","t",'t',new ItemStack(Blocks.REDSTONE_TORCH,1),'f',new ItemStack(ModBlocks.improvedFurnaceBlock,1));
        GameRegistry.addRecipe(new ItemStack(ModBlocks.charcoalBlock,1),"ccc","ccc","ccc",'c',new ItemStack(Items.COAL,1,1));
        GameRegistry.addShapelessRecipe(new ItemStack(Items.COAL,9,1),new ItemStack(ModBlocks.charcoalBlock,1));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.itemFurnaceUpgrade,1,0),"grg","rcr","glg",'c',"gemDiamond",'g',"ingotGold",'r',"blockRedstone",'l',"blockLapis"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(com.m4thg33k.lit.items.ModItems.itemFurnaceUpgrade,2,1),"grg","rcr","glg",'c',"gemDiamond",'g',"ingotGold",'r',new ItemStack(Items.BLAZE_ROD,1),'l',"blockLapis"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(com.m4thg33k.lit.items.ModItems.itemFurnaceUpgrade,2,2),"grg","rcr","glg",'c',"gemDiamond",'g',"ingotGold",'r',new ItemStack(com.m4thg33k.lit.blocks.ModBlocks.improvedFurnaceBlock,1),'l',"blockLapis"));

        GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.improvedHopperBlock,1,0),new ItemStack(Blocks.HOPPER,1));

        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Blocks.CRAFTING_TABLE, 1), "workbench"));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Blocks.FURNACE, 1), "furnace"));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Blocks.HOPPER, 1), "hopper"));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Blocks.CHEST, 1), "chest"));
    }
}
