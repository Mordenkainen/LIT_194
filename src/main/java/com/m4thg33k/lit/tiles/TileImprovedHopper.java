package com.m4thg33k.lit.tiles;

import com.m4thg33k.lit.api.LitStateProps;
import com.m4thg33k.lit.core.util.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

//much of this code was adapted from the TileEntityHopper code.
public class TileImprovedHopper extends TileEntityLockableLoot implements IHopper, ITickable{

    protected ItemStack[] inventory = new ItemStack[9];
    protected String customName;
    protected int transferCooldown = -1;


    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        this.inventory = new ItemStack[this.getSizeInventory()];

        if (compound.hasKey("CustomName"))
        {
            this.customName = compound.getString("CustomName");
        }

        this.transferCooldown = compound.getInteger("TransferCooldown");
        NBTTagList tagList = compound.getTagList("Items",10);

        for (int i=0; i < tagList.tagCount(); i++)
        {
            NBTTagCompound stackTag = tagList.getCompoundTagAt(i);
            int j = stackTag.getByte("Slot");

            if (j >= 0 && j < this.inventory.length)
            {
                this.inventory[j] = ItemStack.func_77949_a(stackTag);
            }
        }

    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        NBTTagList tagList = new NBTTagList();

        for (int i=0; i < this.inventory.length; i++)
        {
            if (this.inventory[i] != null)
            {
                NBTTagCompound slotTag = new NBTTagCompound();
                slotTag.setByte("Slot", (byte)i);
                this.inventory[i].writeToNBT(slotTag);
                tagList.appendTag(slotTag);
            }
        }

        compound.setTag("Items", tagList);


        compound.setInteger("TransferCooldown", this.transferCooldown);

        if (this.hasCustomName())
        {
            compound.setString("CustomName", this.customName);
        }

        return compound;
    }

    @Override
    public int getSizeInventory() {
        return this.inventory.length;
    }

    @Nullable
    @Override
    public ItemStack getStackInSlot(int index) {
        return (index>=0 && index<this.getSizeInventory()) ? this.inventory[index] : null;
    }

    @Nullable
    @Override
    public ItemStack decrStackSize(int index, int count) {
        return ItemStackHelper.getAndSplit(this.inventory, index, count);
    }

    @Nullable
    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(this.inventory, index);
    }

    @Override
    public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
        if (index >=0 && index < this.getSizeInventory())
        {
            this.inventory[index] = stack;
            if (stack != null && stack.stackSize > this.getInventoryStackLimit())
            {
                stack.stackSize = this.getInventoryStackLimit();
            }
        }
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : "container.improvedHopper";
    }

    @Override
    public boolean hasCustomName() {
        return this.customName != null && !this.customName.isEmpty();
    }

    public void setCustomName(String name)
    {
        this.customName = name;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world.getTileEntity(this.pos) == this && player.getDistanceSq(this.getPos().add(0.5,0.5,0.5)) <= 64;
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
    public void update() {
        if (this.world != null && !this.world.isRemote)
        {
            --this.transferCooldown;

//            LogHelper.info("Test");

            if (!this.isOnTransferCooldown())
            {
//                LogHelper.info(transferCooldown);
                this.setTransferCooldown(0);
                this.updateHopper();
            }
        }
    }

    public boolean updateHopper()
    {
        if (this.world != null && !this.world.isRemote)
        {
            if (!this.isOnTransferCooldown())
            {
                boolean flag = false;

                if (!this.isEmpty())
                {
                    flag = this.transferItemsOut();
                }

                if (!this.isFull())
                {
                    flag = TileEntityHopper.captureDroppedItems(this) || flag;
                }

                if (flag)
                {
                    this.setTransferCooldown(8);
                    this.markDirty();
                    return true;
                }
            }

            return false;
        }
        return false;
    }

    protected boolean isFull()
    {
        for (ItemStack stack : this.inventory)
        {
            if (stack == null || stack.stackSize != stack.getMaxStackSize())
            {
                return false;
            }
        }

        return true;
    }

    protected boolean isEmpty()
    {
        for (ItemStack stack : this.inventory)
        {
            if (stack != null)
            {
                return false;
            }
        }

        return true;
    }

    protected boolean transferItemsOut()
    {
        IInventory iInventory = this.getInventoryForHopperTransfer();

        if (iInventory == null)
        {
            return false;
        }

        EnumFacing sideIn = this.world.getBlockState(this.pos).getValue(LitStateProps.CONNECTIONS).getOpposite();

        if (this.isInventoryFull(iInventory, sideIn))
        {
            return false;
        }

        for (int i=0; i < this.getSizeInventory(); i++)
        {
            if (this.getStackInSlot(i) != null)
            {
                ItemStack myCopy = this.getStackInSlot(i).copy();
                ItemStack altered = TileEntityHopper.putStackInInventoryAllSlots(iInventory, this.decrStackSize(i, 1), sideIn);

                if (altered == null || altered.stackSize == 0)
                {
                    iInventory.markDirty();
                    return true;
                }

                this.setInventorySlotContents(i, myCopy);
            }
        }

        return false;
    }

    protected boolean isInventoryFull(IInventory inventoryIn, EnumFacing side)
    {
        if (inventoryIn instanceof ISidedInventory)
        {
            ISidedInventory iSidedInventory = (ISidedInventory)inventoryIn;
            int[] validSides = iSidedInventory.getSlotsForFace(side);

            for (int k : validSides)
            {
                ItemStack inSlot = iSidedInventory.getStackInSlot(k);
                if (inSlot == null || inSlot.stackSize != inSlot.getMaxStackSize())
                {
                    return false;
                }
            }
        }
        else
        {
            int i = inventoryIn.getSizeInventory();

            for (int j=0; j < i; j++)
            {
                ItemStack inSlot = inventoryIn.getStackInSlot(j);

                if (inSlot == null || inSlot.stackSize != inSlot.getMaxStackSize())
                {
                    return false;
                }
            }
        }

        return true;
    }



    protected static ItemStack insertStack(IInventory receiever, ItemStack stack, int index, EnumFacing side)
    {
        ItemStack itemStack = receiever.getStackInSlot(index);

        if (canInsertItemInSlot(receiever, stack, index, side)) {
            boolean flag = false;

            if (itemStack == null) {
                int max = Math.min(stack.getMaxStackSize(), receiever.getInventoryStackLimit());
                if (max >= stack.stackSize) {
                    receiever.setInventorySlotContents(index, stack);
                } else {
                    receiever.setInventorySlotContents(index, stack.splitStack(max));
                }
                flag = true;
            }
            else if (canCombine(itemStack, stack))
            {
                int max = Math.min(stack.getMaxStackSize(), receiever.getInventoryStackLimit());
                if (max > itemStack.stackSize) {
                    int i = max - itemStack.stackSize;
                    int j = Math.min(stack.stackSize, i);
                    stack.stackSize -= j;
                    itemStack.stackSize += j;
                    flag = j > 0;
                }
            }

            if (flag)
            {
                if (receiever instanceof TileEntityHopper)
                {
                    TileEntityHopper hopper = (TileEntityHopper) receiever;

                    if (hopper.mayTransfer())
                    {
                        hopper.setTransferCooldown(8);
                    }
                }

                else if (receiever instanceof TileImprovedHopper)
                {
                    TileImprovedHopper hopper = (TileImprovedHopper) receiever;

                    if (hopper.mayTransfer())
                    {
                        hopper.setTransferCooldown(8);
                    }
                }

                receiever.markDirty();
            }
        }

        return stack;
    }

    protected static boolean canInsertItemInSlot(IInventory receiver, ItemStack stack, int index, EnumFacing side)
    {
        return receiver.isItemValidForSlot(index, stack) && (!(receiver instanceof ISidedInventory) || ((ISidedInventory)receiver).canInsertItem(index,stack,side));
    }

    protected static boolean canCombine(ItemStack first, ItemStack second)
    {
        return first.getItem() == second.getItem() && first.getMetadata() == second.getMetadata() && first.stackSize <= first.getMaxStackSize() && ItemStack.areItemStackTagsEqual(first, second);
    }

    public boolean mayTransfer()
    {
        return this.transferCooldown <= 1;
    }

    public void setTransferCooldown(int ticks)
    {
        this.transferCooldown = ticks;
    }

    protected IInventory getInventoryForHopperTransfer()
    {
        EnumFacing enumFacing = this.world.getBlockState(this.pos).getValue(LitStateProps.CONNECTIONS);
        BlockPos position = this.pos.add(enumFacing.getFrontOffsetX(), enumFacing.getFrontOffsetY(), enumFacing.getFrontOffsetZ());
        return TileEntityHopper.getInventoryAtPosition(this.world, position.getX(), position.getY(), position.getZ());
    }

    public boolean isOnTransferCooldown()
    {
        return this.transferCooldown > 0;
    }

    @Override
    public double getXPos() {
        return this.pos.getX() + 0.5;
    }

    @Override
    public double getYPos() {
        return this.pos.getY() + 0.5;
    }

    @Override
    public double getZPos() {
        return this.pos.getZ() + 0.5;
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return null; // TODO: 6/24/2016
    }

    @Override
    public String getGuiID() {
        return null; //// TODO: 6/24/2016
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
        for (int i=0; i < this.inventory.length; i++)
        {
            this.inventory[i] = null;
        }
    }
}
