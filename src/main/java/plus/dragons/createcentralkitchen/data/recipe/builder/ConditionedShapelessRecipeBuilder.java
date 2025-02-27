package plus.dragons.createcentralkitchen.data.recipe.builder;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ConditionedShapelessRecipeBuilder implements RecipeBuilder, ConditionedRecipeBuilder<ConditionedShapelessRecipeBuilder> {
    private final ResourceLocation id;
    private Item result;
    private int count = 1;
    private final List<Ingredient> ingredients = Lists.newArrayList();
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    @Nullable
    private String group;
    private final List<ICondition> conditions = new ArrayList<>();
    
    public ConditionedShapelessRecipeBuilder(ResourceLocation id) {
        this.id = id;
    }
    
    /**
     * Adds an ingredient that can be any item in the given tag.
     */
    public ConditionedShapelessRecipeBuilder requires(TagKey<Item> pTag) {
        return this.requires(Ingredient.of(pTag));
    }
    
    /**
     * Adds an ingredient of the given item.
     */
    public ConditionedShapelessRecipeBuilder requires(ItemLike pItem) {
        return this.requires(pItem, 1);
    }
    
    /**
     * Adds the given ingredient multiple times.
     */
    public ConditionedShapelessRecipeBuilder requires(ItemLike pItem, int pQuantity) {
        for(int i = 0; i < pQuantity; ++i) {
            this.requires(Ingredient.of(pItem));
        }
        
        return this;
    }
    
    /**
     * Adds an ingredient.
     */
    public ConditionedShapelessRecipeBuilder requires(Ingredient pIngredient) {
        return this.requires(pIngredient, 1);
    }
    
    /**
     * Adds an ingredient multiple times.
     */
    public ConditionedShapelessRecipeBuilder requires(Ingredient pIngredient, int pQuantity) {
        for(int i = 0; i < pQuantity; ++i) {
            this.ingredients.add(pIngredient);
        }
        
        return this;
    }
    
    public ConditionedShapelessRecipeBuilder output(ItemLike item, int count) {
        this.result = item.asItem();
        this.count = count;
        return this;
    }
    
    public ConditionedShapelessRecipeBuilder output(ItemLike item) {
        return output(item, 1);
    }
    
    @Override
    public ConditionedShapelessRecipeBuilder withCondition(ICondition condition) {
        conditions.add(condition);
        return this;
    }
    
    @Override
    public ConditionedShapelessRecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
        this.advancement.addCriterion(pCriterionName, pCriterionTrigger);
        return this;
    }
    
    @Override
    public ConditionedShapelessRecipeBuilder group(@Nullable String pGroupName) {
        this.group = pGroupName;
        return this;
    }
    
    @Override
    public Item getResult() {
        return this.result;
    }
    
    @Override
    public void save(Consumer<FinishedRecipe> consumer) {
        this.save(consumer, this.id);
    }
    
    @Override
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        id = new ResourceLocation(id.getNamespace(), "crafting/" + id.getPath());
        this.ensureValid(id);
        this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
        consumer.accept(new Result(
            id, this.result, this.count,
            this.group == null ? "" : this.group,
            this.ingredients, this.advancement,
            new ResourceLocation(id.getNamespace(),
                "recipes/" + id.getPath()),
            conditions));
    }
    
    /**
     * Makes sure that this recipe is valid
     */
    private void ensureValid(ResourceLocation id) {
        if (this.result == null) {
            throw new IllegalStateException("No result is defined for shapeless recipe " + id + "!");
        }
    }
    
    public static class Result extends ShapelessRecipeBuilder.Result {
        private final boolean serializeAdvancement;
        private final List<ICondition> conditions;
        
        public Result(ResourceLocation id,
                      Item result,
                      int count,
                      String group,
                      List<Ingredient> ingredients,
                      Advancement.Builder advancement,
                      ResourceLocation advancementId,
                      List<ICondition> conditions) {
            super(id, result, count, group, ingredients, advancement, advancementId);
            this.serializeAdvancement = !advancement.getCriteria().isEmpty();
            this.conditions = conditions;
        }
    
        @Override
        public void serializeRecipeData(JsonObject json) {
            super.serializeRecipeData(json);
            if (!conditions.isEmpty()) {
                JsonArray conds = new JsonArray();
                conditions.forEach(c -> conds.add(CraftingHelper.serialize(c)));
                json.add("conditions", conds);
            }
        }
    
        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return serializeAdvancement ? super.serializeAdvancement() : null;
        }
        
    }
    
}
