package mcjty.rftoolsutility.modules.environmental.recipes;

import mcjty.rftoolsutility.modules.environmental.EnvironmentalModule;
import mcjty.rftoolsutility.modules.spawner.items.SyringeItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class SyringeBasedRecipe extends ShapedRecipe {

    private final ResourceLocation mobId;

    public SyringeBasedRecipe(ResourceLocation id, String group, int width, int height, NonNullList<Ingredient> ingredients, ItemStack result, ResourceLocation mobId) {
        super(id, group, width, height, addMob(ingredients, mobId), result);
        this.mobId = mobId;
    }

    public SyringeBasedRecipe(ShapedRecipe other, ResourceLocation mobId) {
        super(other.getId(), other.getGroup(), other.getWidth(), other.getHeight(), addMob(other.getIngredients(), mobId), other.getResultItem());
        this.mobId = mobId;
    }

    private static NonNullList<Ingredient> addMob(NonNullList<Ingredient> input, ResourceLocation mobId) {
        NonNullList<Ingredient> output = NonNullList.withSize(input.size(), Ingredient.EMPTY);
        for (int i = 0 ; i < input.size() ; i++) {
            Ingredient ingredient = input.get(i);
            if (!ingredient.isEmpty() && ingredient.getItems().length > 0 && ingredient.getItems()[0].getItem() instanceof SyringeItem) {
                ItemStack syringe = SyringeItem.createMobSyringe(mobId);
                ingredient = Ingredient.of(syringe);
            }
            output.set(i, ingredient);
        }
        return output;
    }

    @Override
    public boolean matches(@Nonnull CraftingInventory inv, @Nonnull World level) {
        boolean matches = super.matches(inv, level);
        if (matches) {
            for (int i = 0 ; i < inv.getWidth() * inv.getHeight() ; i++) {
                ItemStack stack = inv.getItem(i);
                if (stack.getItem() instanceof SyringeItem) {
                    String mob = SyringeItem.getMobId(stack);
                    if (mob == null || !mob.equals(mobId.toString())) {
                        return false;
                    }
                    int amount = SyringeItem.getLevel(stack);
                    if (amount < 100) {
                        return false;
                    }
                }
            }
        }
        return matches;
    }

    public ResourceLocation getMobId() {
        return mobId;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return EnvironmentalModule.SYRINGE_SERIALIZER.get();
    }
}
