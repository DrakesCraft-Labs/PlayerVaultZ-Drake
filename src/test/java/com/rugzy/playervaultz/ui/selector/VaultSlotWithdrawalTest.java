package com.rugzy.playervaultz.ui.selector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rugzy.playervaultz.core.vault.VaultPage;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

class VaultSlotWithdrawalTest {
    @Test
    void removesTheRealSlotWhenTakingTheWholeStack() {
        VaultPage page = new VaultPage(1);
        ItemStack stored = mock(ItemStack.class);
        ItemStack withdrawn = mock(ItemStack.class);
        when(stored.getAmount()).thenReturn(12);
        when(stored.clone()).thenReturn(withdrawn);
        page.setItem(4, stored);

        ItemStack result = VaultSlotWithdrawal.take(page, 4, 64);

        assertSame(withdrawn, result);
        verify(withdrawn).setAmount(12);
        assertNull(page.getItem(4));
    }

    @Test
    void leavesOnlyTheUnwithdrawnRemainder() {
        VaultPage page = new VaultPage(1);
        ItemStack stored = mock(ItemStack.class);
        ItemStack withdrawn = mock(ItemStack.class);
        ItemStack remainder = mock(ItemStack.class);
        when(stored.getAmount()).thenReturn(12);
        when(stored.clone()).thenReturn(withdrawn, remainder);
        page.setItem(8, stored);

        ItemStack result = VaultSlotWithdrawal.take(page, 8, 5);

        assertSame(withdrawn, result);
        assertSame(remainder, page.getItem(8));
        verify(withdrawn).setAmount(5);
        verify(remainder).setAmount(7);
    }

    @Test
    void clonedPageSnapshotsCannotAffectStoredContents() {
        VaultPage page = new VaultPage(1);
        ItemStack stored = mock(ItemStack.class);
        page.setItem(2, stored);

        page.getItems()[2] = null;

        assertSame(stored, page.getItem(2));
    }
}
