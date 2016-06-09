package com.m4thg33k.lit.tiles;

import com.m4thg33k.lit.core.util.LogHelper;
import com.m4thg33k.lit.lib.Names;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;

public class TileImprovedWorktable extends TileEntity implements IInventory{


    private int CRAFT_SLOT = 0;
    protected int CHEST_SLOT = -1;
    protected int INVENTORY_SIZE = 9;

//    protected IInventory craftingOutput = null;

    protected ItemStack result = null;
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

    public void setFacing(EnumFacing facing)
    {
        this.facing = facing;
    }

    public EnumFacing getFacing()
    {
        return facing;
    }

    public ItemStack getResult()
    {
        return result;
    }

    public void setResult(ItemStack stack)
    {
        if (stack == null)
        {
            result = null;
            return;
        }
        result = stack.copy();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        NBTTagList list = compound.getTagList("Items",10);
        for (int i=0; i<list.tagCount(); i++)
        {
            NBTTagCompound stackTag = list.getCompoundTagAt(i);
            int slot = stackTag.getByte("Slot") & 0xff;
            if (isValidSlot(slot))
            {
                inventory[slot] = ItemStack.loadItemStackFromNBT(stackTag);
            }
        }

        if (compound.hasKey("CustomName"))
        {
            this.customName = compound.getString("CustomName");
        }

        if (compound.hasKey("Facing"))
        {
            this.facing = EnumFacing.values()[compound.getInteger("Facing")];
        }

        if (compound.hasKey("Result"))
        {
            this.result = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("Result"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tagCompound =  super.writeToNBT(compound);

        NBTTagList list = new NBTTagList();
        for (int i=0; i<getSizeInventory(); i++)
        {
            if (inventory[i] != null)
            {
                NBTTagCompound stackTag = new NBTTagCompound();
                stackTag.setByte("Slot",(byte)i);
                inventory[i].writeToNBT(stackTag);
                list.appendTag(stackTag);
            }
        }
        tagCompound.setTag("Items",list);

        tagCompound.setInteger("Facing", facing.ordinal());

        if (this.hasCustomName())
        {
            tagCompound.setString("CustomName",customName);
        }

        if (result != null)
        {
            NBTTagCompound resultTag = new NBTTagCompound();
            result.writeToNBT(resultTag);
            tagCompound.setTag("Result",resultTag);
        }

        return tagCompound;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tagCompound = new NBTTagCompound();
        NBTTagList list = new NBTTagList();

        for (int i=CRAFT_SLOT; i < CRAFT_SLOT+9; i++)
        {
            if (inventory[i] != null)
            {
                NBTTagCompound stackTag = new NBTTagCompound();
                stackTag.setByte("Slot", (byte)i);
                inventory[i].writeToNBT(stackTag);
                list.appendTag(stackTag);
            }
        }
        tagCompound.setTag("Items", list);

        if (result != null)
        {
            NBTTagCompound resultTag = new NBTTagCompound();
            result.writeToNBT(resultTag);
            tagCompound.setTag("Result", resultTag);
        }

        LogHelper.info("Sending Worktable Update Packet");
        return new SPacketUpdateTileEntity(pos, 0, tagCompound);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());

    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        this.readFromNBT(tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagList tagList = pkt.getNbtCompound().getTagList("Items",10);
        for (int i=0; i<tagList.tagCount(); i++)
        {
            NBTTagCompound stackTag = tagList.getCompoundTagAt(i);
            int slot = stackTag.getByte("Slot") & 0xff;
            if (isValidSlot(slot))
            {
                inventory[slot] = ItemStack.loadItemStackFromNBT(stackTag);
            }
        }

        if (pkt.getNbtCompound().hasKey("Result"))
        {
            result = ItemStack.loadItemStackFromNBT(pkt.getNbtCompound().getCompoundTag("Result"));
        }
        else
        {
            result = null;
        }
    }
}
