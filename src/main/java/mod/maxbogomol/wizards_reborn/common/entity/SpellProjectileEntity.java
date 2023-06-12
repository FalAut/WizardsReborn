package mod.maxbogomol.wizards_reborn.common.entity;

import mod.maxbogomol.wizards_reborn.WizardsReborn;
import mod.maxbogomol.wizards_reborn.api.spell.Spell;
import mod.maxbogomol.wizards_reborn.client.particle.Particles;
import mod.maxbogomol.wizards_reborn.common.network.PacketHandler;
import mod.maxbogomol.wizards_reborn.common.network.SpellBurstEffectPacket;
import mod.maxbogomol.wizards_reborn.common.spell.EarthProjectileSpell;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.awt.*;
import java.util.UUID;

public class SpellProjectileEntity extends Entity {
    UUID casterId = null;
    String spellId = null;

    public SpellProjectileEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    public Entity shoot(double x, double y, double z, double vx, double vy, double vz, UUID caster, String spell) {
        setPosition(x, y, z);
        setMotion(vx, vy, vz);
        casterId = caster;
        spellId = spell;
        velocityChanged = true;
        return this;
    }

    @Override
    public void tick() {
        Vector3d motion = getMotion();
        setMotion(motion.x * 0.96, (motion.y > 0 ? motion.y * 0.96 : motion.y) - 0.03f, motion.z * 0.96);

        super.tick();

        if (!world.isRemote) {
            RayTraceResult ray = ProjectileHelper.func_234618_a_(this, (e) -> !e.isSpectator() && e.canBeCollidedWith() && !e.getUniqueID().equals(casterId));
            if (ray.getType() == RayTraceResult.Type.ENTITY) {
                onImpact(ray, ((EntityRayTraceResult)ray).getEntity());
            }
            else if (ray.getType() == RayTraceResult.Type.BLOCK) {
                onImpact(ray);
            }
        }

        Vector3d pos = getPositionVec();
        prevPosX = pos.x;
        prevPosY = pos.y;
        prevPosZ = pos.z;
        setPosition(pos.x + motion.x, pos.y + motion.y, pos.z + motion.z);

        if (world.isRemote) {
            rayEffect();
        }
    }

    protected void onImpact(RayTraceResult ray, Entity target) {
        setDead();
        if (!world.isRemote) {
            burstEffect();
        }
    }

    protected void onImpact(RayTraceResult ray) {
        setDead();
        if (!world.isRemote) {
            burstEffect();
        }
    }

    @Override
    protected void registerData() {
        //
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        casterId = compound.contains("caster") ? compound.getUniqueId("caster") : null;
        spellId = compound.contains("spell") ? compound.getString("spell") : null;
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        if (casterId != null) compound.putUniqueId("caster", casterId);
        if (spellId != null) compound.putString("spell", spellId);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public Spell getSpell() {
        return new EarthProjectileSpell("id");
    }

    public void rayEffect() {
        Vector3d motion = getMotion();
        Vector3d pos = getPositionVec();
        Vector3d norm = motion.normalize().scale(0.025f);

        Spell spell = getSpell();
        Color color = spell.getColor();
        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;

        for (int i = 0; i < 10; i ++) {
            double lerpX = MathHelper.lerp(i / 10.0f, prevPosX, pos.x);
            double lerpY = MathHelper.lerp(i / 10.0f, prevPosY, pos.y);
            double lerpZ = MathHelper.lerp(i / 10.0f, prevPosZ, pos.z);

            Particles.create(WizardsReborn.WISP_PARTICLE)
                    .addVelocity(-norm.x + ((rand.nextDouble() - 0.5D) / 500), -norm.y + ((rand.nextDouble() - 0.5D) / 500), -norm.z + ((rand.nextDouble() - 0.5D) / 500))
                    .setAlpha(0.3f, 0).setScale(0.15f, 0)
                    .setColor(r, g, b)
                    .setLifetime(20)
                    .spawn(world, lerpX, lerpY, lerpZ);

            if (rand.nextFloat() < 0.1f) {
                Particles.create(WizardsReborn.SPARKLE_PARTICLE)
                        .addVelocity(-norm.x + ((rand.nextDouble() - 0.5D) / 250), -norm.y + ((rand.nextDouble() - 0.5D) / 250), -norm.z + ((rand.nextDouble() - 0.5D) / 250))
                        .setAlpha(0.125f, 0).setScale(0.2f, 0)
                        .setColor(r, g, b)
                        .setLifetime(30)
                        .spawn(world, lerpX, lerpY, lerpZ);
            }
        }
    }

    public void burstEffect() {
        Vector3d pos = getPositionVec();

        Spell spell = getSpell();
        Color color = spell.getColor();
        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;

        PacketHandler.sendToTracking(world, new BlockPos(pos), new SpellBurstEffectPacket((float) pos.x, (float) pos.y, (float) pos.z, r, g, b));
    }
}
