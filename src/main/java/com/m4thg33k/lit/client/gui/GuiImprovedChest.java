package com.m4thg33k.lit.client.gui;

import com.m4thg33k.lit.api.chest.ChestTypes;
import com.m4thg33k.lit.core.util.LogHelper;
import com.m4thg33k.lit.inventory.ContainerImprovedChest;
import com.m4thg33k.lit.tiles.TileImprovedChest;
import javafx.scene.input.MouseButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.ArrayList;

public class GuiImprovedChest  extends GuiContainer{

    private ChestTypes type;
    private TileImprovedChest tileImprovedChest;

    public GuiImprovedChest(ChestTypes type, IInventory player, TileImprovedChest chest)
    {
        super(new ContainerImprovedChest(player,chest,type));
        this.tileImprovedChest = chest;
        this.type = type;
        this.xSize = type.getWidth();
        this.ySize = type.getHeight();
        this.allowUserInput = false;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        if (mouseX >= guiLeft - 8 && mouseX < guiLeft && mouseY >= guiTop && mouseY < guiTop + 8)
        {
            int mX = mouseX - (width-xSize)/2;
            int mY = mouseY - (height-ySize)/2;
            ArrayList<String> sort = new ArrayList<>();
            sort.add("Sort" + (isShiftKeyDown() ? " by Mod" : ""));
            sort.add(TextFormatting.ITALIC + "Left click -> Increasing");
            sort.add(TextFormatting.ITALIC + "Right click -> Decreasing");
            this.drawHoveringText(sort,mX,mY);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0f,1.0f,1.0f,1.0f);
        this.mc.getTextureManager().bindTexture(type.getGuiLocation());
        int x = (width-xSize)/2;
        int y = (height-ySize)/2;
        drawTexturedModalRect(x,y,0,0,8,8);
//        drawTexturedModalRect(x,y,0,0,xSize,ySize);

        ResourceLocation button = ChestTypes.sortButtonLocation;

        this.mc.getTextureManager().bindTexture(button);
        drawTexturedModalRect(x-8,y,0,0,8,8);
//        drawTexturedModalRect(guiLeft-8,guiTop,0,0,8,8);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseX >= guiLeft - 8 && mouseX < guiLeft && mouseY >= guiTop && mouseY < guiTop + 8)
        {
            if (mouseButton == 0)
            {
                tileImprovedChest.prepareSort((isShiftKeyDown() ? 1 : 0),true);
            }
            else if (mouseButton == 1)
            {
                tileImprovedChest.prepareSort((isShiftKeyDown() ? 1 : 0),false);
            }
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

    }
}
