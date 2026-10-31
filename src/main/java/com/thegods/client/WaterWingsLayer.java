package com.thegods.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.thegods.TheGods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class WaterWingsLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final ResourceLocation WINGS =
            new ResourceLocation(TheGods.MOD_ID, "textures/entity/water_wings.png");

    public WaterWingsLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        if (!ClientDivineData.godForm || player != Minecraft.getInstance().player) {
            return;
        }

        VertexConsumer consumer = buffer.getBuffer(RenderType.eyes(WINGS));
        float time = player.tickCount + partialTicks;

        poseStack.pushPose();
        poseStack.translate(0.0D, 1.02D, 0.22D);

        for (int side = -1; side <= 1; side += 2) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(side * 18.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(side * -6.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees((float) Math.sin(time * 0.05D) * 4.0F));

            for (int i = 0; i < 8; i++) {
                float t = i / 7.0F;

                poseStack.pushPose();
                poseStack.translate(side * (0.08F + t * 0.05F), 0.06F + t * 0.58F, 0.0F);
                poseStack.mulPose(Axis.ZP.rotationDegrees(side * (22.0F + t * 58.0F)));
                poseStack.mulPose(Axis.XP.rotationDegrees((float) Math.sin(time * 0.08D + i * 0.6D) * 7.0F));

                float width = 0.16F + t * 0.10F;
                float height = 0.95F - t * 0.42F;
                float alpha = 0.55F - t * 0.04F;

                quad(poseStack.last(), consumer, 0.0F, 0.0F, width, height, 0.0F,
                        0.0F, 0.0F, 1.0F, 1.0F, alpha);

                if (i % 2 == 0) {
                    quad(poseStack.last(), consumer, -width * 0.35F, 0.05F, width * 0.65F, height * 0.72F, 0.012F,
                            0.0F, 0.0F, 1.0F, 0.7F, alpha * 0.6F);
                }

                poseStack.popPose();
            }

            poseStack.popPose();
        }

        poseStack.popPose();
    }

    private static void quad(PoseStack.Pose pose, VertexConsumer consumer, float x0, float y0, float x1, float y1,
                             float z, float u0, float v0, float u1, float v1, float alpha) {
        consumer.vertex(pose.pose(), x0, y0, z)
                .color(0.08F, 0.45F, 0.85F, alpha)
                .uv(u0, v0)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(pose.normal(), 0.0F, 0.0F, 1.0F)
                .endVertex();
        consumer.vertex(pose.pose(), x1, y0, z)
                .color(0.08F, 0.45F, 0.85F, alpha)
                .uv(u1, v0)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(pose.normal(), 0.0F, 0.0F, 1.0F)
                .endVertex();
        consumer.vertex(pose.pose(), x1, y1, z)
                .color(0.55F, 0.92F, 1.0F, alpha)
                .uv(u1, v1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(pose.normal(), 0.0F, 0.0F, 1.0F)
                .endVertex();
        consumer.vertex(pose.pose(), x0, y1, z)
                .color(0.55F, 0.92F, 1.0F, alpha)
                .uv(u0, v1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(pose.normal(), 0.0F, 0.0F, 1.0F)
                .endVertex();
    }
}
