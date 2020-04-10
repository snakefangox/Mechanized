package net.snakefangox.mechanized.reiplugin;

import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.minecraft.util.Identifier;
import net.snakefangox.mechanized.Mechanized;
import net.snakefangox.mechanized.recipes.AlloyRecipe;
import net.snakefangox.mechanized.reiplugin.alloy_furnace.AlloyCategory;
import net.snakefangox.mechanized.reiplugin.alloy_furnace.AlloyCategory.AlloyRecipeDisplay;

public class REIEntryPoint implements REIPluginV0 {
	
	public static final Identifier PLUGIN_ID = new Identifier(Mechanized.MODID, "reiplugin");

	@Override
	public Identifier getPluginIdentifier() {
		return PLUGIN_ID;
	}

	@Override
	public void registerPluginCategories(RecipeHelper recipeHelper) {
		recipeHelper.registerCategory(new AlloyCategory());
	}
	
	@Override
	public void registerRecipeDisplays(RecipeHelper recipeHelper) {
		recipeHelper.registerRecipes(AlloyCategory.ALLOY_CAT, AlloyRecipe.class, AlloyRecipeDisplay::new);
	}
	
}
