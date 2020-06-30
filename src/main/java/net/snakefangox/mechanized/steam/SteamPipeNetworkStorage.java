package net.snakefangox.mechanized.steam;

import blue.endless.jankson.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import net.snakefangox.mechanized.Mechanized;

import java.util.*;

public class SteamPipeNetworkStorage extends PersistentState {

	public static final String KEY = Mechanized.MODID + "pipe_network";

	List<PipeNetwork> pipe_networks = new ArrayList<PipeNetwork>();
	int nextID = 0;

	public SteamPipeNetworkStorage() {
		super(KEY);
	}

	public void createNewPipeNetwork(BlockPos pos, World world) {
		PipeNetwork pn = new PipeNetwork();
		pn.addPipe(pos, this);
		int index = getNextID();
		// Shouldn't actually need the index here but better safe then sorry
		pipe_networks.add(index, pn);
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof NetworkMember) {
			((NetworkMember) be).setNetwork(index);
		}
	}

	@Nullable
	public PipeNetwork getPipeNetwork(int id) {
		if (id >= 0 && id < pipe_networks.size())
			return pipe_networks.get(id);
		return null;
	}

	public void addToPipeNetwork(int id, BlockPos pos, World world) {
		PipeNetwork pn = getPipeNetwork(id);
		if (pn != null)
			pn.addPipe(pos, this);
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof NetworkMember)
			((NetworkMember) be).setNetwork(id);
	}

	public void mergePipeNetworks(int id1, int id2, World world) {
		PipeNetwork pn1 = getPipeNetwork(id1);
		PipeNetwork pn2 = getPipeNetwork(id2);
		if (pn1 != null && pn2 != null) {
			pn1.appendNetwork(pn2.getNetwork());
			pn1.addSteam(null, pn2.getSteamAmount(null));
			for (BlockPos pos : pn2.network) {
				BlockEntity be = world.getBlockEntity(pos);
				if (be instanceof NetworkMember) {
					((NetworkMember) be).setNetwork(id1);
				}
			}
			pipe_networks.set(id2, null);
			markDirty();
		}
	}

	public void removePipeFromNetwork(int id, BlockPos splitPoint, World world) {
		PipeNetwork pn = getPipeNetwork(id);
		if (pn != null) {
			pn.removePipe(splitPoint, this);
			scanPipeNetworkCasacading(splitPoint, pn, world);
			pipe_networks.set(id, null);
		}
	}

	/**
	 * Jesus I'm so fucking sorry
	 * 
	 * @param start
	 * @param pn
	 */
	private void scanPipeNetworkCasacading(BlockPos start, PipeNetwork pn, World world) {
		@SuppressWarnings("unchecked")
		List<BlockPos>[] searchList = new ArrayList[6];
		for (int i = 0; i < searchList.length; i++) {
			BlockPos bp = start.offset(Direction.values()[i]);
			if (pn.contains(bp)) {
				searchList[i] = new ArrayList<BlockPos>();
				searchList[i].add(bp);
				for (int j = 0; j < searchList[i].size(); j++) {
					for (int k = 0; k < Direction.values().length; k++) {
						BlockPos bpo = searchList[i].get(j).offset(Direction.values()[k]);
						if (pn.contains(bpo) && !searchList[i].contains(bpo))
							searchList[i].add(bpo);
					}
					boolean stop = false;
					for (int l = 0; l < searchList.length; l++) {
						if (l != i && searchList[l] != null && searchList[i] != null
								&& searchList[l].contains(searchList[i].get(j))) {
							searchList[i] = null;
							stop = true;
						}
					}
					if (stop)
						break;
				}
			}
		}
		for (int i = 0; i < searchList.length; i++) {
			if (searchList[i] != null) {
				PipeNetwork newPn = new PipeNetwork();
				newPn.addAllPipes(searchList[i]);
				int index = getNextID();
				// Shouldn't actually need the index here but better safe then sorry
				pipe_networks.add(index, newPn);
				for (BlockPos pos : searchList[i]) {
					BlockEntity be = world.getBlockEntity(pos);
					if (be instanceof NetworkMember)
						((NetworkMember) be).setNetwork(index);
				}
			}
		}
	}

	public int getNextID() {
		return nextID++;
	}

	public static SteamPipeNetworkStorage getInstance(ServerWorld world) {
		return world.getPersistentStateManager().getOrCreate(() -> new SteamPipeNetworkStorage(), KEY);
	}

	@Override
	public void fromTag(CompoundTag tag) {
		nextID = tag.getInt("nextID");
		for (int i = 0; i < nextID; i++) {
			String s = String.valueOf(i);
			if (tag.contains(s)) {
				PipeNetwork network = new PipeNetwork();
				network.fromTag(tag.getCompound(s));
				pipe_networks.add(i, network);
			} else {
				pipe_networks.add(i, null);
			}
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		for (int i = 0; i < pipe_networks.size(); i++) {
			PipeNetwork pn = pipe_networks.get(i);
			if (pn != null)
				tag.put(String.valueOf(i), pn.toTag(new CompoundTag()));
		}
		tag.putInt("nextID", nextID);
		return tag;
	}

	@Override
	public String toString() {
		String value = "";
		int nonNullCount = 0;
		int count = 0;
		for (PipeNetwork net : pipe_networks) {
			count++;
			if (net != null) {
				value += "Index:" + String.valueOf(count) + " " + net.toString() + " \n ";
				nonNullCount++;
			}
		}
		return value + " \n Contains:" + nonNullCount;
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

		public void addAllPipes(Collection<BlockPos> coll) {
			network.addAll(coll);
		}

		public boolean contains(BlockPos pos) {
			return network.contains(pos);
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

		@Override
		public String toString() {
			return network.toString() + " Amount: " + String.valueOf(steamAmount);
		}
	}

	public static interface NetworkMember {
		public void setNetwork(int id);

		public int getNetwork();
	}
}
