package com.m4thg33k.lit.inventory;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class ContainerBase<T extends TileEntity> extends Container {

    protected double maxDistance = 64.0;
    protected T tileEntity;
    protected final Block originalBlock;
    protected final BlockPos blockPos;
    protected final World world;
    protected final IItemHandler itemHandler;

    public List<Container> subContainers = Lists.newArrayList();

    public ContainerBase(T tile)
    {
        this(tile, null);
    }

    public ContainerBase(T tile, EnumFacing direction)
    {
        this.tileEntity = tile;
        if (tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction))
        {
            itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction);
        }
        else
        {
            itemHandler = new EmptyHandler();
        }

        this.world = tileEntity.getWorld();
        this.blockPos = tile.getPos();
        this.originalBlock = world.getBlockState(blockPos).getBlock();
    }

    public void syncDataOnOpen(EntityPlayerMP playerIn)
    {
        WorldServer server = playerIn.getServerWorld();
        for (EntityPlayer player : server.playerEntities)
        {
            if (player == playerIn)
            {
                continue;
            }
            if (player.openContainer instanceof ContainerBase)
            {
                if (this.isSameGUI((ContainerBase<T>) player.openContainer))
                {
                    syncWithOpenContainer((ContainerBase<T>) player.openContainer, playerIn);
                    return;
                }
            }
        }

        syncToNewContainer(playerIn);
    }

    public T getTileEntity()
    {
        return this.tileEntity;
    }

    public IItemHandler getItemHandler()
    {
        return this.itemHandler;
    }

    protected void syncWithOpenContainer(ContainerBase<T> other, EntityPlayerMP player)
    {
    }

    protected void syncToNewContainer(EntityPlayerMP player)
    {

    }

    public boolean isSameGUI(ContainerBase<T> other)
    {
        return this.tileEntity == other.tileEntity;
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
        Block block = world.getBlockState(blockPos).getBlock();

        return block != Blocks.AIR && block == originalBlock && playerIn.getDistanceSq(blockPos.add(0.5,0.5,0.5)) <= maxDistance;
    }

    @Nonnull
    @Override
    public NonNullList<ItemStack> getInventory() {
        return super.getInventory();
    }

    public String getInventoryDisplayName()
    {
        if (tileEntity instanceof IWorldNameable)
        {
            IWorldNameable nameable = (IWorldNameable) tileEntity;
            ITextComponent name = nameable.getDisplayName();
            return name != null ? name.getFormattedText() : nameable.getName();
        }
        return null;
    }

    protected int playerInventoryStart = -1;

    protected void addPlayerInventory(InventoryPlayer playerInventory, int startX, int startY)
    {
        int index = 9;

        int start = this.inventorySlots.size();

        for (int row = 0; row < 3; row++)
        {
            for (int col = 0; col < 9; col++)
            {
                this.addSlotToContainer(new Slot(playerInventory, index, startX + col * 18, startY + row * 18));
                index ++;
            }
        }

        index = 0;
        for (int col=0; col < 9; col++)
        {
            this.addSlotToContainer(new Slot(playerInventory, index, startX + col * 18, startY + 58));
            index++;
        }

        playerInventoryStart = start;
    }

    @Nonnull
    @Override
    protected Slot addSlotToContainer(Slot slotIn)
    {
        if (playerInventoryStart >= 0)
        {
            throw new RuntimeException("You must add the player's inventory last; re-order your code!");
        }
        return super.addSlotToContainer(slotIn);
    }

    @Nullable
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        if (playerInventoryStart < 0)
        {
            return ItemStack.EMPTY;
        }

        int inventorySize = this.inventorySlots.size();

        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack stackInSlot = slot.getStack();
            itemStack = stackInSlot.copy();

            if (index < playerInventoryStart)
            {
                if (!this.mergeItemStack(stackInSlot,playerInventoryStart,inventorySize,true))
                {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(stackInSlot, itemStack);
            }
            else if (index < playerInventoryStart + 27)
            {
                if (!this.mergeItemStack(stackInSlot,playerInventoryStart+27,inventorySize,true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (index < inventorySize)
            {
                if (!this.mergeItemStack(stackInSlot,playerInventoryStart,playerInventoryStart+27,false))
                {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.getCount() == 0)
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemStack.getCount() == stackInSlot.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn,stackInSlot);
        }

        return itemStack;
    }

}
