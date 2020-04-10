package net.snakefangox.mechanized.reiplugin.alloy_furnace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import me.shedaniel.math.api.Point;
import me.shedaniel.math.api.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.RecipeDisplay;
import me.shedaniel.rei.gui.widget.EntryWidget;
import me.shedaniel.rei.gui.widget.RecipeArrowWidget;
import me.shedaniel.rei.gui.widget.RecipeBaseWidget;
import me.shedaniel.rei.gui.widget.Widget;
import me.shedaniel.rei.plugin.DefaultPlugin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.Mechanized;
import net.snakefangox.mechanized.blocks.entity.AlloyFurnaceEntity;
import net.snakefangox.mechanized.recipes.AlloyRecipe;
import net.snakefangox.mechanized.reiplugin.alloy_furnace.AlloyCategory.AlloyRecipeDisplay;

public class AlloyCategory implements RecipeCategory<AlloyRecipeDisplay> {

	public static final Identifier ALLOY_CAT = new Identifier(Mechanized.MODID, "plugins/alloy_furnace");

	@Override
	public Identifier getIdentifier() {
		return ALLOY_CAT;
	}

	@Override
	public String getCategoryName() {
		return I18n.translate(Mechanized.MODID + ".reiplugin.alloy_furnace");
	}

	@Override
	public EntryStack getLogo() {
		return EntryStack.create(MRegister.ALLOY_FURNACE);
	}

	@Override
	public List<Widget> setupDisplay(Supplier<AlloyRecipeDisplay> recipeDisplaySupplier, Rectangle bounds) {
		final RecipeDisplay recipeDisplay = recipeDisplaySupplier.get();
		Point startPoint = new Point(bounds.getCenterX() - 41, bounds.getCenterY() - 17);
		List<Widget> widgets = new LinkedList<>(Collections.singletonList(new RecipeBaseWidget(bounds) {
			@Override
			public void render(int mouseX, int mouseY, float delta) {
				super.render(mouseX, mouseY, delta);
				MinecraftClient.getInstance().getTextureManager().bindTexture(DefaultPlugin.getDisplayTexture());
				blit(startPoint.x - 7, startPoint.y, 0, 177, 25, 35);
				int height = 14 - MathHelper.ceil((System.currentTimeMillis() / 250d % 14d) / 1f);
				blit(startPoint.x - 5, startPoint.y + 31 + (3 - height), 82, 77 + (14 - height), 14, height);

			}
		}));
		
		widgets.add(RecipeArrowWidget.create(new Point(startPoint.x + 24, startPoint.y + 8), true)
				.time(AlloyFurnaceEntity.SMELT_TIME * 50));
		widgets.add(EntryWidget.create(startPoint.x - 16, startPoint.y + 1)
				.entries(recipeDisplay.getInputEntries().get(0)).markIsInput());
		widgets.add(EntryWidget.create(startPoint.x + 2, startPoint.y + 1)
				.entries(recipeDisplay.getInputEntries().get(1)).markIsInput());
		widgets.add(EntryWidget.create(startPoint.x + 55, startPoint.y + 9).entries(recipeDisplay.getOutputEntries())
				.markIsOutput());
		return widgets;
	}

	public static class AlloyRecipeDisplay implements RecipeDisplay {

		final List<List<EntryStack>> input;
		final List<EntryStack> output;

		public AlloyRecipeDisplay(AlloyRecipe recipeIn) {
			input = new ArrayList<List<EntryStack>>();

			List<EntryStack> entries1 = new ArrayList<>();
			for (ItemStack stack : recipeIn.input1.getMatchingStacksClient()) {
				EntryStack estack = EntryStack.create(stack);
				estack.setAmount(recipeIn.amount1);
				entries1.add(estack);
			}
			input.add(entries1);

			List<EntryStack> entries2 = new ArrayList<>();
			for (ItemStack stack : recipeIn.input2.getMatchingStacksClient()) {
				EntryStack estack = EntryStack.create(stack);
				estack.setAmount(recipeIn.amount2);
				entries2.add(estack);
			}
			input.add(entries2);

			output = Collections.singletonList(EntryStack.create(recipeIn.getOutput()));
		}

		@Override
		public List<List<EntryStack>> getInputEntries() {
			return input;
		}

		@Override
		public List<EntryStack> getOutputEntries() {
			return output;
		}

		@Override
		public Identifier getRecipeCategory() {
			return ALLOY_CAT;
		}

	}
}
