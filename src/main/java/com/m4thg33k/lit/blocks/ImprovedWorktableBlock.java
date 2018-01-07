package com.m4thg33k.lit.blocks;

import com.m4thg33k.lit.LIT;
import com.m4thg33k.lit.gui.LitGuiHandler;
import com.m4thg33k.lit.lib.Names;
import com.m4thg33k.lit.tiles.TileImprovedWorktable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ImprovedWorktableBlock extends BaseBlock {

    public ImprovedWorktableBlock()
    {
        super(Names.IMPROVED_WORKTABLE, Material.GROUND, 2.75f, 10.0f);
    }

    @Override
    public boolean isToolEffective(String type, IBlockState state) {
        return (type.equals("pickaxe") || type.equals("axe"));
    }

    @Override
    public void handleRegName() {
        this.setRegistryName(LIT.MODID, Names.IMPROVED_WORKTABLE);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote)
        {
            playerIn.openGui(LIT.instance, LitGuiHandler.IMPROVED_WORKTABLE,worldIn,pos.getX(),pos.getY(),pos.getZ());
        }

        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nonnull
    @Override
    public TileEntity createTileEntity(@Nonnull World world,@Nonnull IBlockState state) {
        return new TileImprovedWorktable();
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileImprovedWorktable tile = (TileImprovedWorktable)worldIn.getTileEntity(pos);
        InventoryHelper.dropInventoryItems(worldIn,pos,tile);
        super.breakBlock(worldIn,pos,state);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return true;
    }

    @Override
    public boolean isNormalCube(IBlockState state) {
        return true;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        TileImprovedWorktable tile = (TileImprovedWorktable)worldIn.getTileEntity(pos);
        tile.setFacing(placer.getHorizontalFacing().getOpposite());
    }
}
