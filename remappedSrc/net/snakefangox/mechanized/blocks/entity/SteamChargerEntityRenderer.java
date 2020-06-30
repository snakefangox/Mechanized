package net.snakefangox.mechanized.blocks.entity;

import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;

public class SteamChargerEntityRenderer extends BlockEntityRenderer<SteamChargerEntity> {

	public SteamChargerEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(SteamChargerEntity blockEntity, float tickDelta, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light, int overlay) {
		Direction facing = blockEntity.getCachedState().get(HorizontalFacingBlock.FACING);
		matrices.push();
		matrices.translate(0.5, 0.275, 0.5);
		matrices.translate(facing.getOffsetX() * -0.2, facing.getOffsetY() * -0.2, facing.getOffsetZ() * -0.2);
		matrices.scale(0.75f, 0.8f, 0.75f);
		if(facing == Direction.WEST || facing == Direction.EAST)
			matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180));
		matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(facing.asRotation()));
		matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90));
		MinecraftClient.getInstance().getItemRenderer().renderItem(blockEntity.getInvStack(0), ModelTransformation.Mode.FIXED, light, overlay,
				matrices, vertexConsumers);
		matrices.pop();
	}

}
