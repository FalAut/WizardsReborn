package mod.maxbogomol.wizards_reborn.client.arcanemicon;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.maxbogomol.wizards_reborn.WizardsReborn;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ArcaneWorkbenchPage extends Page {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(WizardsReborn.MOD_ID, "textures/gui/arcanemicon_arcane_workbench_page.png");
    public ItemStack result;
    public ItemStack[] inputs;

    public ArcaneWorkbenchPage(ItemStack result, ItemStack... inputs) {
        super(BACKGROUND);
        this.result = result;
        this.inputs = inputs;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(ArcanemiconGui book, GuiGraphics gui, int x, int y, int mouseX, int mouseY) {
        for (int i = 0; i < 3; i ++) {
            for (int j = 0; j < 3; j ++) {
                int index = i * 3 + j;
                if (index < inputs.length && !inputs[index].isEmpty())
                    drawItem(book, gui, inputs[index], x + 38 + j * 18, y + 38 + i * 18, mouseX, mouseY);
            }
        }
        drawItem(book, gui, inputs[9], x + 56, y + 16, mouseX, mouseY);
        drawItem(book, gui, inputs[10], x + 96, y + 56, mouseX, mouseY);
        drawItem(book, gui, inputs[11], x + 56, y + 96, mouseX, mouseY);
        drawItem(book, gui, inputs[12], x + 16, y + 56, mouseX, mouseY);
        drawItem(book, gui, result,x + 56, y + 128, mouseX, mouseY);
    }
}