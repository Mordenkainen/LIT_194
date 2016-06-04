package com.m4thg33k.lit.tiles;

import com.m4thg33k.lit.lib.Names;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;

public class TileImprovedWorktable extends TileEntity implements IInventory{


    private int CRAFT_SLOT = 0;
    private int CHEST_SLOT = 9;
    private int INVENTORY_SIZE = 36;

    protected IInventory craftingOutput = null;

    protected ItemStack result;
    protected ItemStack[] inventory = new ItemStack[INVENTORY_SIZE];


    protected EnumFacing facing = EnumFacing.NORTH;
    protected String customName;

    public TileImprovedWorktable()
    {
        super();
    }

    public boolean isValidSlot(int index)
    {
        return index >= CRAFT_SLOT && index < INVENTORY_SIZE;
    }

    @Override
    public int getSizeInventory() {
        return INVENTORY_SIZE;
    }

    @Nullable
    @Override
    public ItemStack getStackInSlot(int index) {
        if (isValidSlot(index))
        {
            return inventory[index];
        }
        return null;
    }

    @Nullable
    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (getStackInSlot(index) != null)
        {
            if (inventory[index].stackSize <= count)
            {
                ItemStack itemStack = inventory[index];
                inventory[index] = null;
                markDirty();
                return itemStack;
            }
            ItemStack itemStack = inventory[index].splitStack(count);
            if (inventory[index].stackSize == 0)
            {
                inventory[index] = null;
            }
            markDirty();
            return itemStack;
        }
        return null;
    }

    @Nullable
    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stackInSlot = getStackInSlot(index);
        if (stackInSlot != null)
        {
            inventory[index] = null;
        }
        return stackInSlot;
    }

    @Override
    public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
        if (isValidSlot(index))
        {
            inventory[index] = stack;

            if (stack != null && stack.stackSize==0)
            {
                inventory[index] = null;
            }
            if (stack != null && stack.stackSize > getInventoryStackLimit())
            {
                stack.stackSize = getInventoryStackLimit();
            }

            markDirty();
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj!=null && worldObj.getTileEntity(pos) == this && player.getDistanceSq(pos.add(0.5,0.5,0.5)) <= 64;
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        for (int i=0; i < getSizeInventory(); i++)
        {
            inventory[i] = null;
        }
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : "container." + Names.IMPROVED_WORKTABLE;
    }

    @Override
    public boolean hasCustomName() {
        return this.customName != null && !this.customName.equals("");
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString(getName());
    }
}
