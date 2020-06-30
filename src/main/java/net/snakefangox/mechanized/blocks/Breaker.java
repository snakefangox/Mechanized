package net.snakefangox.mechanized.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.gui.AlloyFurnaceContainer;

public class Breaker extends Block implements BlockEntityProvider {

	public static final BooleanProperty EXTENDED = BooleanProperty.of("extended");

	private VoxelShape BOX_N = VoxelShapes.cuboid(0, 0, 0.3, 1, 1, 1);
	private VoxelShape BOX_E;
	private VoxelShape BOX_S;
	private VoxelShape BOX_W;
	private VoxelShape BOX_U;
	private VoxelShape BOX_D;

	public Breaker(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(EXTENDED, false));
		Box box = BOX_N.getBoundingBox();
		BOX_E = VoxelShapes.cuboid(1F - box.minZ, box.minY, box.minX, 1F - box.maxZ, box.maxY, box.maxX);
		BOX_S = VoxelShapes.cuboid(box.minX, box.minY, 1F - box.minZ, box.maxX, box.maxY, 1F - box.maxZ);
		BOX_W = VoxelShapes.cuboid(box.minZ, box.minY, box.minX, box.maxZ, box.maxY, box.maxX);
		BOX_U = VoxelShapes.cuboid(box.minX, 1F - box.minZ, box.minY, box.maxX, 1F - box.maxZ, box.maxY);
		BOX_D = VoxelShapes.cuboid(box.minX, box.minZ, box.minY, box.maxX, box.maxZ, box.maxY);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		switch (state.get(Properties.FACING)) {
			case EAST:
				return BOX_E;

			case NORTH:
				return BOX_N;

			case SOUTH:
				return BOX_S;

			case WEST:
				return BOX_W;

			case UP:
				return BOX_U;

			case DOWN:
				return BOX_D;

			default:
				return BOX_N;

		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(Properties.FACING, ctx.getSide().getOpposite());
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(Properties.FACING, EXTENDED);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return MRegister.BREAKER_ENTITY.instantiate();
	}

}
