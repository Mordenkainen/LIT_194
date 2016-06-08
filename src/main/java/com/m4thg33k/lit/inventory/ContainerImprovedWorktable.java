package com.m4thg33k.lit.inventory;

import com.m4thg33k.lit.client.gui.GuiImprovedWorktable;
import com.m4thg33k.lit.tiles.TileImprovedWorktable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerImprovedWorktable extends ContainerBase<TileImprovedWorktable> {

    public InventoryCraftingPersists craftMatrix;
    public IInventory craftingResult;


    public ContainerImprovedWorktable(InventoryPlayer playerInventory, TileImprovedWorktable tile)
    {
        super(tile);

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

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        this.craftingResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.world));
        this.tileEntity.setResult(this.craftingResult.getStackInSlot(0));
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
        return slotIn.inventory != this.craftingResult && super.canMergeSlot(stack, slotIn);
    }


    public void updateGUI()
    {
        if (this.tileEntity.getWorld().isRemote)
        {
            Minecraft.getMinecraft().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    ContainerImprovedWorktable.clientGuiUpdate();
                }
            });
        }
    }

    @SideOnly(Side.CLIENT)
    private static void clientGuiUpdate()
    {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (screen instanceof GuiImprovedWorktable)
        {
            ((GuiImprovedWorktable) screen).updateDisplay();
        }
    }
}
