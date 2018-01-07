package com.m4thg33k.lit.blocks;

import com.m4thg33k.lit.LIT;
import com.m4thg33k.lit.api.LitStateProps;
import com.m4thg33k.lit.lib.Names;
import com.m4thg33k.lit.tiles.TileImprovedHopper;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
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
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
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
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(LitStateProps.CONNECTIONS, (facing == EnumFacing.DOWN ? EnumFacing.DOWN : facing.getOpposite()));
    }


    private static final EnumFacing[] validRotationAxes = new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN};

    @Override
    public EnumFacing[] getValidRotations(World world, BlockPos pos) {
        return validRotationAxes;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.0625,0.625,0.0625,0.9375,0.65625,0.9375));
        addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.0,0.625,0.0,0.9375,1.0,0.125));
        addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.125,0.625,0.875,1.0,1.0,1.0));
        addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.875,0.625,0.0,1.0,1.0,0.875));
        addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.0,0.625,0.125,0.125,1.0,1.0));
    }
}
