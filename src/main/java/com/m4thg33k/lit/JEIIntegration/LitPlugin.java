package com.m4thg33k.lit.JEIIntegration;

import com.m4thg33k.lit.blocks.ModBlocks;
import com.m4thg33k.lit.client.gui.GuiImprovedFurnace;
import com.m4thg33k.lit.client.gui.GuiImprovedWorktable;
import com.m4thg33k.lit.inventory.ContainerImprovedFurnace;
import com.m4thg33k.lit.inventory.ContainerImprovedWorktable;
import mezz.jei.api.*;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

@JEIPlugin
public class LitPlugin extends BlankModPlugin{
    @Override
    public void register(@Nonnull IModRegistry registry) {

        registry.addRecipeClickArea(GuiImprovedWorktable.class,88,32,28,23, VanillaRecipeCategoryUid.CRAFTING);
        registry.addRecipeClickArea(GuiImprovedFurnace.class,77,34,22,16,VanillaRecipeCategoryUid.SMELTING,VanillaRecipeCategoryUid.FUEL);

        IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();

        registry.addRecipeCatalyst(new ItemStack(ModBlocks.improvedWorktableBlock),VanillaRecipeCategoryUid.CRAFTING);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.improvedFurnaceBlock),VanillaRecipeCategoryUid.SMELTING,VanillaRecipeCategoryUid.FUEL);


        recipeTransferRegistry.addRecipeTransferHandler(ContainerImprovedWorktable.class,VanillaRecipeCategoryUid.CRAFTING,1,9,10,36);
        recipeTransferRegistry.addRecipeTransferHandler(ContainerImprovedFurnace.class,VanillaRecipeCategoryUid.SMELTING,1,1,3,36);
        recipeTransferRegistry.addRecipeTransferHandler(ContainerImprovedFurnace.class,VanillaRecipeCategoryUid.FUEL,0,1,3,36);
    }
}
