package com.m4thg33k.lit.inventory;

import com.m4thg33k.lit.tiles.TileImprovedCraftingTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;

public class SlotCraftingLinked extends SlotCrafting {


    TileImprovedCraftingTable linkedInventory;
    protected InventoryCrafting iCrafting;
    int start;
    int end;

    public SlotCraftingLinked(EntityPlayer player, InventoryCrafting crafting, IInventory result, int slotIndex, int xCoord, int yCoord, TileImprovedCraftingTable linkedInventory, int start, int end)
    {
        super(player,crafting,result,slotIndex,xCoord,yCoord);

        iCrafting = crafting;
        this.linkedInventory = linkedInventory;
        this.start = start;
        this.end = end;
    }

    @Override
    public ItemStack onTake(EntityPlayer playerIn, ItemStack stack) {
        super.onTake(playerIn, stack);


        if (!playerIn.world.isRemote) {
            for (int i = start; i < end; i++) {
                linkedInventory.setInventorySlotContents(i,iCrafting.getStackInSlot(i-start));
            }
        }
        linkedInventory.syncInventories();
        return stack;
    }

}
