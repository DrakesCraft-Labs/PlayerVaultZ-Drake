package com.rugzy.playervaultz.ui.selector;

import static org.mockito.Mockito.*;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.junit.jupiter.api.Test;

class ItemPickerGuardTest {

    @Test
    void cancelsClickInTopInventoryAndForwardsToHandler() {
        Inventory top = mock(Inventory.class);
        ItemPickerGUI gui = mock(ItemPickerGUI.class);
        when(top.getHolder()).thenReturn(gui);
        when(top.getSize()).thenReturn(54);
        InventoryView view = mock(InventoryView.class);
        when(view.getTopInventory()).thenReturn(top);

        InventoryClickEvent event = mock(InventoryClickEvent.class);
        when(event.getView()).thenReturn(view);
        when(event.getClickedInventory()).thenReturn(top);
        when(event.getAction()).thenReturn(InventoryAction.PICKUP_ALL);
        when(event.getClick()).thenReturn(ClickType.LEFT);
        when(event.getRawSlot()).thenReturn(10);
        when(event.getSlot()).thenReturn(10);

        ItemPickerGuard guard = new ItemPickerGuard();
        guard.onInventoryClick(event);

        verify(event).setCancelled(true);
        verify(gui).handleClick(10, ClickType.LEFT);
    }

    @Test
    void allowsPureBottomClicks() {
        Inventory top = mock(Inventory.class);
        ItemPickerGUI gui = mock(ItemPickerGUI.class);
        when(top.getHolder()).thenReturn(gui);
        when(top.getSize()).thenReturn(54);
        InventoryView view = mock(InventoryView.class);
        when(view.getTopInventory()).thenReturn(top);
        Inventory bottom = mock(Inventory.class);

        InventoryClickEvent event = mock(InventoryClickEvent.class);
        when(event.getView()).thenReturn(view);
        when(event.getClickedInventory()).thenReturn(bottom);
        when(event.getAction()).thenReturn(InventoryAction.PICKUP_ALL);
        when(event.getClick()).thenReturn(ClickType.LEFT);
        when(event.getRawSlot()).thenReturn(60);
        when(event.getSlot()).thenReturn(6);

        ItemPickerGuard guard = new ItemPickerGuard();
        guard.onInventoryClick(event);

        verify(event, never()).setCancelled(true);
    }

    @Test
    void cancelsMoveToOtherInventoryFromBottom() {
        Inventory top = mock(Inventory.class);
        ItemPickerGUI gui = mock(ItemPickerGUI.class);
        when(top.getHolder()).thenReturn(gui);
        when(top.getSize()).thenReturn(54);
        InventoryView view = mock(InventoryView.class);
        when(view.getTopInventory()).thenReturn(top);
        Inventory bottom = mock(Inventory.class);

        InventoryClickEvent event = mock(InventoryClickEvent.class);
        when(event.getView()).thenReturn(view);
        when(event.getClickedInventory()).thenReturn(bottom);
        when(event.getAction()).thenReturn(InventoryAction.MOVE_TO_OTHER_INVENTORY);
        when(event.getClick()).thenReturn(ClickType.SHIFT_LEFT);
        when(event.getRawSlot()).thenReturn(60);
        when(event.getSlot()).thenReturn(6);

        ItemPickerGuard guard = new ItemPickerGuard();
        guard.onInventoryClick(event);

        verify(event).setCancelled(true);
    }

    @Test
    void cancelsDoubleClickCollect() {
        Inventory top = mock(Inventory.class);
        ItemPickerGUI gui = mock(ItemPickerGUI.class);
        when(top.getHolder()).thenReturn(gui);
        when(top.getSize()).thenReturn(54);
        InventoryView view = mock(InventoryView.class);
        when(view.getTopInventory()).thenReturn(top);
        Inventory bottom = mock(Inventory.class);

        InventoryClickEvent event = mock(InventoryClickEvent.class);
        when(event.getView()).thenReturn(view);
        when(event.getClickedInventory()).thenReturn(bottom);
        when(event.getAction()).thenReturn(InventoryAction.COLLECT_TO_CURSOR);
        when(event.getClick()).thenReturn(ClickType.DOUBLE_CLICK);
        when(event.getRawSlot()).thenReturn(60);
        when(event.getSlot()).thenReturn(6);

        ItemPickerGuard guard = new ItemPickerGuard();
        guard.onInventoryClick(event);

        verify(event).setCancelled(true);
    }

    @Test
    void cancelsDragOverTop() {
        Inventory top = mock(Inventory.class);
        ItemPickerGUI gui = mock(ItemPickerGUI.class);
        when(top.getHolder()).thenReturn(gui);
        when(top.getSize()).thenReturn(54);
        InventoryView view = mock(InventoryView.class);
        when(view.getTopInventory()).thenReturn(top);

        InventoryDragEvent event = mock(InventoryDragEvent.class);
        when(event.getView()).thenReturn(view);
        when(event.getRawSlots()).thenReturn(java.util.Set.of(10, 20));
        when(event.getInventory()).thenReturn(top);

        ItemPickerGuard guard = new ItemPickerGuard();
        guard.onInventoryDrag(event);

        verify(event).setCancelled(true);
    }

    @Test
    void allowsDragOnlyInBottom() {
        Inventory top = mock(Inventory.class);
        ItemPickerGUI gui = mock(ItemPickerGUI.class);
        when(top.getHolder()).thenReturn(gui);
        when(top.getSize()).thenReturn(54);
        InventoryView view = mock(InventoryView.class);
        when(view.getTopInventory()).thenReturn(top);

        InventoryDragEvent event = mock(InventoryDragEvent.class);
        when(event.getView()).thenReturn(view);
        when(event.getRawSlots()).thenReturn(java.util.Set.of(60, 61));

        ItemPickerGuard guard = new ItemPickerGuard();
        guard.onInventoryDrag(event);

        verify(event, never()).setCancelled(true);
    }

    @Test
    void cancelsHotbarSwapInTop() {
        Inventory top = mock(Inventory.class);
        ItemPickerGUI gui = mock(ItemPickerGUI.class);
        when(top.getHolder()).thenReturn(gui);
        when(top.getSize()).thenReturn(54);
        InventoryView view = mock(InventoryView.class);
        when(view.getTopInventory()).thenReturn(top);

        InventoryClickEvent event = mock(InventoryClickEvent.class);
        when(event.getView()).thenReturn(view);
        when(event.getClickedInventory()).thenReturn(top);
        when(event.getAction()).thenReturn(InventoryAction.HOTBAR_SWAP);
        when(event.getClick()).thenReturn(ClickType.NUMBER_KEY);
        when(event.getRawSlot()).thenReturn(5);
        when(event.getSlot()).thenReturn(5);

        ItemPickerGuard guard = new ItemPickerGuard();
        guard.onInventoryClick(event);

        verify(event).setCancelled(true);
    }
}
