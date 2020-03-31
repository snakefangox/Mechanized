package net.snakefangox.mechanized;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class AutoGenJson {

	private static boolean hasChanged = false;

	private static final String blockstate = "{\r\n" + "  \"variants\": {\r\n"
			+ "    \"\": { \"model\": \"modid:block/blockname\" }\r\n" + "  }\r\n" + "}";

	private static final String blockstateRotatable = "{\r\n" + "  \"variants\": {\r\n"
			+ "    \"facing=north\": { \"model\": \"modid:block/blockname\" },\r\n"
			+ "    \"facing=south\": { \"model\": \"modid:block/blockname\", \"y\": 180 },\r\n"
			+ "    \"facing=west\": { \"model\": \"modid:block/blockname\", \"y\": 270 },\r\n"
			+ "    \"facing=east\": { \"model\": \"modid:block/blockname\", \"y\": 90 }\r\n" + "  }\r\n" + "}";

	private static final String blockModel = "{\r\n" + "  \"parent\": \"block/cube_all\",\r\n" + "  \"textures\": {\r\n"
			+ "    \"all\": \"modid:block/blockname\"\r\n" + "  }\r\n" + "}";

	private static final String blockItemModel = "{\r\n" + "  \"parent\": \"modid:block/blockname\"\r\n" + "}";

	private static final String lootTable = "{\r\n" + "  \"type\": \"minecraft:block\",\r\n" + "  \"pools\": [\r\n"
			+ "    {\r\n" + "      \"rolls\": 1,\r\n" + "      \"entries\": [\r\n" + "        {\r\n"
			+ "          \"type\": \"minecraft:item\",\r\n" + "          \"name\": \"modid:blockname\"\r\n"
			+ "        }\r\n" + "      ],\r\n" + "      \"conditions\": [\r\n" + "        {\r\n"
			+ "          \"condition\": \"minecraft:survives_explosion\"\r\n" + "        }\r\n" + "      ]\r\n"
			+ "    }\r\n" + "  ]\r\n" + "}";

	private static String itemModel = "{\r\n" + "  \"parent\": \"item/generated\",\r\n" + "  \"textures\": {\r\n"
			+ "    \"layer0\": \"modid:item/itemname\"\r\n" + "  }\r\n" + "}";

	public static void autoGenMissingBlockJson(String modid, String rootPath) {
		for (Block b : Registry.BLOCK) {
			if (Registry.BLOCK.getId(b).getNamespace().equals(modid)) {

				String assetPath = rootPath + "/src/main/resources/assets/" + modid;
				String dataPath = rootPath + "/src/main/resources/data/" + modid;
				String langPath = assetPath + "/lang/en_us.json";
				String blockName = Registry.BLOCK.getId(b).getPath();

				File f = new File(assetPath + "/blockstates/" + blockName + ".json");

				System.out.println(blockName);
				System.out.println(f.isFile());
				if (!f.isFile()) {
					hasChanged = true;
					try {
						FileOutputStream fos = new FileOutputStream(
								assetPath + "/blockstates/" + Registry.BLOCK.getId(b).getPath() + ".json");
						if (b.getDefaultState().getProperties().contains(HorizontalFacingBlock.FACING)) {
							fos.write(blockstateRotatable.replace("blockname", blockName).replace("modid", modid)
									.getBytes());
						} else {
							fos.write(blockstate.replace("blockname", blockName).replace("modid", modid).getBytes());
						}
						fos.flush();
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

					try {
						FileOutputStream fos = new FileOutputStream(
								assetPath + "/models/block/" + Registry.BLOCK.getId(b).getPath() + ".json");
						fos.write(blockModel.replace("blockname", blockName).replace("modid", modid).getBytes());
						fos.flush();
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

					try {
						FileOutputStream fos = new FileOutputStream(
								assetPath + "/models/item/" + Registry.BLOCK.getId(b).getPath() + ".json");
						fos.write(blockItemModel.replace("blockname", blockName).replace("modid", modid).getBytes());
						fos.flush();
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

					try {
						FileOutputStream fos = new FileOutputStream(
								dataPath + "/loot_tables/blocks/" + Registry.BLOCK.getId(b).getPath() + ".json");
						fos.write(lootTable.replace("blockname", blockName).replace("modid", modid).getBytes());
						fos.flush();
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

					try {
						List<String> list = Files.readAllLines(Paths.get(langPath));
						String secLastLine = list.remove(list.size() - 2);
						secLastLine = secLastLine + ",";
						list.add(list.size() - 1, secLastLine);
						list.add(list.size() - 1,
								"\"block." + modid + "." + blockName + "\": \"" + toLangName(blockName) + "\"");
						Files.write(Paths.get(langPath), list);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static void autoGenMissingItemJson(String modid, String rootPath) {
		for (Item i : Registry.ITEM) {
			if (Registry.ITEM.getId(i).getNamespace().equals(modid)) {
				String itemName = Registry.ITEM.getId(i).getPath();
				String assetPath = rootPath + "/src/main/resources/assets/" + modid;
				String itemModelPath = assetPath + "/models/item/" + itemName + ".json";
				String langPath = assetPath + "/lang/en_us.json";

				File f = new File(itemModelPath);

				System.out.println(itemName);
				System.out.println(f.isFile());

				if (!f.isFile()) {
					hasChanged = true;
					try {
						FileOutputStream fos = new FileOutputStream(itemModelPath);
						fos.write(itemModel.replace("itemname", itemName).replace("modid", modid).getBytes());
						fos.flush();
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

					try {
						List<String> list = Files.readAllLines(Paths.get(langPath));
						String secLastLine = list.remove(list.size() - 2);
						secLastLine = secLastLine + ",";
						list.add(list.size() - 1, secLastLine);
						list.add(list.size() - 1,
								"\"item." + modid + "." + itemName + "\": \"" + toLangName(itemName) + "\"");
						Files.write(Paths.get(langPath), list);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static String toLangName(String name) {
		String[] splitName = name.split("_");
		String finalName = "";
		boolean first = true;
		for (String s : splitName) {
			if (!first)
				finalName += " ";
			finalName += s.substring(0, 1).toUpperCase() + s.substring(1);
			first = false;
		}
		return finalName;
	}

	public static void checkRestartNeeded() {
		if (hasChanged) {
			System.out.println("JSON generated, refresh IDE and restart game");
			System.exit(0);
		}
	}

	public static void autoGenerateJson(String modid, String path) {
		autoGenMissingBlockJson(modid, path);
		autoGenMissingItemJson(modid, path);
		checkRestartNeeded();
	}

}
