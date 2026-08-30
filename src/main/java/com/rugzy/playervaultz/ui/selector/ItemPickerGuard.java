package com.rugzy.playervaultz.ui.selector;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

/**
 * Bloquea transferencias vanilla hacia/desde Quick Pick.
 *
 * <p>Este listener es deliberadamente sólo un guardia: el listener principal
 * del selector es el único responsable de llamar a {@code ItemPickerGUI.handleClick}.
 * Despachar la pulsación aquí también retira el mismo ítem dos veces y vuelve a
 * introducir el duplicado reportado por jugadores Bedrock.</p>
 */
public final class ItemPickerGuard implements Listener {

    private static volatile boolean registered = false;
    private static final Object LOCK = new Object();

    public static void ensureRegistered(org.bukkit.plugin.Plugin plugin) {
        if (registered || plugin == null) {
            return;
        }
        synchronized (LOCK) {
            if (registered) {
                return;
            }
            try {
                if (plugin.getServer() == null || plugin.getServer().getPluginManager() == null) {
                    return;
                }
                plugin.getServer().getPluginManager().registerEvents(new ItemPickerGuard(), plugin);
                registered = true;
            } catch (Exception ignored) {
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof ItemPickerGUI)) {
            return;
        }
        int topSize = event.getView().getTopInventory().getSize();
        int rawSlot = event.getRawSlot();

        boolean involvesTop = false;

        if (event.getClickedInventory() != null && event.getClickedInventory().equals(event.getView().getTopInventory())) {
            involvesTop = true;
        }
        if (rawSlot >= 0 && rawSlot < topSize) {
            involvesTop = true;
        }
        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            involvesTop = true;
        }
        if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR || event.getClick() == ClickType.DOUBLE_CLICK) {
            involvesTop = true;
        }
        // HOTBAR_MOVE_AND_READD está deprecado forRemoval en Paper 1.21.5 - lo chequeamos por nombre para evitar error de compilación/IDE en rojo
        InventoryAction action = event.getAction();
        if (action == InventoryAction.HOTBAR_SWAP || "HOTBAR_MOVE_AND_READD".equals(action.name())) {
            involvesTop = true;
        }
        if (event.getAction() == InventoryAction.DROP_ALL_SLOT || event.getAction() == InventoryAction.DROP_ONE_SLOT
                || event.getAction() == InventoryAction.DROP_ALL_CURSOR || event.getAction() == InventoryAction.DROP_ONE_CURSOR) {
            if (rawSlot >= 0 && rawSlot < topSize) {
                involvesTop = true;
            } else if (event.getClickedInventory() != null && event.getClickedInventory().equals(event.getView().getTopInventory())) {
                involvesTop = true;
            }
        }
        if (event.getClick() == ClickType.UNKNOWN && rawSlot >= 0 && rawSlot < topSize) {
            involvesTop = true;
        }
        if (event.getClick() == ClickType.NUMBER_KEY || event.getClick() == ClickType.SWAP_OFFHAND
                || event.getClick() == ClickType.CREATIVE || event.getClick() == ClickType.MIDDLE) {
            if (rawSlot >= 0 && rawSlot < topSize) {
                involvesTop = true;
            }
            if (event.getClickedInventory() != null && event.getClickedInventory().equals(event.getView().getTopInventory())) {
                involvesTop = true;
            }
        }

        if (involvesTop) {
            // No manejar la acción aquí. El selector ya procesa el click una vez;
            // esta cancelación únicamente impide que Bukkit aplique una segunda
            // transferencia vanilla (shift click, tecla numérica, arrastre, etc.).
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof ItemPickerGUI)) {
            return;
        }
        int topSize = event.getView().getTopInventory().getSize();
        for (int rawSlot : event.getRawSlots()) {
            if (rawSlot < topSize) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
                return;
            }
        }
        if (event.getInventory() != null && event.getInventory().equals(event.getView().getTopInventory())) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
        }
    }
}
