package net.snakefangox.mechanized.steam;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import blue.endless.jankson.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.PersistentState;
import net.snakefangox.mechanized.Mechanized;

public class SteamPipeNetworkStorage extends PersistentState {

	public static final String KEY = Mechanized.MODID + "pipe_network";

	List<PipeNetwork> pipe_networks = new ArrayList<PipeNetwork>();

	public SteamPipeNetworkStorage() {
		super(KEY);
	}

	public int createNewPipeNetwork(BlockPos pos) {
		PipeNetwork pn = new PipeNetwork();
		pn.addPipe(pos, this);
		int index = pipe_networks.size();
		// Shouldn't actually need the index here but better safe then sorry
		pipe_networks.add(index, pn);
		return index;
	}

	public void removeFromPipeNetwork(int id, BlockPos pos) {
		PipeNetwork pn = getPipeNetwork(id);
		if (pn != null) {
			pn.removePipe(pos, this);
			if(pn.isEmpty())
				pipe_networks.remove(id);
		}
	}

	@Nullable
	public PipeNetwork getPipeNetwork(int id) {
		if (id >= 0 && id < pipe_networks.size())
			return pipe_networks.get(id);
		return null;
	}

	public void addToPipeNetwork(int id, BlockPos pos) {
		PipeNetwork pn = getPipeNetwork(id);
		if (pn != null)
			pn.addPipe(pos, this);
	}

	public void mergePipeNetworks(int id1, int id2) {
		PipeNetwork pn1 = getPipeNetwork(id1);
		PipeNetwork pn2 = getPipeNetwork(id2);
		if (pn1 != null && pn2 != null) {
			pn1.appendNetwork(pn2.getNetwork());
			pn1.addSteam(null, pn2.getSteamAmount(null));
			pipe_networks.remove(id2);
			markDirty();
		}
	}

	public void splitPipeNetworks(int id, BlockPos splitPoint) {
		PipeNetwork pn = getPipeNetwork(id);
		if (pn != null) {
			
		}
	}

	public static SteamPipeNetworkStorage getInstance(ServerWorld world) {
		return world.getPersistentStateManager().getOrCreate(() -> new SteamPipeNetworkStorage(), KEY);
	}

	@Override
	public void fromTag(CompoundTag tag) {
		for (String index : tag.getKeys()) {
			PipeNetwork network = new PipeNetwork();
			network.fromTag(tag.getCompound(index));
			pipe_networks.add(Integer.valueOf(index), network);
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		for (int i = 0; i < pipe_networks.size(); i++) {
			PipeNetwork pn = pipe_networks.get(i);
			if (pn != null)
				tag.put(String.valueOf(i), pn.toTag(new CompoundTag()));
		}
		return tag;
	}

	public static class PipeNetwork implements Steam {

		private Set<BlockPos> network = new HashSet<BlockPos>();
		int steamAmount = 0;

		public static final int STEAM_CAPACITY = Steam.UNIT;

		public void addPipe(BlockPos pos, SteamPipeNetworkStorage instance) {
			network.add(pos);
			instance.markDirty();
		}

		public void removePipe(BlockPos pos, SteamPipeNetworkStorage instance) {
			network.remove(pos);
			instance.markDirty();
		}
		
		public boolean isEmpty() {
			return network.isEmpty();
		}
		
		public Set<BlockPos> getNetwork() {
			return network;
		}

		public void appendNetwork(Set<BlockPos> network) {
			this.network.addAll(network);
		}

		public void fromTag(CompoundTag tag) {
			CompoundTag setTag = tag.getCompound("set");
			for (String index : setTag.getKeys()) {
				network.add(BlockPos.fromLong(setTag.getLong(index)));
			}
			steamAmount = tag.getInt("steamAmount");
		}

		public CompoundTag toTag(CompoundTag tag) {
			CompoundTag setTag = new CompoundTag();
			Iterator<BlockPos> iterator = network.iterator();
			int i = 0;
			while (iterator.hasNext()) {
				setTag.putLong(String.valueOf(i++), iterator.next().asLong());
			}
			tag.put("set", setTag);
			tag.putInt("steamAmount", steamAmount);
			return tag;
		}

		@Override
		public int getSteamAmount(Direction dir) {
			return steamAmount;
		}

		@Override
		public void setSteamAmount(Direction dir, int amount) {
			steamAmount = amount;
		}

		@Override
		public int getMaxSteamAmount(Direction dir) {
			return STEAM_CAPACITY;
		}
	}

	public static interface NetworkMember {
		public void recheckForNetwork();
	}
}
