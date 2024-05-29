package com.github.lileep.pixelmonnavigator.register;

import com.github.lileep.pixelmonnavigator.item.PixelmonNavigatorItem;
import com.github.lileep.pixelmonnavigator.lib.Reference;
import com.google.common.collect.Lists;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.function.Supplier;

public class ItemRegister {
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);

    public static final ItemEntry<PixelmonNavigatorItem> PIXELMON_NAVIGATOR = ItemEntry
            .register("pixelmon_navigator", PixelmonNavigatorItem::new);

    private ItemRegister() {
    }

    public static void init(IEventBus bus) {
        REGISTER.register(bus);
    }

    public static class ItemEntry<T extends Item> implements Supplier<T>, IItemProvider {

        public static final List<ItemEntry<? extends Item>> ALL_ITEMS = Lists.newArrayList();

        private final RegistryObject<T> regObject;

        private ItemEntry(RegistryObject<T> regObject) {
            this.regObject = regObject;
            ALL_ITEMS.add(this);
        }

        public static <T extends Item> ItemEntry<T> register(String name, Supplier<? extends T> make) {
            return new ItemEntry<>(REGISTER.register(name, make));
        }

        @Override
        public T get() {
            return this.regObject.get();
        }

        @Override
        public Item asItem() {
            return this.regObject.get();
        }

        public ResourceLocation getId() {
            return this.regObject.getId();
        }
    }
}
