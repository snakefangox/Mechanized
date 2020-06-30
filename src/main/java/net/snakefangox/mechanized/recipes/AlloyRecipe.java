package net.snakefangox.mechanized.recipes;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.snakefangox.mechanized.Mechanized;

public class AlloyRecipe implements Recipe<Inventory> {

	public static final Identifier ID = new Identifier(Mechanized.MODID, "alloy_furnace_recipe");

	public final Identifier id;
	public final Ingredient input1;
	public final Ingredient input2;
	public final int amount1;
	public final int amount2;
	public final ItemStack outStack;

	public AlloyRecipe(Identifier idIn, Ingredient input1In, Ingredient input2In, int amount1In, int amount2In,
			ItemStack outStackIn) {
		id = idIn;
		input1 = input1In;
		input2 = input2In;
		amount1 = amount1In;
		amount2 = amount2In;
		outStack = outStackIn;
	}

	@Override
	public boolean matches(Inventory inv, World world) {
		if (inv.size() < 3)
			return false;
		return (input1.test(inv.getStack(0)) && inv.getStack(0).getCount() >= amount1
				&& input2.test(inv.getStack(1)) && inv.getStack(1).getCount() >= amount2)
				|| (input1.test(inv.getStack(1)) && inv.getStack(1).getCount() >= amount1
						&& input2.test(inv.getStack(0)) && inv.getStack(0).getCount() >= amount2);
	}

	@Override
	public ItemStack craft(Inventory inv) {
		inv.removeStack(0, amount1);
		inv.removeStack(1, amount2);
		return getOutput();
	}

	@Override
	public boolean fits(int width, int height) {
		return false;
	}

	@Override
	public ItemStack getOutput() {
		return outStack.copy();
	}

	@Override
	public Identifier getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return AlloyRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<?> getType() {
		return AlloyRecipeType.INSTANCE;
	}

	public static class AlloyRecipeSerializer implements RecipeSerializer<AlloyRecipe> {

		public static final AlloyRecipeSerializer INSTANCE = new AlloyRecipeSerializer();

		private AlloyRecipeSerializer() {
		}

		@Override
		public AlloyRecipe read(Identifier id, JsonObject json) {
			AlloyRecipeJsonFormat recipeJson = new Gson().fromJson(json, AlloyRecipeJsonFormat.class);
			if ((recipeJson.input1 == null || recipeJson.input2 == null || recipeJson.outputItem == null)) {
				throw new JsonSyntaxException("Alloy furnace recipe is missing a required attribute");
			}
			if (recipeJson.amount1 <= 0)
				recipeJson.amount1 = 1;
			if (recipeJson.amount2 <= 0)
				recipeJson.amount2 = 1;
			if (recipeJson.outputAmount <= 0)
				recipeJson.outputAmount = recipeJson.amount1 + recipeJson.amount2;
			Ingredient in1 = Ingredient.fromJson(recipeJson.input1);
			Ingredient in2 = Ingredient.fromJson(recipeJson.input2);

			ItemStack out;
			if (recipeJson.guideBookNBT) {
				Item outItem = Registry.ITEM.getOrEmpty(new Identifier(recipeJson.outputItem)).orElse(null);
				if (outItem != null) {
					out = new ItemStack(outItem, recipeJson.outputAmount);
					out.getOrCreateTag().putString("patchouli:book", "mechanized:mechanized_guide");
				} else {
					out = new ItemStack(Items.WRITTEN_BOOK);
					out.getOrCreateTag().putString("title", "Book - Burned");
					out.getOrCreateTag().putString("author", "You");
					ListTag tag = new ListTag();
					tag.add(StringTag.of("Patchouli_Stand-in"));
					out.getOrCreateTag().put("pages", tag);
				}
			} else {
				Item outItem = Registry.ITEM.getOrEmpty(new Identifier(recipeJson.outputItem))
						.orElseThrow(() -> new JsonSyntaxException(
								"Alloy furnace recipe uses " + recipeJson.outputItem + " which does not exist"));
				out = new ItemStack(outItem, recipeJson.outputAmount);
			}
			return new AlloyRecipe(id, in1, in2, recipeJson.amount1, recipeJson.amount2, out);
		}

		@Override
		public AlloyRecipe read(Identifier id, PacketByteBuf buf) {
			Ingredient in1 = Ingredient.fromPacket(buf);
			Ingredient in2 = Ingredient.fromPacket(buf);
			int am1 = buf.readInt();
			int am2 = buf.readInt();
			ItemStack out = buf.readItemStack();
			return new AlloyRecipe(id, in1, in2, am1, am2, out);
		}

		@Override
		public void write(PacketByteBuf buf, AlloyRecipe recipe) {
			recipe.input1.write(buf);
			recipe.input2.write(buf);
			buf.writeInt(recipe.amount1);
			buf.writeInt(recipe.amount2);
			buf.writeItemStack(recipe.outStack);
		}

	}

	class AlloyRecipeJsonFormat {
		JsonObject input1;
		JsonObject input2;
		int amount1;
		int amount2;
		String outputItem;
		int outputAmount;
		boolean guideBookNBT;
	}

	public static class AlloyRecipeType implements RecipeType<AlloyRecipe> {
		private AlloyRecipeType() {
		}

		public static final AlloyRecipeType INSTANCE = new AlloyRecipeType();
	}
}
