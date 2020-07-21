package net.snakefangox.mechanized.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.items.Upgradable;
import net.snakefangox.mechanized.networking.PacketIdentifiers;
import net.snakefangox.mechanized.parts.StandardInventory;
import net.snakefangox.mechanized.steam.SteamItem;

import java.awt.event.ContainerListener;

public class UpgradeTableContainer extends SyncedGuiDescription {
	public static final ItemStack CLOSED = new ItemStack(Items.BARRIER);
	public static final int INV_OFFSET = 0;
	static {
		CLOSED.setCustomName(new LiteralText("Closed"));
	}

	PlayerEntity player;
	WItemSlot upgradeSlots[];

	public UpgradeTableContainer(int syncID, PlayerInventory playerInventory) {
		super(MRegister.UPGRADE_TABLE_CONTAINER, syncID, playerInventory, new UpgradeTableInv(), new ArrayPropertyDelegate(0));
		player = playerInventory.player;
		WGridPanel root = new WGridPanel();
		setRootPanel(root);

		for (int i = 1; i < 6; i++) {
			blockInventory.setStack(i, CLOSED);
		}

		WItemSlot mainSlot = WItemSlot.outputOf(blockInventory, 0);
		upgradeSlots = new WItemSlot[5];

		root.add(mainSlot, 4, 0);

		for (int i = 0; i < upgradeSlots.length; i++) {
			upgradeSlots[i] = WItemSlot.of(blockInventory, i + 1);
			upgradeSlots[i].setModifiable(false);
			root.add(upgradeSlots[i], 2 * i, 2);
		}

		root.add(createPlayerInventoryPanel(), 0, 4);

		addListener(new UpgradeListener());

		root.validate(this);

	}

	public void changeUpgradeItem(Upgradable up, ItemStack stack) {
		if (up != null) {
			Item[] upgrades = up.getItemsFromStack(stack);
			for (int i = 0; i < up.upgradeSlotCount(stack.getItem()); i++) {
				blockInventory.setStack(i + 1, upgrades[i] == null ? ItemStack.EMPTY : new ItemStack(upgrades[i]));
				if (!player.world.isClient)
					ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, new ScreenHandlerSlotUpdateS2CPacket(syncId,
							i + INV_OFFSET + 1, blockInventory.getStack(i + 1)));
				upgradeSlots[i].setModifiable(true);
			}
		} else {
			for (int i = 1; i < blockInventory.size(); i++) {
				blockInventory.setStack(i, CLOSED);
				upgradeSlots[i - 1].setModifiable(false);
				if (!player.world.isClient)
					ServerSidePacketRegistry.INSTANCE.sendToPlayer(player,
							new ScreenHandlerSlotUpdateS2CPacket(syncId, i + INV_OFFSET, blockInventory.getStack(i)));
			}
		}
	}

	public void getAndApplyUpgrades(ItemStack stack) {
		Upgradable up = (Upgradable) stack.getItem();
		Item[] upgradeList = new Item[up.upgradeSlotCount(stack.getItem())];
		for (int i = 0; i < up.upgradeSlotCount(stack.getItem()); i++) {
			upgradeList[i] = blockInventory.getStack(i + 1).getItem();
		}
		up.getUpgradeTag(stack, upgradeList);
	}

	public void updateSlot(int slotID, ItemStack stack) {
		if (!player.world.isClient) {
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(player,
					new ScreenHandlerSlotUpdateS2CPacket(syncId, slotID, stack));
			PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
			passedData.writeItemStack(playerInventory.getCursorStack());
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, PacketIdentifiers.SYNC_CURSER_STACK, passedData);
		}
	}

	@Override
	public void close(PlayerEntity player) {
		super.close(player);
		if (!blockInventory.getStack(0).isEmpty())
			player.dropItem(blockInventory.getStack(0), false);
	}

	public static class UpgradeListener implements ScreenHandlerListener {

		@Override
		public void onHandlerRegistered(ScreenHandler container, DefaultedList<ItemStack> defaultedList) {
		}

		@Override
		public void onSlotUpdate(ScreenHandler container, int slotId, ItemStack itemStack) {
			UpgradeTableContainer con = (UpgradeTableContainer) container;
			if (slotId == INV_OFFSET) {
				if (itemStack.getItem() instanceof Upgradable) {
					con.changeUpgradeItem((Upgradable) itemStack.getItem(), itemStack);
				} else if (itemStack.isEmpty()) {
					con.changeUpgradeItem(null, itemStack);
				}
			} else if (slotId > INV_OFFSET && slotId - INV_OFFSET < 6
					&& con.blockInventory.getStack(0).getItem() instanceof Upgradable) {
				con.getAndApplyUpgrades(con.blockInventory.getStack(0));
			}
			con.updateSlot(slotId, itemStack);
		}

		@Override
		public void onPropertyUpdate(ScreenHandler handler, int property, int value) {

		}
	}

	public static class UpgradeTableInv implements StandardInventory {
		DefaultedList<ItemStack> list = DefaultedList.ofSize(6, ItemStack.EMPTY);

		@Override
		public DefaultedList<ItemStack> getItems() {
			return list;
		}

		@Override
		public boolean isValid(int slot, ItemStack stack) {
			if (slot == 0) {
				if (stack.getItem() instanceof Upgradable) {
					return true;
				} else {
					return false;
				}
			} else if (getStack(0).getItem() instanceof Upgradable && !getStack(0).isEmpty()) {
				return ((Upgradable) getStack(0).getItem()).validUpgrades(getStack(0).getItem()).test(stack);
			}
			return true;
		}

		@Override
		public int getMaxCountPerStack() {
			return 1;
		}
	}

	public static class UpgradeTableScreen extends CottonInventoryScreen<UpgradeTableContainer> {
		public UpgradeTableScreen(UpgradeTableContainer container, PlayerInventory player, Text text) {
			super(container, player.player);
		}
	}

}
