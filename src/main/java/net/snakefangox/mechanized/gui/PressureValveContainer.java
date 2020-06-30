package net.snakefangox.mechanized.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.Mechanized;
import net.snakefangox.mechanized.networking.PacketIdentifiers;

public class PressureValveContainer extends SyncedGuiDescription {

	public static final String numbers = "0123456789";
	WTextField pressure;
	BlockPos ventPos;

	public PressureValveContainer(int syncID, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(MRegister.PRESSURE_VALVE_CONTAINER, syncID, playerInventory, getBlockInventory(context),
				getBlockPropertyDelegate(context));
		ventPos = context.run((world, pos) -> {
			return pos;
		}).orElse(null);
		WGridPanel root = new WGridPanel();
		setRootPanel(root);

		WLabel ventText = new WLabel(new TranslatableText(Mechanized.MODID + ".pressure_valve_text"));
		WLabel psbText = new WLabel(new LiteralText("PSB"));
		psbText.setHorizontalAlignment(HorizontalAlignment.CENTER);
		Text text = new LiteralText(String.valueOf(getPropertyDelegate().get(0)));
		pressure = new WTextField(text) {
			@Override
			@Environment(EnvType.CLIENT)
			public void onCharTyped(char ch) {
				if (numbers.contains(String.valueOf(ch))) {
					super.onCharTyped(ch);
				}
			}
		};

		root.add(ventText, 0, 0, 3, 1);
		root.add(pressure, 0, 1, 3, 1);
		root.add(psbText, 4, 1);

		root.validate(this);

	}

	@Override
	public void close(PlayerEntity player) {
		super.close(player);
		try {
			int value = Integer.valueOf(pressure.getText());
			if (value >= 0 && value <= 100 && ventPos != null) {
				PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
				passedData.writeBlockPos(ventPos);
				passedData.writeByte(value);
				ClientSidePacketRegistry.INSTANCE.sendToServer(PacketIdentifiers.SYNC_VALVE_PRESSURE, passedData);
			}
		} catch (NumberFormatException e) {
			// Oh well, not sure what happened but we'll just chuck it out
		}
	}

	public static class PressureValveScreen extends CottonInventoryScreen<PressureValveContainer> {
		public PressureValveScreen(PressureValveContainer container, PlayerInventory player, Text text) {
			super(container, player.player);
		}
	}
}
