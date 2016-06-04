package com.m4thg33k.lit.inventory;

import com.m4thg33k.lit.tiles.TileImprovedWorktable;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;

public class ContainerImprovedWorktable extends Container {

    public InventoryCraftingPersists craftMatrix;
    public IInventory craftingResult;

    protected TileImprovedWorktable tile;

    public ContainerImprovedWorktable(InventoryPlayer playerInventory, TileImprovedWorktable tile)
    {
        this.tile = tile;

        craftingResult = new InventoryCraftResult();
        craftMatrix = new InventoryCraftingPersists(this, tile, 3, 3);

        //add crafting result
        this.addSlotToContainer(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftingResult, 0, 124, 35));
        int i;
        int j;

        //add crafting grid
        for (i = 0; i < 3; i++)
        {
            for (j = 0; j < 3; j++)
            {
                this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18));
            }
        }

        //add player main inventory
        this.addPlayerInventory(playerInventory, 8, 84);

        this.onCraftMatrixChanged(this.craftMatrix);
    }

    public void addPlayerInventory(InventoryPlayer playerInventory, int x, int y)
    {
        
    }
}
