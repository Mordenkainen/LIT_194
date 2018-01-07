package com.m4thg33k.lit.tiles;

import com.m4thg33k.lit.LIT;
import com.m4thg33k.lit.api.chest.ChestTypes;
import com.m4thg33k.lit.blocks.ModBlocks;
import com.m4thg33k.lit.core.util.InventorySortHelper;
import com.m4thg33k.lit.inventory.ContainerImprovedChest;
import com.m4thg33k.lit.network.packets.PacketChestSorting;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class TileImprovedChest extends TileEntityLockable implements ITickable, IInventory{

    protected int ticksSinceSync = -1;

    public float prevLidAngle;
    public float lidAngle;

    protected int numUsingPlayers;
    protected ChestTypes type;
    public NonNullList<ItemStack> inventory;
    protected EnumFacing facing;
    protected boolean inventoryTouched;
    protected String customName;

    public TileImprovedChest()
    {
        this(ChestTypes.getTypeByName("Improved"));
    }

    public TileImprovedChest(ChestTypes type)
    {
        super();
        this.type = type;
        this.inventory = NonNullList.<ItemStack>withSize(getSizeInventory(), ItemStack.EMPTY);
        this.facing = EnumFacing.NORTH;
    }

    public void setContents(ItemStack[] contents)
    {
        inventory = NonNullList.<ItemStack>withSize(getSizeInventory(), ItemStack.EMPTY);
        for (int i=0;i<contents.length;i++)
        {
            if (i<inventory.size() && contents[i] != null)
            {
                inventory.set(i, contents[i]);
            }
        }
        inventoryTouched = true;
    }

    @Override
    public int getSizeInventory() {
        return type.getSize();
    }

    public EnumFacing getFacing()
    {
        return this.facing;
    }

    public ChestTypes getType()
    {
        return this.type;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        inventoryTouched = true;
        return inventory.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (!inventory.get(index).isEmpty())
        {
            if (inventory.get(index).getCount() <= count)
            {
                ItemStack itemStack = inventory.get(index);
                inventory.set(index, ItemStack.EMPTY);
                markDirty();
                return itemStack;
            }
            ItemStack itemStack =inventory.get(index).splitStack(count);
            if (inventory.get(index).getCount()==0)
            {
                inventory.set(index, ItemStack.EMPTY);
            }
            markDirty();
            return itemStack;
        }
        else
        {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        inventory.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit())
        {
            stack.setCount(getInventoryStackLimit());
        }
        markDirty();
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : type.getTypeName();
    }

    @Override
    public boolean hasCustomName() {
        return this.customName!=null && !this.customName.equals("");
    }

    public void setCustomName(String name)
    {
        this.customName = name;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("Type"))
        {
            type = ChestTypes.getTypeByName(compound.getString("Type"));
        }

        NBTTagList list = compound.getTagList("Items",10);
        this.inventory = NonNullList.<ItemStack>withSize(getSizeInventory(), ItemStack.EMPTY);;

        if (compound.hasKey("CustomName"))
        {
            this.customName = compound.getString("CustomName");
        }

        for (int i=0;i<list.tagCount();i++)
        {
            NBTTagCompound stackTag = list.getCompoundTagAt(i);
            int slot = stackTag.getByte("Slot") & 0xff;
            if (slot >= 0 && slot<inventory.size())
            {
                inventory.set(slot, new ItemStack(stackTag));
            }
        }
        facing = EnumFacing.values()[compound.getInteger("Facing")];
//        facing = compound.getByte("Facing");

    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        NBTTagList list = new NBTTagList();
        for (int i=0;i<inventory.size();i++)
        {
            if (!inventory.get(i).isEmpty())
            {
                NBTTagCompound stackTag = new NBTTagCompound();
                stackTag.setByte("Slot",(byte)i);
                inventory.get(i).writeToNBT(stackTag);
                list.appendTag(stackTag);
            }
        }
        compound.setTag("Items",list);
        compound.setInteger("Facing",facing.ordinal());
//        compound.setByte("Facing",facing);

        if (this.hasCustomName())
        {
            compound.setString("CustomName",customName);
        }

        compound.setString("Type",type.getTypeName());

        return compound;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        if (world==null)
        {
            return true;
        }
        if (world.getTileEntity(pos) != this)
        {
            return false;
        }
        return player.getDistanceSq(pos.add(0.5,0.5,0.5))<=64;
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        switch (id)
        {
            case 1:
                numUsingPlayers = type;
                break;
            case 2:
                facing = EnumFacing.VALUES[(byte)type];
                break;
            case 3:
                facing = EnumFacing.VALUES[(byte)(type & 0x7)];
                numUsingPlayers = (type & 0xF8)>>3;
                break;
            default:
        }
        return true;
    }

    @Override
    public void update() {
        //resync clients with the server state
        if (world!=null && !this.world.isRemote && this.numUsingPlayers!=0 && (this.ticksSinceSync + pos.getX() + pos.getY() + pos.getZ())%200==0) {
            this.numUsingPlayers = 0;
            float var1 = 5.0f;
            List<EntityPlayer> var2 = this.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.getX() - var1, pos.getY() - var1, pos.getZ() - var1, pos.getX() + 1 + var1, pos.getY() + 1 + var1, pos.getZ() + 1 + var1));

            for (EntityPlayer var4 : var2) {
                if (var4.openContainer instanceof ContainerImprovedChest) {
                    ++this.numUsingPlayers;
                }
            }
        }

        if (world != null && !world.isRemote && ticksSinceSync<0)
        {
            world.addBlockEvent(pos, ModBlocks.improvedChestBlock,3,((numUsingPlayers << 3) & 0xF8 | ((byte)facing.ordinal() & 0x7)));
        }
        if (!world.isRemote && inventoryTouched)
        {
            inventoryTouched = false;
        }

        this.ticksSinceSync++;
        prevLidAngle = lidAngle;
        float f = 0.1f;
        if (numUsingPlayers>0 && lidAngle==0f)
        {
            double d = pos.getX() + 0.5;
            double d1 = pos.getZ() + 0.5;

            world.playSound((EntityPlayer)null,d,pos.getY()+0.5,d1, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS,0.5f,world.rand.nextFloat()*0.1f+0.9f);
//            world.playAuxSFX(d,pos.getY()+0.5,d1,"random.chestopen",0.5f,world.rand.nextFloat()*0.1f+0.9f);
        }
        if (numUsingPlayers == 0 && lidAngle > 0.0f || numUsingPlayers > 0 && lidAngle < 1.0f)
        {
            float f1 = lidAngle;
            if (numUsingPlayers>0)
            {
                lidAngle += f;
            }
            else
            {
                lidAngle -= f;
            }
            if (lidAngle>1.0f)
            {
                lidAngle = 1.0f;
            }
            float f2 = 0.5f;
            if (lidAngle < f2 && f1 >= f2)
            {
                double d2 = pos.getX() + 0.5;
                double d3 = pos.getZ() + 0.5;
                world.playSound((EntityPlayer)null,d2,pos.getY()+0.5,d3, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS,0.5f,world.rand.nextFloat()*0.1f+0.9f);
//                world.playSoundEffect(d2,pos.getY()+0.5,d3,"random.chestclosed",0.5f, world.rand.nextFloat()*0.1f+0.9f);
            }
            if (lidAngle<0.0f)
            {
                lidAngle = 0.0f;
            }
        }

    }

    @Override
    public void openInventory(EntityPlayer player) {
        if (world == null)
        {
            return;
        }
        numUsingPlayers++;
        world.addBlockEvent(pos,ModBlocks.improvedChestBlock,1,numUsingPlayers);
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        if (world == null)
        {
            return;
        }
        numUsingPlayers--;
        world.addBlockEvent(pos, ModBlocks.improvedChestBlock,1,numUsingPlayers);
    }

    public void setFacing(EnumFacing facing)
    {
        this.facing = facing;
        markDirty();
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbt = this.getUpdateTag();

        return new SPacketUpdateTileEntity(pos,0,nbt);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
//        nbt.setString("Type",getType().getTypeName());
//        nbt.setInteger("Facing",facing.ordinal());
        return nbt;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        this.readFromNBT(tag);
//        type = ChestTypes.getTypeByName(tag.getString("Type"));
//        facing = EnumFacing.values()[tag.getInteger("Facing")];
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
//        if (pkt.getTileEntityType() == 0)
//        {
            NBTTagCompound compound = pkt.getNbtCompound();
            this.readFromNBT(compound);
//            type = ChestTypes.getTypeByName(compound.getString("Type"));
//            facing = EnumFacing.values()[compound.getInteger("Facing")];
//        }
    }



    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (!this.inventory.get(index).isEmpty())
        {
            ItemStack stack = this.inventory.get(index);
            this.inventory.set(index, ItemStack.EMPTY);
            return stack;
        }
        return null;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    public void rotateAround()
    {
        facing.rotateY();
//        facing++;
//        if (facing > EnumFacing.EAST.ordinal())
//        {
//            facing = (byte)EnumFacing.NORTH.ordinal();
//        }
//        setFacing(facing);
        world.addBlockEvent(pos,ModBlocks.improvedChestBlock,2,(byte)facing.ordinal());
    }

    public void wasPlaced(EntityLivingBase entityLivingBase, ItemStack itemStack)
    {

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
        for (int i=0;i<this.inventory.size();i++)
        {
            this.inventory.set(i, ItemStack.EMPTY);
        }
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return null;
    }

    @Override
    public String getGuiID() {
        return "GemChest:" + type.getTypeName(); 
    }

    @Override
    public boolean canRenderBreaking() {
        return true;
    }

    public void readInventoryFromNBT(NBTTagCompound compound)
    {
        NBTTagList list = compound.getTagList("Items",10);
        for (int i=0;i<list.tagCount();i++)
        {
            NBTTagCompound stackTag = list.getCompoundTagAt(i);
            int slot = stackTag.getByte("Slot") & 0xff;
            if (slot >= 0 && slot<inventory.size())
            {
                inventory.set(slot, new ItemStack(stackTag));
            }
        }
    }

    public NBTTagCompound getInventoryNBT()
    {
        NBTTagList list = new NBTTagList();
        for (int i=0;i<this.getSizeInventory();i++)
        {
            ItemStack stack = this.getStackInSlot(i);
            if (stack!=null)
            {
                NBTTagCompound stackTag = new NBTTagCompound();
                stackTag.setByte("Slot",(byte)i);
                stack.writeToNBT(stackTag);
                list.appendTag(stackTag);
            }
        }

        if (list.tagCount()==0)
        {
            return null;
        }

        NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.setTag("Items",list);
        return tagCompound;
    }

    public void performSort(int sortType, boolean forward)
    {
        ItemStack[] sorted;
        switch (sortType)
        {
            case 1:
                sorted = InventorySortHelper.sortByMod(this,forward);
                break;
            default:
                sorted = InventorySortHelper.sortByBlocksAndItems(this, forward);
        }
        this.setContents(sorted);
        markDirty();
        world.markAndNotifyBlock(pos,null,world.getBlockState(pos), world.getBlockState(pos),1);

    }

    public void prepareSort(int sortType, boolean forward)
    {
        LIT.proxy.sendPacketToServerOnly(new PacketChestSorting(pos,sortType,forward));
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : inventory) {
            if (stack != null) {
                return false;
            }
        }
        return true;
    }
}
