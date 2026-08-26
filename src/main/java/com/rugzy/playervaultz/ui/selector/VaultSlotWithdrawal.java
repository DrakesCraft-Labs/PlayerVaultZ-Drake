package com.rugzy.playervaultz.ui.selector;

import com.rugzy.playervaultz.core.vault.VaultPage;
import org.bukkit.inventory.ItemStack;

/** Removes an item from the real vault page while preserving all custom metadata. */
final class VaultSlotWithdrawal {
    private VaultSlotWithdrawal() {
    }

    static ItemStack take(VaultPage page, int slot, int requestedAmount) {
        ItemStack stored = page.getItem(slot);
        if (stored == null || requestedAmount <= 0) {
            throw new IllegalArgumentException("A populated slot and positive amount are required");
        }

        int withdrawnAmount = Math.min(stored.getAmount(), requestedAmount);
        ItemStack withdrawn = stored.clone();
        withdrawn.setAmount(withdrawnAmount);

        if (withdrawnAmount == stored.getAmount()) {
            page.setItem(slot, null);
        } else {
            ItemStack remainder = stored.clone();
            remainder.setAmount(stored.getAmount() - withdrawnAmount);
            page.setItem(slot, remainder);
        }
        return withdrawn;
    }
}
