package com.m4thg33k.lit.client.gui;

import com.m4thg33k.lit.inventory.ContainerImprovedWorktable;
import com.m4thg33k.lit.tiles.TileImprovedWorktable;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiImprovedWorktable extends GuiContainer{

    private static final ResourceLocation background = new ResourceLocation("textures/gui/container/crafting_table.png");

    private InventoryPlayer playerInventory;

    public GuiImprovedWorktable(InventoryPlayer inventoryPlayer, TileImprovedWorktable tileImprovedWorktable)
    {
        super(new ContainerImprovedWorktable(inventoryPlayer, tileImprovedWorktable));

        this.xSize = 176;
        this.ySize = 166;

        this.playerInventory = inventoryPlayer;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String name = "Improved Crafting Table";
        this.fontRendererObj.drawString(name, xSize/2 - this.fontRendererObj.getStringWidth(name)/2, 6, 0x404040);
        this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, ySize-94, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0f,1.0f,1.0f,1.0f);
        mc.getTextureManager().bindTexture(background);
        int i = (this.width - this.xSize)/2;
        int j = (this.height = this.ySize)/2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }

    public void updateDisplay() {}
}
