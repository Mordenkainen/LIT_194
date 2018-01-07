package com.m4thg33k.lit.inventory;

import com.m4thg33k.lit.tiles.TileSolidGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class ContainerSolidGenerator extends Container{

    private TileSolidGenerator te;
    public int storedEnergy = 0;
    public int maxEnergy = 0;

    public ContainerSolidGenerator(InventoryPlayer playerInventory, TileSolidGenerator tileSolidGenerator)
    {
        this.te = tileSolidGenerator;

        //te inventory
        this.addSlotToContainer(new Slot(te,0,80,34));

        //player inventory slots
        for (int y=0;y<3;y++)
        {
            for (int x=0;x<9;x++)
            {
                addSlotToContainer(new Slot(playerInventory,x+y*9+9,8+x*18,84+y*18));
            }
        }

        //player hotbar
        for (int x=0;x<9;x++)
        {
            addSlotToContainer(new Slot(playerInventory,x,8+x*18,142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return te.isUsableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack previous = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);

        if (slot!=null && slot.getHasStack())
        {
            ItemStack current = slot.getStack();
            previous = current.copy();

            // [...] custom behaviour
            if (index < te.getSizeInventory())
            {
                //from the te
                if (!mergeItemStack(current,te.getSizeInventory(),te.getSizeInventory()+36,true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else
            {
                //from player inventory
                boolean canBurn = TileSolidGenerator.getItemBurnTime(current)>0;

                if (canBurn) //if it can be burned but not smelted
                {
                    if (!mergeItemStack(current,0,1,false))
                    {
                        return ItemStack.EMPTY;
                    }
                }

                else //can neither burn nor smelt (so don't move it
                {
                    return ItemStack.EMPTY;
                }
            }

            if (current.getCount() == 0)
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (current.getCount() == previous.getCount())
            {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn,current);
        }
        return previous;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        int newStoredEnergy = te.getEnergyStored(EnumFacing.DOWN);
        int newMaxEnergy = te.getMaxEnergyStored(EnumFacing.DOWN);

        for (IContainerListener listener : this.listeners)
        {
            if (storedEnergy != newStoredEnergy)
            {
                listener.sendProgressBarUpdate(this, 0, newStoredEnergy/1000);
                listener.sendProgressBarUpdate(this, 1, newStoredEnergy%1000);
            }
            if (maxEnergy != newMaxEnergy)
            {
                listener.sendProgressBarUpdate(this, 2, newMaxEnergy/1000);
                listener.sendProgressBarUpdate(this, 3, newMaxEnergy%1000);
            }
        }

        this.storedEnergy = newStoredEnergy;
        this.maxEnergy = newMaxEnergy;
    }

    @Override
    public void updateProgressBar(int id, int data) {
        switch (id)
        {
            case 0:
                storedEnergy = data*1000;
                break;
            case 1:
                storedEnergy += data;
                break;
            case 2:
                maxEnergy = data*1000;
                break;
            case 3:
                maxEnergy += data;
                break;
            default:
        }
    }

    public int getStoredEnergy()
    {
        return storedEnergy;
    }

    public int getMaxEnergy()
    {
        return maxEnergy;
    }
}

