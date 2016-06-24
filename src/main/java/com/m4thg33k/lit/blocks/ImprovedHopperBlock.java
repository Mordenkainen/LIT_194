package com.m4thg33k.lit.blocks;

import com.m4thg33k.lit.LIT;
import com.m4thg33k.lit.api.LitStateProps;
import com.m4thg33k.lit.core.util.LogHelper;
import com.m4thg33k.lit.lib.Names;
import com.m4thg33k.lit.tiles.TileImprovedHopper;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ImprovedHopperBlock extends BaseBlock {

    public ImprovedHopperBlock()
    {
        super(Names.IMPROVED_HOPPER, Material.IRON,3.0f,10.0f );

        this.setDefaultState(this.blockState.getBaseState().withProperty(LitStateProps.CONNECTIONS,EnumFacing.DOWN));
        this.setCreativeTab(LIT.tabLIT);
    }

    @Override
    public void handleRegName() {
        this.setRegistryName(LIT.MODID,Names.IMPROVED_HOPPER);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.0,0.625,0.0,1.0,1.0,1.0);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
//        LogHelper.info("Creating new hopper");
        return new TileImprovedHopper();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(LitStateProps.CONNECTIONS, EnumFacing.values()[meta%6]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(LitStateProps.CONNECTIONS).ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, LitStateProps.CONNECTIONS);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(LitStateProps.CONNECTIONS, (facing == EnumFacing.DOWN ? EnumFacing.DOWN : facing.getOpposite()));
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {

    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return super.hasComparatorInputOverride(state);
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        return super.getComparatorInputOverride(blockState, worldIn, pos);
    }

    private static final EnumFacing[] validRotationAxes = new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN};

    @Override
    public EnumFacing[] getValidRotations(World world, BlockPos pos) {
        return validRotationAxes;
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        return super.rotateBlock(world, pos, axis);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return super.getRenderType(state);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn) {
//        collidingBoxes.add(this.getBoundingBox(state,worldIn,pos));
//        super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.0625,0.625,0.0625,0.9375,0.65625,0.9375));

        addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.0,0.625,0.0,0.9375,1.0,0.125));
        addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.125,0.625,0.875,1.0,1.0,1.0));
        addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.875,0.625,0.0,1.0,1.0,0.875));
        addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.0,0.625,0.125,0.125,1.0,1.0));
    }
}
