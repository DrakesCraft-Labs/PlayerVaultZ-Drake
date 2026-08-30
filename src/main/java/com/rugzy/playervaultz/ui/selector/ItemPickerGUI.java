/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.TextColor
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemFlag
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package com.rugzy.playervaultz.ui.selector;

import com.rugzy.playervaultz.PlayerVaultZ;
import com.rugzy.playervaultz.core.vault.Vault;
import com.rugzy.playervaultz.core.vault.VaultPage;
import com.rugzy.playervaultz.ui.selector.VaultSelectorGUI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemPickerGUI
implements InventoryHolder {
    private static final int GUI_SIZE = 54;
    private static final int ITEMS_PER_PAGE = 45;
    private final PlayerVaultZ plugin;
    private final Player viewer;
    private final Vault vault;
    private final int returnPage;
    private Inventory inventory;
    private List<ItemStack> vaultItems;
    private int currentPage = 0;
    private boolean retrievalInProgress;

    public ItemPickerGUI(PlayerVaultZ plugin, Player viewer, Vault vault, int returnPage) {
        this.plugin = plugin;
        this.viewer = viewer;
        this.vault = vault;
        this.returnPage = returnPage;
        this.loadVaultItems();
        this.createGUI();
        ItemPickerGuard.ensureRegistered(plugin);
    }

    private void loadVaultItems() {
        this.vaultItems = new ArrayList<ItemStack>();
        for (VaultPage page : this.vault.getPages()) {
            for (ItemStack item : page.getItems()) {
                if (item == null || item.getType() == Material.AIR) continue;
                this.vaultItems.add(item.clone());
            }
        }
    }

    private void createGUI() {
        String title = String.format("\u00a76Quick Pick - Vault #%d \u00a77(Page %d/%d)", this.vault.getVaultNumber(), this.currentPage + 1, Math.max(1, (int)Math.ceil((double)this.vaultItems.size() / 45.0)));
        this.inventory = Bukkit.createInventory((InventoryHolder)this, (int)54, (String)title);
        this.populateItems();
        this.setupNavigation();
    }

    private void populateItems() {
        for (int i = 0; i < 45; ++i) {
            this.inventory.setItem(i, null);
        }
        int startIndex = this.currentPage * 45;
        int endIndex = Math.min(startIndex + 45, this.vaultItems.size());
        for (int i = startIndex; i < endIndex; ++i) {
            ItemStack item = this.vaultItems.get(i).clone();
            this.enhanceItemLore(item, i);
            int slot = i - startIndex;
            this.inventory.setItem(slot, item);
        }
    }

    private void enhanceItemLore(ItemStack item, int index) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        if (!lore.isEmpty()) {
            lore.add("");
        }
        lore.add("\u00a78\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500");
        lore.add("\u00a77Item #" + (index + 1) + " of " + this.vaultItems.size());
        lore.add("");
        lore.add("\u00a7e\u25b6 Left-click to take item");
        lore.add("\u00a7b\u25b6 Right-click to take stack");
        lore.add("\u00a7d\u25b6 Shift-click to take all similar");
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    private void setupNavigation() {
        if (this.currentPage > 0) {
            this.inventory.setItem(45, this.createNavigationItem(Material.ARROW, "\u00a7a\u25c0 Previous Page", Arrays.asList("\u00a77Go to page " + this.currentPage)));
        } else {
            this.inventory.setItem(45, this.createNavigationItem(Material.GRAY_DYE, "\u00a77\u25c0 Previous Page", Arrays.asList("\u00a7cYou're on the first page")));
        }
        this.inventory.setItem(47, this.createNavigationItem(Material.BOOK, "\u00a76Item Picker Info", Arrays.asList("\u00a77\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500", "\u00a77Total Items: \u00a7e" + this.vaultItems.size(), "\u00a77Current Page: \u00a7b" + (this.currentPage + 1), "\u00a77\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500", "", "\u00a77Browse and quickly", "\u00a77retrieve items from", "\u00a77your vault")));
        this.inventory.setItem(49, this.createNavigationItem(Material.ENDER_CHEST, "\u00a7aOpen Full Vault", Arrays.asList("\u00a77Open the complete vault", "\u00a77interface for full access")));
        this.inventory.setItem(51, this.createNavigationItem(Material.DARK_OAK_DOOR, "\u00a7cBack to Vault Selector", Arrays.asList("\u00a77Return to vault list")));
        int totalPages = (int)Math.ceil((double)this.vaultItems.size() / 45.0);
        if (this.currentPage < totalPages - 1) {
            this.inventory.setItem(53, this.createNavigationItem(Material.ARROW, "\u00a7aNext Page \u25b6", Arrays.asList("\u00a77Go to page " + (this.currentPage + 2))));
        } else {
            this.inventory.setItem(53, this.createNavigationItem(Material.GRAY_DYE, "\u00a77Next Page \u25b6", Arrays.asList("\u00a7cYou're on the last page")));
        }
        ItemStack filler = this.createNavigationItem(Material.BLACK_STAINED_GLASS_PANE, " ", new ArrayList<String>());
        for (int i = 46; i <= 52; ++i) {
            if (this.inventory.getItem(i) != null) continue;
            this.inventory.setItem(i, filler);
        }
    }

    private ItemStack createNavigationItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
            item.setItemMeta(meta);
        }
        return item;
    }

    public void handleClick(int slot, ClickType clickType) {
        if (slot < 0 || slot >= GUI_SIZE || clickType == null) {
            return;
        }
        if (slot < 45) {
            int index = this.currentPage * 45 + slot;
            if (index < this.vaultItems.size()) {
                this.handleItemRetrieval(index, clickType);
            }
        } else if (slot >= 45) {
            this.handleNavigationClick(slot);
        }
    }

    private void handleItemRetrieval(int index, ClickType clickType) {
        if (this.retrievalInProgress) {
            this.viewer.sendMessage(Component.text("A withdrawal is already being saved.").color(NamedTextColor.YELLOW));
            return;
        }
        ItemStack targetItem = this.vaultItems.get(index);
        if (targetItem == null) {
            return;
        }
        switch (clickType) {
            case LEFT: {
                this.retrieveItems(targetItem, 1);
                break;
            }
            case RIGHT: {
                this.retrieveItems(targetItem, targetItem.getMaxStackSize());
                break;
            }
            case SHIFT_LEFT: 
            case SHIFT_RIGHT: {
                this.retrieveAllOfType(targetItem);
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * Entrega items del vault SOLO cuando la persistencia esta confirmada.
     *
     * Antes se anadian al inventario del jugador de inmediato y el guardado corria
     * despues, de forma asincrona. Si ese guardado fallaba, el item ya estaba en el
     * inventario pero el vault seguia conteniendolo al releerse de disco: duplicado
     * permanente que ni siquiera desaparecia al reconectar. Es el reporte de
     * Dival_830 ("pasa a mi inventario pero tambien se mantiene en el vault").
     *
     * Ahora se retira en memoria, se persiste, y solo entonces se entrega. Si el
     * guardado falla se restaura el vault exactamente como estaba y no se entrega
     * nada: es preferible repetir el clic a duplicar.
     */
    private void retrieveItems(ItemStack target, int amount) {
        if (this.retrievalInProgress) {
            // Una retirada en vuelo. Los packs de UI que remapean botones envian
            // rafagas de clics; sin esto se solapan varias retiradas del mismo slot.
            return;
        }
        int capacity = InventoryCapacity.availableFor(this.viewer.getInventory(), target);
        if (capacity <= 0) {
            this.viewer.sendMessage(Component.text("Your inventory is full!").color(NamedTextColor.RED));
            return;
        }

        this.retrievalInProgress = true;
        List<VaultSlotSnapshot> snapshots = new ArrayList<VaultSlotSnapshot>();
        List<ItemStack> withdrawnItems = new ArrayList<ItemStack>();
        int retrieved = 0;
        int requested = Math.min(amount, capacity);
        for (VaultPage page : this.vault.getPages()) {
            for (int slot = 0; slot < VaultPage.USABLE_SLOTS && retrieved < requested; ++slot) {
                ItemStack stored = page.getItem(slot);
                if (stored == null || !stored.isSimilar(target)) {
                    continue;
                }
                snapshots.add(new VaultSlotSnapshot(page, slot, stored.clone()));
                ItemStack withdrawn = VaultSlotWithdrawal.take(page, slot, requested - retrieved);
                withdrawnItems.add(withdrawn);
                retrieved += withdrawn.getAmount();
            }
        }
        if (retrieved <= 0) {
            this.retrievalInProgress = false;
            return;
        }

        this.vault.markDirty();
        int finalRetrieved = retrieved;
        this.plugin.getVaultManager().saveVault(this.vault).whenComplete((saved, error) ->
            Bukkit.getScheduler().runTask(this.plugin, () -> {
                this.retrievalInProgress = false;
                if (error != null || !Boolean.TRUE.equals(saved)) {
                    for (VaultSlotSnapshot snapshot : snapshots) {
                        snapshot.restore();
                    }
                    this.vault.markDirty();
                    this.plugin.getLogger().severe("Quick Pick withdrawal could not be persisted for "
                        + this.viewer.getUniqueId() + " in vault #" + this.vault.getVaultNumber()
                        + "; vault restaurado y no se entrego nada");
                    this.viewer.sendMessage(Component.text(
                        "No se pudo guardar el vault. No se retiro nada; intentalo de nuevo.")
                        .color(NamedTextColor.RED));
                    this.loadVaultItems();
                    this.refresh();
                    return;
                }
                for (ItemStack withdrawn : withdrawnItems) {
                    for (ItemStack leftover : this.viewer.getInventory().addItem(withdrawn).values()) {
                        // El inventario pudo llenarse entre la comprobacion y este tick.
                        // Se suelta al suelo antes que perderlo.
                        this.viewer.getWorld().dropItemNaturally(this.viewer.getLocation(), leftover);
                    }
                }
                this.viewer.sendMessage(Component.text("Retrieved " + finalRetrieved + "x "
                    + this.formatMaterialName(target.getType())).color(NamedTextColor.GREEN));
                this.loadVaultItems();
                this.refresh();
            })
        );
    }

    /** Estado de un slot antes de retirar, para deshacer si la persistencia falla. */
    private static final class VaultSlotSnapshot {
        private final VaultPage page;
        private final int slot;
        private final ItemStack previous;

        VaultSlotSnapshot(VaultPage page, int slot, ItemStack previous) {
            this.page = page;
            this.slot = slot;
            this.previous = previous;
        }

        void restore() {
            this.page.setItem(this.slot, this.previous);
        }
    }

    private void retrieveAllOfType(ItemStack target) {
        int retrieved = 0;
        for (VaultPage page : this.vault.getPages()) {
            for (ItemStack item : page.getItems()) {
                if (item == null || !item.isSimilar(target)) continue;
                retrieved += item.getAmount();
            }
        }
        if (retrieved > 0) {
            this.retrieveItems(target, retrieved);
        }
    }

    private String formatMaterialName(Material material) {
        String name = material.name().toLowerCase().replace('_', ' ');
        String[] words = name.split(" ");
        StringBuilder formatted = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) continue;
            formatted.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
        }
        return formatted.toString().trim();
    }

    private void handleNavigationClick(int slot) {
        switch (slot) {
            case 45: {
                if (this.currentPage <= 0) break;
                --this.currentPage;
                this.refresh();
                break;
            }
            case 49: {
                this.viewer.closeInventory();
                this.plugin.getVaultManager().openVault(this.viewer, this.vault.getVaultNumber());
                break;
            }
            case 51: {
                this.viewer.closeInventory();
                new VaultSelectorGUI(this.plugin, this.viewer, this.vault.getOwner()).open();
                break;
            }
            case 53: {
                int totalPages = (int)Math.ceil((double)this.vaultItems.size() / 45.0);
                if (this.currentPage >= totalPages - 1) break;
                ++this.currentPage;
                this.refresh();
            }
            default: {
                break;
            }
        }
    }

    private void refresh() {
        this.inventory.clear();
        this.populateItems();
        this.setupNavigation();
        String title = String.format("\u00a76Quick Pick - Vault #%d \u00a77(Page %d/%d)", this.vault.getVaultNumber(), this.currentPage + 1, Math.max(1, (int)Math.ceil((double)this.vaultItems.size() / 45.0)));
        Inventory newInv = Bukkit.createInventory((InventoryHolder)this, (int)54, (String)title);
        newInv.setContents(this.inventory.getContents());
        this.inventory = newInv;
        this.viewer.openInventory(this.inventory);
    }

    public void open() {
        this.viewer.openInventory(this.inventory);
    }

    public Inventory getInventory() {
        return this.inventory;
    }
}
