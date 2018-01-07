package com.m4thg33k.lit.client.render.tiles;

import com.m4thg33k.lit.tiles.TileImprovedCraftingTable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;

public class TileImprovedCraftingTableRenderer extends TileEntitySpecialRenderer<TileImprovedCraftingTable> {

    private double factor = 0.1875;

    public TileImprovedCraftingTableRenderer()
    {

    }

    @Override
    public void renderTileEntityAt(TileImprovedCraftingTable tile, double x, double y, double z, float partialTicks, int destroyStage) {
        if (tile==null)
        {
            return;
        }

        EnumFacing facing = tile.getFacing();



        GlStateManager.pushMatrix();
        GlStateManager.color(1.0f,1.0f,1.0f,1.0f);

        GlStateManager.translate(x,y,z);

        float rotation;

        switch (facing)
        {
            case SOUTH:
                rotation = 90.0f;
                GlStateManager.translate(0,0,+1);
                break;
            case WEST:
                rotation = 0.0f;
                break;
            case EAST:
                rotation = 180.0f;
                GlStateManager.translate(1,0,1);
                break;
            case NORTH:
                rotation = 270.0f;
                GlStateManager.translate(1,0,0);
                break;
            default:
                rotation = 0.0f;
        }

        GlStateManager.rotate(rotation,0,1,0);

        World world = tile.getWorld();
        for (int i=0;i<9;i++)
        {
            this.renderItem(world,tile.getStackInSlot(i),(i/3)-1,0,(i%3)-1,0.125);
        }

        ItemStack result = tile.getResult();
        renderItem(tile.getWorld(),result,0,0.5,0,0.5);

        GlStateManager.popMatrix();
    }

    private void renderItem(World world, ItemStack stack, int xIndex,double extraHeight,int zIndex,double scale)
    {
        RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
        if (stack!=null)
        {
            ItemStack single = ItemHandlerHelper.copyStackWithSize(stack, 1);

            EntityItem entityItem = new EntityItem(world,0.0d,0.0d,0.0d,single);
            entityItem.getEntityItem().setCount(1);
            entityItem.hoverStart = 0.0f;
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.5,1+0.0625+extraHeight,0.5);
            GlStateManager.translate(xIndex*-factor,0,zIndex*factor);
            GlStateManager.disableLighting();

            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            float rotation = (float)(720.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);

            GlStateManager.rotate(rotation,0,1,0);
            GlStateManager.scale(scale,scale,scale);
            GlStateManager.pushAttrib();
            RenderHelper.enableStandardItemLighting();
            itemRenderer.renderItem(entityItem.getEntityItem(),ItemCameraTransforms.TransformType.FIXED);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popAttrib();

            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }
}
