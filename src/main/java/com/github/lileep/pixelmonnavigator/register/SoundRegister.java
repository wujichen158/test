package com.github.lileep.pixelmonnavigator.register;

import com.github.lileep.pixelmonnavigator.lib.Reference;
import com.pixelmonmod.pixelmon.api.util.helpers.ResourceLocationHelper;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SoundRegister {
    public static final DeferredRegister<SoundEvent> REGISTER = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Reference.MOD_ID);

    public static final RegistryObject<SoundEvent> ELEM_CLICK = register("pixelmonnavigator.gui.elem_click");

    private SoundRegister() {
    }

    public static void init(IEventBus bus) {
        REGISTER.register(bus);
    }

    private static RegistryObject<SoundEvent> register(String location) {
        SoundEvent event = new SoundEvent(ResourceLocationHelper.of(Reference.MOD_ID, location));
        return REGISTER.register(location, () -> event);
    }
}
