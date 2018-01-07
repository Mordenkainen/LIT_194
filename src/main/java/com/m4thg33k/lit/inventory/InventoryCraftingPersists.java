package com.m4thg33k.lit.inventory;

import com.m4thg33k.lit.LIT;
import com.m4thg33k.lit.network.packets.PacketLITCraftingUpdate;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class InventoryCraftingPersists extends InventoryCrafting{

    private final int length;
    private final Container eventHandler;
    private final IInventory parentInventory;

    public InventoryCraftingPersists(Container eventHandler, IInventory parentInventory, int width, int height)
    {
        super(eventHandler, width, height);

        int k = width * height;

        this.parentInventory = parentInventory;
        this.length = k;
        this.eventHandler = eventHandler;
    }

    @Override
    public int getSizeInventory() {
        return length;
    }

    @Nullable
    @Override
    public ItemStack getStackInSlot(int index) {
        if (index < 0 || index >= length)
        {
            return ItemStack.EMPTY;
        }

        return parentInventory.getStackInSlot(index);
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Nullable
    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack parentStack = this.getStackInSlot(index);
        if (!parentStack.isEmpty())
        {
            ItemStack stack;

            if (parentStack.getCount() <= count)
            {
                stack = parentStack;
                this.setInventorySlotContents(index, ItemStack.EMPTY);
                this.eventHandler.onCraftMatrixChanged(this);
                return stack;
            }
            else
            {
                stack = this.getStackInSlot(index).splitStack(count);

                if (this.getStackInSlot(index).getCount() <= 0)
                {
                    this.setInventorySlotContents(index, ItemStack.EMPTY);
                }

                this.eventHandler.onCraftMatrixChanged(this);
                return stack;
            }
        }
        else
        {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.parentInventory.setInventorySlotContents(index, stack);
        this.eventHandler.onCraftMatrixChanged(this);
    }

    @Override
    public void markDirty() {
        this.parentInventory.markDirty();
        this.eventHandler.onCraftMatrixChanged(this);

        LIT.proxy.sendPacketToServerOnly(new PacketLITCraftingUpdate());
    }

    @Override
    public void clear() {
        //do nothing
    }
}
