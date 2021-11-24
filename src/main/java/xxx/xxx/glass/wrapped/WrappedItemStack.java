package xxx.xxx.glass.wrapped;

import org.bukkit.inventory.ItemStack;

/**
 * Wrapped version of the ItemStack class for different hashcode and
 * an independent amount.
 *
 * @see ItemStack
 */

public class WrappedItemStack {

    private final ItemStack item;
    private final int amount;

    public WrappedItemStack(final ItemStack item) {

        this.item = item;
        this.amount = item.getAmount();

    }

    public WrappedItemStack(final ItemStack item, final int amount) {

        this.item = item;
        this.amount = amount;

    }

    /**
     * Used to return the underlying ItemStack.
     *
     * @return The underlying ItemStack.
     */

    public ItemStack getItem() {

        return item;

    }

    /**
     * Used to return the amount.
     *
     * @return The amount.
     */

    public int getAmount() {

        return amount;

    }

    /**
     * Used to call the #toString super method which converts
     * the ItemStack to a semi readable string.
     *
     * @return The ItemStack converted to a string.
     * @see ItemStack#toString()
     */

    @Override
    public String toString() {

        return item.toString();

    }

    /**
     * Used to compare two WrappedItemStack instances to determine wherever or not
     * they are considered equal. Overrides the default implementation and differs
     * from the ItemStack equals implementation.
     *
     * @param obj The object to compare to.
     * @return <code>true</code> if the objects are considered equal.
     */

    @Override
    public boolean equals(final Object obj) {

        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof ItemStack) && !(obj instanceof WrappedItemStack)) return false;

        final ItemStack item = (obj instanceof ItemStack) ? ((ItemStack) obj) : ((WrappedItemStack) obj).item;
        return this.item.isSimilar(item);

    }

    /**
     * Used to generate a hash code based on the current WrappedItemStack instance.
     * Overrides the default implementation and differs from the ItemStack hashCode
     * generation implementation.
     *
     * @return The generated hash code.
     */

    @SuppressWarnings("deprecation")
    @Override
    public int hashCode() {

        int hash = 7;
        hash = 31 * hash + item.getTypeId();
        hash = 31 * hash + (item.getDurability() & '\uffff');
        hash = 31 * hash + (item.hasItemMeta() ? item.getItemMeta().hashCode() : 0);

        return hash;

    }

}
