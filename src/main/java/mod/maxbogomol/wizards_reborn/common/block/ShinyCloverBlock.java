package mod.maxbogomol.wizards_reborn.common.block;

import mod.maxbogomol.fluffy_fur.client.particle.ParticleBuilder;
import mod.maxbogomol.fluffy_fur.client.particle.data.ColorParticleData;
import mod.maxbogomol.fluffy_fur.client.particle.data.GenericParticleData;
import mod.maxbogomol.fluffy_fur.registry.client.FluffyFurParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class ShinyCloverBlock extends BushBlock {
    private static final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 3, 13);

    public ShinyCloverBlock(Properties builder) {
        super(builder);
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (random.nextFloat() < 0.1f) {
            double X = (random.nextDouble() - 0.5D) * 0.75f;
            double Z = (random.nextDouble() - 0.5D) * 0.75f;
            ParticleBuilder.create(FluffyFurParticles.SPARKLE)
                    .setColorData(ColorParticleData.create(0.427f, 0.612f, 0.423f, 0.968f, 0.941f, 0.549f).build())
                    .setTransparencyData(GenericParticleData.create(0.5f, 0).build())
                    .setScaleData(GenericParticleData.create(0.1f, 0).build())
                    .setLifetime(10)
                    .addVelocity(-(X / 100), (random.nextDouble() / 20), -(Z / 100))
                    .spawn(world, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
        }
    }
}
