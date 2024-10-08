package mod.maxbogomol.wizards_reborn.common.item.equipment;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.maxbogomol.fluffy_fur.client.animation.ItemAnimation;
import mod.maxbogomol.fluffy_fur.common.item.ICustomAnimationItem;
import mod.maxbogomol.fluffy_fur.common.item.IGuiParticleItem;
import mod.maxbogomol.fluffy_fur.common.item.ItemBackedInventory;
import mod.maxbogomol.fluffy_fur.util.ColorUtil;
import mod.maxbogomol.wizards_reborn.WizardsReborn;
import mod.maxbogomol.wizards_reborn.WizardsRebornClient;
import mod.maxbogomol.wizards_reborn.api.crystal.CrystalStat;
import mod.maxbogomol.wizards_reborn.api.crystal.CrystalType;
import mod.maxbogomol.wizards_reborn.api.knowledge.KnowledgeUtil;
import mod.maxbogomol.wizards_reborn.api.skin.Skin;
import mod.maxbogomol.wizards_reborn.api.spell.Spell;
import mod.maxbogomol.wizards_reborn.api.spell.Spells;
import mod.maxbogomol.wizards_reborn.api.wissen.IWissenItem;
import mod.maxbogomol.wizards_reborn.api.wissen.WissenItemType;
import mod.maxbogomol.wizards_reborn.api.wissen.WissenItemUtil;
import mod.maxbogomol.wizards_reborn.client.config.ClientConfig;
import mod.maxbogomol.wizards_reborn.common.item.CrystalItem;
import mod.maxbogomol.wizards_reborn.util.NumericalUtil;
import mod.maxbogomol.wizards_reborn.util.RenderUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class ArcaneWandItem extends Item implements IWissenItem, ICustomAnimationItem, IGuiParticleItem {
    public ArcaneWandItem(Properties properties) {
        super(properties);
    }

    public static SimpleContainer getInventory(ItemStack stack) {
        return new ItemBackedInventory(stack, 1);
    }

    @Nonnull
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag oldCapNbt) {
        return new InvProvider(stack);
    }

    private static class InvProvider implements ICapabilityProvider {
        private final LazyOptional<IItemHandler> opt;

        public InvProvider(ItemStack stack) {
            opt = LazyOptional.of(() -> new InvWrapper(getInventory(stack)));
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
            return ForgeCapabilities.ITEM_HANDLER.orEmpty(capability, opt);
        }
    }

    @Override
    public void onDestroyed(ItemEntity pItemEntity) {
        Iterator<ItemStack> iter = new Iterator<>() {
            private int i = 0;
            private final SimpleContainer inventory = getInventory(pItemEntity.getItem());

            @Override
            public boolean hasNext() {
                return i < inventory.getContainerSize();
            }

            @Override
            public ItemStack next() {
                return inventory.getItem(i++);
            }
        };

        ItemUtils.onContainerDestroyed(pItemEntity, Stream.iterate(iter.next(), t -> iter.hasNext(), t -> iter.next()));
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        WissenItemUtil.existWissen(stack);
        return stack;
    }

    public static ItemStack existTags(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();

        if (!nbt.contains("crystal")) {
            nbt.putBoolean("crystal", false);
        }
        if (!nbt.contains("spell")) {
            nbt.putString("spell", "");
        }
        if (!nbt.contains("cooldown")) {
            nbt.putInt("cooldown", 0);
        }
        if (!nbt.contains("maxCooldown")) {
            nbt.putInt("maxCooldown", 0);
        }

        return stack;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean isSelected) {
        WissenItemUtil.existWissen(stack);
        existTags(stack);
        CompoundTag nbt = stack.getOrCreateTag();

        if (nbt.contains("cooldown")) {
            if (nbt.getInt("cooldown") > 0) {
                nbt.putInt("cooldown", nbt.getInt("cooldown") - 1);
                if (nbt.getInt("cooldown") == 0) {
                    nbt.putInt("maxCooldown", 0);
                    if (nbt.getString("spell") != "") {
                        Spell spell = Spells.getSpell(nbt.getString("spell"));
                        spell.onReload(stack, world, entity, slot, isSelected);
                    }
                }
            }
        }
    }

    @Override
    public int getMaxWissen() {
        return 10000;
    }

    @Override
    public WissenItemType getWissenItemType() {
        return WissenItemType.USING;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (!slotChanged) {
            return false;
        }
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        CompoundTag nbt = stack.getTag();
        if (canSpell(stack, player)) {
            Spell spell = Spells.getSpell(nbt.getString("spell"));
            if (spell.canSpell(world, player, hand) && spell.canSpellAir(world, player, hand)) {
                if (spell.canWandWithCrystal(stack)) {
                    spell.useSpell(world, player, hand);
                    return InteractionResultHolder.success(stack);
                }
            }
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();

        CompoundTag nbt = stack.getTag();
        if (canSpell(stack, context.getPlayer())) {
            Spell spell = Spells.getSpell(nbt.getString("spell"));
            if (spell.canWandWithCrystal(stack)) {
                return spell.onWandUseOn(stack, context);
            }
        }

        return super.onItemUseFirst(stack, context);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        CompoundTag nbt = stack.getTag();
        if (canSpell(stack, (Player) livingEntity)) {
            Spell spell = Spells.getSpell(nbt.getString("spell"));
            if (spell.canWandWithCrystal(stack)) {
                spell.onUseTick(level, livingEntity, stack, remainingUseDuration);
            }
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
        CompoundTag nbt = stack.getTag();
        if (canSpell(stack, (Player) entityLiving)) {
            Spell spell = Spells.getSpell(nbt.getString("spell"));
            if (spell.canWandWithCrystal(stack)) {
                spell.releaseUsing(stack, level, entityLiving, timeLeft);
            }
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
        CompoundTag nbt = stack.getTag();
        if (canSpell(stack, (Player) entityLiving)) {
            Spell spell = Spells.getSpell(nbt.getString("spell"));
            if (spell.canWandWithCrystal(stack)) {
                spell.finishUsingItem(stack, level, entityLiving);
            }
        }

        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        if (canSpell(stack)) {
            Spell spell = Spells.getSpell(nbt.getString("spell"));
            return spell.getUseDuration(stack);
        }

        return 72000;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        if (!ClientConfig.SPELLS_FIRST_PERSON_ITEM_ANIMATIONS.get()) {
            return UseAnim.NONE;
        }
        CompoundTag nbt = stack.getTag();
        if (canSpell(stack, WizardsReborn.proxy.getPlayer())) {
            Spell spell = Spells.getSpell(nbt.getString("spell"));
            return spell.getUseAnimation(stack);
        }
        return UseAnim.NONE;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ItemAnimation getAnimation(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        if (canSpell(stack, WizardsReborn.proxy.getPlayer())) {
            Spell spell = Spells.getSpell(nbt.getString("spell"));
            return spell.getAnimation(stack);
        }

        return null;
    }

    public boolean canSpell(ItemStack stack, Player player) {
        CompoundTag nbt = stack.getOrCreateTag();
        if (nbt.getBoolean("crystal")) {
            if (!nbt.getString("spell").isEmpty()) {
                Spell spell = Spells.getSpell(nbt.getString("spell"));
                return (KnowledgeUtil.isSpell(player, spell));
            }
        }
        return false;
    }

    public boolean canSpell(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        if (nbt.getBoolean("crystal")) {
            return !nbt.getString("spell").isEmpty();
        }
        return false;
    }

    @Override
    public Component getName(ItemStack stack) {
        Component displayName = super.getName(stack);

        CompoundTag nbt = stack.getOrCreateTag();
        if (nbt.getBoolean("crystal")) {
            if (ArcaneWandItem.getInventory(stack).getItem(0).getItem() instanceof CrystalItem crystal) {
                Component crystalName = getCrystalTranslate(crystal.getName(stack));
                return displayName.copy().append(crystalName);
            }
        }

        return displayName;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> list, TooltipFlag flags) {
        Skin skin = Skin.getSkinFromItem(stack);
        if (skin != null) list.add(skin.getSkinComponent());

        if (ClientConfig.NUMERICAL_WISSEN.get()) {
            WissenItemUtil.existWissen(stack);
            list.add(NumericalUtil.getWissenName(WissenItemUtil.getWissen(stack), getMaxWissen()).copy().withStyle(ChatFormatting.GRAY));
        }

        list.add(Component.empty());
        list.add(Component.translatable("lore.wizards_reborn.arcane_wand.crystal").withStyle(ChatFormatting.GRAY));

        CompoundTag nbt = stack.getOrCreateTag();
        if (nbt.getBoolean("crystal")) {
            if (ArcaneWandItem.getInventory(stack).getItem(0).getItem() instanceof CrystalItem crystal) {
                CrystalType type = crystal.getType();
                Color color = crystal.getType().getColor();
                for (CrystalStat stat : type.getStats()) {
                    int statlevel = crystal.getStatLevel(ArcaneWandItem.getInventory(stack).getItem(0), stat);
                    int red = (int) Mth.lerp((float) statlevel / stat.getMaxLevel(), Color.GRAY.getRed(), color.getRed());
                    int green = (int) Mth.lerp((float) statlevel / stat.getMaxLevel(), Color.GRAY.getGreen(), color.getGreen());
                    int blue = (int) Mth.lerp((float) statlevel / stat.getMaxLevel(), Color.GRAY.getBlue(), color.getBlue());

                    int packColor = ColorUtil.packColor(255, red, green, blue);
                    list.add(Component.literal(" ").append(Component.translatable(stat.getTranslatedName()).append(": " + statlevel).withStyle(Style.EMPTY.withColor(packColor))));
                }
            }
        }

        list.add(Component.translatable("lore.wizards_reborn.arcane_wand.spell").withStyle(ChatFormatting.GRAY));

        if (nbt.getString("spell") != "") {
            Spell spell = Spells.getSpell(nbt.getString("spell"));
            Color color = spell.getColor();
            int packColor = ColorUtil.packColor(255, color.getRed(), color.getGreen(), color.getBlue());
            list.add(Component.literal(" ").append(Component.translatable(spell.getTranslatedName()).withStyle(Style.EMPTY.withColor(packColor))));
        }
    }

    public static Component getCrystalTranslate(Component component) {
        Component crystal = Component.literal(" - ").append(component);
        return crystal;
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawWandGui(GuiGraphics gui) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        ItemStack main = mc.player.getMainHandItem();
        ItemStack offhand = mc.player.getOffhandItem();

        boolean isMain = false;
        boolean isOff = false;
        boolean render = false;
        if (!main.isEmpty() && main.getItem() instanceof ArcaneWandItem) {
            render = true;
            isMain = true;
        }
        if (!offhand.isEmpty() && offhand.getItem() instanceof ArcaneWandItem) {
            render = true;
            isOff = true;
        }

        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        gui.pose().pushPose();
        gui.pose().translate(0, 0, -200);

        if (render) {
            if (!player.isSpectator()) {
                boolean up = ClientConfig.ARCANE_WAND_OVERLAY_UP.get();
                boolean right = ClientConfig.ARCANE_WAND_OVERLAY_RIGHT.get();
                boolean sideHud = ClientConfig.ARCANE_WAND_OVERLAY_SIDE_HUD.get();
                boolean sideBar = ClientConfig.ARCANE_WAND_OVERLAY_SIDE_BAR.get();
                boolean horizontalBar = ClientConfig.ARCANE_WAND_OVERLAY_HORIZONTAL_BAR.get();

                int xOff = ClientConfig.ARCANE_WAND_OVERLAY_X_OFFSET.get();
                int yOff = ClientConfig.ARCANE_WAND_OVERLAY_Y_OFFSET.get();
                int xTwoOff = ClientConfig.ARCANE_WAND_OVERLAY_SECOND_X_OFFSET.get();
                int yTwoOff = ClientConfig.ARCANE_WAND_OVERLAY_SECOND_Y_OFFSET.get();
                int xBarOff = ClientConfig.ARCANE_WAND_OVERLAY_BAR_X_OFFSET.get();
                int yBarOff = ClientConfig.ARCANE_WAND_OVERLAY_BAR_Y_OFFSET.get();

                boolean twoHudFree = ClientConfig.ARCANE_WAND_OVERLAY_SECOND_HUD_FREE.get();
                boolean barFree = ClientConfig.ARCANE_WAND_OVERLAY_BAR_FREE.get();

                boolean drawCooldown = ClientConfig.ARCANE_WAND_OVERLAY_COOLDOWN_TEXT.get();
                boolean drawWissen = ClientConfig.ARCANE_WAND_OVERLAY_WISSEN_TEXT.get();
                boolean reverseBar = ClientConfig.ARCANE_WAND_OVERLAY_REVERSE_BAR.get();
                boolean showEmpty = ClientConfig.ARCANE_WAND_OVERLAY_SHOW_EMPTY.get();

                boolean rightBar = right;
                if (up && horizontalBar) {
                    rightBar = false;
                }
                if (!up && horizontalBar) {
                    rightBar = true;
                }

                List<Spell> spells = getSpellSet(showEmpty, reverseBar);
                int spellsOffset = (spells.size() * 18);

                int x = 1;
                int y = 1;

                int width = gui.guiWidth();
                int height = gui.guiHeight();

                int xOffset = xOff;
                int yOffset = yOff;

                if (!up) {
                    y = height - 43;
                }

                if (right) {
                    x = width - 53;
                }

                if (isMain) {
                    drawWandHUD(gui, x + xOffset, y + yOffset, main, drawCooldown, drawWissen);
                    if (sideHud) {
                        xOffset = xOffset + (right ? -54 : 54);
                    } else {
                        yOffset = yOffset - (up ? -43 : 43);
                    }
                    if (isOff) {
                        if (twoHudFree) {
                            xOffset = 0;
                            yOffset = 0;
                        }
                        xOffset = xOffset + xTwoOff;
                        yOffset = yOffset + yTwoOff;
                    }
                }
                if (isOff) {
                    drawWandHUD(gui, x + xOffset, y + yOffset, offhand, drawCooldown, drawWissen);
                    if (sideHud) {
                        xOffset = xOffset + (right ? -54 : 54);
                    } else {
                        yOffset = yOffset - (up ? -43 : 43);
                    }
                }

                if (!sideBar && sideHud) {
                    xOffset = xOff;
                    yOffset = yOff;
                    yOffset = yOffset - (up ? -43 : 43);
                }
                if (sideBar && !sideHud) {
                    xOffset = xOff;
                    yOffset = yOff;
                    xOffset = xOffset + (right ? -54 : 54);
                }

                if (barFree) {
                    xOffset = 0;
                    yOffset = 0;
                }

                if (up) {
                    if (horizontalBar && right) {
                       xOffset = xOffset - spellsOffset + 4;
                    }
                } else {
                    if (horizontalBar) {
                        if (right) xOffset = xOffset - spellsOffset + 4;
                        yOffset = yOffset + 25;
                    } else {
                        yOffset = yOffset - spellsOffset + 28;
                    }
                }

                if (right) {
                    xOffset = xOffset + 34;
                }

                xOffset = xOffset + xBarOff;
                yOffset = yOffset + yBarOff;

                drawBar(gui, x + xOffset, y + yOffset, horizontalBar, rightBar, showEmpty, reverseBar);
            }
        }

        gui.pose().popPose();

        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawWandHUD(GuiGraphics gui, int x, int y, ItemStack stack, boolean drawCooldown, boolean drawWissen) {
        if (stack.getItem() instanceof ArcaneWandItem wand) {
            CompoundTag nbt = stack.getOrCreateTag();
            existTags(stack);
            WissenItemUtil.existWissen(stack);
            Spell spell = null;

            if (nbt.contains("spell")) {
                if (nbt.getString("spell") != "") {
                    spell = Spells.getSpell(nbt.getString("spell"));
                }
            }

            int cooldown = nbt.getInt("cooldown");
            int maxCooldown = nbt.getInt("maxCooldown");
            int wissen = WissenItemUtil.getWissen(stack);
            int maxWissen = wand.getMaxWissen();


            gui.blit(new ResourceLocation(WizardsReborn.MOD_ID + ":textures/gui/arcane_wand_frame.png"), x, y, 0, 0, 52, 18, 64, 64);
            gui.blit(new ResourceLocation(WizardsReborn.MOD_ID + ":textures/gui/cooldown_frame.png"), x + 2, y + 19, 0, 0, 48, 10, 64, 64);
            gui.blit(new ResourceLocation(WizardsReborn.MOD_ID + ":textures/gui/wissen_frame.png"), x + 2, y + 30, 0, 0, 48, 10, 64, 64);

            int width = 32;
            if (spell != null && cooldown > 0) {
                width /= (double) maxCooldown / (double) cooldown;
            } else {
                width = -32;
            }
            gui.blit(new ResourceLocation(WizardsReborn.MOD_ID + ":textures/gui/cooldown_frame.png"), x + 10, y + 20, 0, 10, 32 - width, 8, 64, 64);

            width = 32;
            width /= (double) maxWissen / (double) wissen;
            gui.blit(new ResourceLocation(WizardsReborn.MOD_ID + ":textures/gui/wissen_frame.png"), x + 10, y + 31, 0, 10, width, 8, 64, 64);

            if (nbt.getBoolean("crystal")) {
                SimpleContainer stack_inv = ArcaneWandItem.getInventory(stack);
                gui.renderItem(stack_inv.getItem(0), x + 8, y);
            }

            if (spell != null) {
                if (KnowledgeUtil.isSpell(Minecraft.getInstance().player, spell)) {
                    gui.blit(spell.getIcon(), x + 28, y + 1, 0, 0, 16, 16, 16, 16);
                    if (!spell.canWandWithCrystal(stack)) {
                        gui.blit(new ResourceLocation(WizardsReborn.MOD_ID + ":textures/gui/arcane_wand_frame.png"), x + 27, y, 0, 18, 18, 18, 64, 64);
                    }
                } else {
                    gui.blit(new ResourceLocation(WizardsReborn.MOD_ID, "textures/gui/arcanemicon/unknown.png"), x + 28, y + 1, 0, 0, 16, 16, 16, 16);
                }
            }

            Font font_renderer = Minecraft.getInstance().font;
            if (drawCooldown) {
                String textCooldown = Integer.toString(cooldown);
                int cooldownStringWidth = font_renderer.width(textCooldown);

                gui.drawString(Minecraft.getInstance().font, textCooldown, x + 26 - (cooldownStringWidth / 2), y + 20, 0xffffff);
            }

            if (drawWissen) {
                String textWissen = Integer.toString(wissen);
                int wissenStringWidth = font_renderer.width(textWissen);

                gui.drawString(Minecraft.getInstance().font, textWissen, x + 26 - (wissenStringWidth / 2), y + 31, 0xffffff);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawBar(GuiGraphics gui, int x, int y, boolean horizontal, boolean right, boolean showEmpty, boolean reverse) {
        Minecraft mc = Minecraft.getInstance();
        int currentSpellInSet = getCurrentSpellInSet(showEmpty, reverse);

        int x1 = 18;
        int y1 = 18;
        int x2 = 18;
        int y2 = 24;
        int x3 = 42;
        int y3 = 18;

        int u1 = 16;
        int v1 = 6;
        int u3 = 8;
        int v3 = 16;

        int xOffset = 0;
        int yOffset = 1;

        int xo = 1;
        int yo = 0;

        if (horizontal) {
            x1 = 20;
            y1 = 30;
            x2 = 26;
            y2 = 30;
            x3 = 34;
            y3 = 42;

            u1 = 6;
            v1 = 16;
            u3 = 16;
            v3 = 8;

            xOffset = -1;
            yOffset = 0;

            xo = 1;
            yo = 0;

            if (right) {
                x3 = 34;
                y3 = 34;
            }
        } else if (right) {
            x3 = 34;
            y3 = 18;
        }

        gui.blit(new ResourceLocation(WizardsReborn.MOD_ID + ":textures/gui/arcane_wand_frame.png"), x + xOffset + xo, y + yOffset + yo, x1, y1, u1, v1, 64, 64);

        if (horizontal) {
            xOffset = xOffset + 7;
        } else {
            yOffset = yOffset + 7;
        }

        List<Spell> spells = getSpellSet(showEmpty, reverse);

        int i = 0;
        for (Spell spellI : spells) {
            ResourceLocation resource = new ResourceLocation(WizardsReborn.MOD_ID, "textures/gui/arcanemicon/research.png");
            if (spellI != null) {
                if (!KnowledgeUtil.isSpell(mc.player, spellI)) {
                    resource = new ResourceLocation(WizardsReborn.MOD_ID, "textures/gui/arcanemicon/unknown.png");
                } else {
                    resource = spellI.getIcon();
                }
            }

            if (!(KnowledgeUtil.isSpell(Minecraft.getInstance().player, spellI)) && spellI != null) {
                resource = new ResourceLocation(WizardsReborn.MOD_ID, "textures/gui/arcanemicon/unknown.png");
            }

            int xof = 0;
            int yof = 0;
            int xc = 0;
            int yc = 0;
            if (currentSpellInSet == i) {
                if (horizontal) {
                    if (right) {
                        yof = -8;
                        yc = 7;
                    } else {
                        yof = 8;
                    }
                } else {
                    if (right) {
                        xof = -8;
                        xc = 8;
                    } else {
                        xof = 8;
                    }
                }
                gui.blit(new ResourceLocation(WizardsReborn.MOD_ID + ":textures/gui/arcane_wand_frame.png"), x + xOffset + xo + xc, y + yOffset + yo + yc, x3, y3, u3, v3, 64, 64);
            }

            gui.blit(resource, x + 1 + xOffset + xof, y + yOffset + yof, 0, 0, 16, 16, 16, 16);
            if (horizontal) {
                xOffset = xOffset + 18;
            } else {
                yOffset = yOffset + 18;
            }
            i++;
        }

        if (horizontal) {
            xOffset = xOffset - 1;
        } else {
            yOffset = yOffset - 1;
        }

        gui.blit(new ResourceLocation(WizardsReborn.MOD_ID + ":textures/gui/arcane_wand_frame.png"), x + xOffset + xo, y + yOffset + yo, x2, y2, u1, v1, 64, 64);
    }

    @OnlyIn(Dist.CLIENT)
    public static List<Spell> getSpellSet(boolean showEmpty, boolean reverse) {
        Minecraft mc = Minecraft.getInstance();

        int currentSpellSet = KnowledgeUtil.getCurrentSpellSet(mc.player);
        int currentSpellInSet = KnowledgeUtil.getCurrentSpellInSet(mc.player);

        List<Spell> spells = KnowledgeUtil.getSpellSet(mc.player, currentSpellSet);
        List<Spell> spellSet = new ArrayList<>();

        int ii = 0;
        if (reverse) ii = 9;
        for (int i = 0; i < 10; i++) {
            Spell spell = spells.get(ii);
            boolean add = showEmpty || spell != null || currentSpellInSet == ii;
            if (add) spellSet.add(spell);
            if (reverse) {
                ii--;
            } else {
                ii++;
            }
        }

        return spellSet;
    }

    @OnlyIn(Dist.CLIENT)
    public static int getCurrentSpellInSet(boolean showEmpty, boolean reverse) {
        Minecraft mc = Minecraft.getInstance();

        int currentSpellSet = KnowledgeUtil.getCurrentSpellSet(mc.player);
        int currentSpellInSet = KnowledgeUtil.getCurrentSpellInSet(mc.player);

        List<Spell> spells = KnowledgeUtil.getSpellSet(mc.player, currentSpellSet);

        int ii = 0;
        if (reverse) ii = 9;
        for (int i = 0; i < 10; i++) {
            Spell spell = spells.get(ii);
            boolean add = (!showEmpty && spell == null) || currentSpellInSet == ii;
            if (add) return i;
            if (reverse) {
                ii--;
            } else {
                ii++;
            }
        }

        return currentSpellInSet;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderParticle(PoseStack pose, LivingEntity entity, Level level, ItemStack stack, int x, int y, int seed, int guiOffset) {
        SimpleContainer inv = ArcaneWandItem.getInventory(stack);

        if (inv.getItem(0).getItem() instanceof CrystalItem crystal) {
            if (crystal.getPolishing().getPolishingLevel() > 0) {
                int polishingLevel = crystal.getPolishing().getPolishingLevel();
                if (polishingLevel > 4) {
                    polishingLevel = 4;
                }
                Color color = crystal.getType().getColor();
                int seedI = this.getDescriptionId().length();

                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                MultiBufferSource.BufferSource buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
                RenderSystem.depthMask(false);
                RenderSystem.setShader(WizardsRebornClient::getGlowingShader);
                RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

                pose.pushPose();
                pose.translate(x, y, 100);
                RenderSystem.setShaderColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 0.75F * (polishingLevel / 4f));
                RenderUtils.dragon(pose, buffersource, 12, 4, 0, 7f, Minecraft.getInstance().getPartialTick(), 1, 1, 1, seedI);
                buffersource.endBatch();
                pose.popPose();

                if (crystal.getPolishing().hasParticle()) {
                    Color polishingColor = crystal.getPolishing().getColor();
                    pose.pushPose();
                    pose.translate(x, y, 100);
                    RenderSystem.setShaderColor(polishingColor.getRed() / 255f, polishingColor.getGreen() / 255f, polishingColor.getBlue() / 255f, 0.5F);
                    RenderUtils.dragon(pose, buffersource, 12, 4, 0, 6f, Minecraft.getInstance().getPartialTick(), 1, 1, 1, seedI + 1f);
                    buffersource.endBatch();
                    pose.popPose();
                }

                RenderSystem.disableBlend();
                RenderSystem.depthMask(true);
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            }
        }
    }
}
