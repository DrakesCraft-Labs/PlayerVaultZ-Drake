package com.rugzy.playervaultz.ui.selector;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/** Calculates exact capacity so Quick Pick never removes items that cannot be delivered. */
final class InventoryCapacity {
    private InventoryCapacity() {
    }

    static int availableFor(Inventory inventory, ItemStack target) {
        int capacity = 0;
        for (ItemStack item : inventory.getStorageContents()) {
            if (item == null || item.getType().isAir()) {
                capacity += target.getMaxStackSize();
            } else if (item.isSimilar(target)) {
                capacity += Math.max(0, item.getMaxStackSize() - item.getAmount());
            }
        }
        return capacity;
    }
}
