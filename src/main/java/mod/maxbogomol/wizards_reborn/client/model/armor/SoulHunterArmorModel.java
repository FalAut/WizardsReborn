package mod.maxbogomol.wizards_reborn.client.model.armor;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class SoulHunterArmorModel extends ArmorModel {
    public SoulHunterArmorModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(new CubeDeformation(0), 0);
        PartDefinition root = createHumanoidAlias(mesh);

        PartDefinition body = root.getChild("body");
        PartDefinition pelvis = root.getChild("pelvis");
        PartDefinition right_arm = root.getChild("right_arm");
        PartDefinition left_arm = root.getChild("left_arm");
        PartDefinition right_legging = root.getChild("right_legging");
        PartDefinition left_legging = root.getChild("left_legging");
        PartDefinition right_foot = root.getChild("right_foot");
        PartDefinition left_foot = root.getChild("left_foot");
        PartDefinition head = root.getChild("head");

        PartDefinition head_armor = head.addOrReplaceChild("head_armor", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.02F)), PartPose.ZERO);
        PartDefinition head_layer_armor = head_armor.addOrReplaceChild("head_layer_armor", CubeListBuilder.create().texOffs(0, 16)
                .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.22F)), PartPose.ZERO);
        PartDefinition right_ear_armor = head.addOrReplaceChild("right_ear_armor", CubeListBuilder.create().texOffs(0, 32)
                .addBox(0, 1F, 0, 1.0F, 3.0F, 7.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-3.85F, -4F, -2.5F,0.35f, -0.5f, 0.1f));
        PartDefinition left_ear_armor = head.addOrReplaceChild("left_ear_armor", CubeListBuilder.create().texOffs(16, 32)
                .addBox(0, 1F, 0, 1.0F, 3.0F, 7.0F, new CubeDeformation(0.25F)),  PartPose.offsetAndRotation(2.85F, -4F, -2.5F,0.35f, 0.5f, -0.1f));

        PartDefinition body_armor = body.addOrReplaceChild("body_armor", CubeListBuilder.create().texOffs(32, 0)
                .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.85F)), PartPose.ZERO);
        PartDefinition body_layer_armor = body_armor.addOrReplaceChild("body_layer_armor", CubeListBuilder.create().texOffs(32, 16)
                .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.1F)), PartPose.ZERO);

        PartDefinition right_arm_armor = right_arm.addOrReplaceChild("right_arm_armor", CubeListBuilder.create().texOffs(56, 0)
                .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.52F)), PartPose.ZERO);
        PartDefinition right_arm_layer_armor = right_arm_armor.addOrReplaceChild("right_arm_layer_armor", CubeListBuilder.create().texOffs(72, 0)
                .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.72F)), PartPose.ZERO);

        PartDefinition left_arm_armor = left_arm.addOrReplaceChild("left_arm_armor", CubeListBuilder.create().texOffs(56, 10)
                .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.52F)), PartPose.ZERO);
        PartDefinition left_arm_layer_armor = left_arm_armor.addOrReplaceChild("left_arm_layer_armor", CubeListBuilder.create().texOffs(72, 10)
                .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.72F)), PartPose.ZERO);

        PartDefinition right_legging_armor = right_legging.addOrReplaceChild("right_legging_armor", CubeListBuilder.create().texOffs(32, 32)
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.75F)), PartPose.ZERO);
        PartDefinition right_legging_layer_armor = right_legging_armor.addOrReplaceChild("right_legging_layer_armor", CubeListBuilder.create().texOffs(48, 32)
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.02F)), PartPose.ZERO);

        PartDefinition left_legging_armor = left_legging.addOrReplaceChild("left_legging_armor", CubeListBuilder.create().texOffs(32, 48)
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.75F)), PartPose.ZERO);
        PartDefinition left_legging_layer_armor = left_legging_armor.addOrReplaceChild("left_legging_layer_armor", CubeListBuilder.create().texOffs(48, 48)
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.02F)), PartPose.ZERO);

        PartDefinition right_foot_armor = right_foot.addOrReplaceChild("right_foot_armor", CubeListBuilder.create().texOffs(64, 20)
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.02F)), PartPose.ZERO);
        PartDefinition left_foot_armor = left_foot.addOrReplaceChild("left_foot_armor", CubeListBuilder.create().texOffs(64, 36)
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.02F)), PartPose.ZERO);

        PartDefinition codpiece = pelvis.addOrReplaceChild("codpiece", CubeListBuilder.create().texOffs(96, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 5.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 7.0F, 0.0F));

        return LayerDefinition.create(mesh, 128, 64);
    }
}
