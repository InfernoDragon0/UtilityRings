package net.infernodragon.utilityring;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.infernodragon.utilityring.items.RingOfFlight;
import net.infernodragon.utilityring.items.RingOfRocketElytra;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(UtilityRingMod.MODID)
public class UtilityRingMod
{
    public static final String MODID = "utilityring";
    private static final Logger LOGGER = LogUtils.getLogger();

    // Registries
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    //Items
    public static final DeferredItem<Item> RING_OF_FLIGHT = ITEMS.registerItem("ring_of_flight", RingOfFlight::new, new Item.Properties());
    public static final DeferredItem<Item> RING_OF_ROCKET_ELYTRA = ITEMS.registerItem("ring_of_rocket_elytra", RingOfRocketElytra::new, new Item.Properties());

    // Creative Tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("rings", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.utilityring")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> RING_OF_FLIGHT.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(RING_OF_FLIGHT.get());
                output.accept(RING_OF_ROCKET_ELYTRA.get());
            }).build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public UtilityRingMod(IEventBus modEventBus, ModContainer modContainer)
    {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::registerCapabilities);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }


    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(
            CuriosCapability.ITEM,
            (stack, context) -> new ICurio() {

                @Override
                public ItemStack getStack() {
                  return stack;
                }

                @Override
                public void curioTick(SlotContext slotContext) {
                  // ticking logic here
                    if (slotContext.entity() instanceof Player) {
                        Player player = ((Player) slotContext.entity());
                        if (!player.mayFly()) {
                            // player.getAbilities().mayfly = true;
                            player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT).addTransientModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath("neoforge", "creative_flight"),1.0F, AttributeModifier.Operation.ADD_VALUE));
                            player.onUpdateAbilities();
                        }

                        if (player.getAbilities().flying) {
                            this.getStack().hurtAndBreak(1, player, null);
                        }
                    }
                }

                @Override
                public void onUnequip(SlotContext slotContext, ItemStack newStack) {
                    // This function gets also called when the data of the item changes.
                    // We have to avoid this.
                    if (newStack.getItem().getClass() == RingOfFlight.class) return;

                    LivingEntity livingEntity = slotContext.entity();
                    if (livingEntity instanceof Player) {
                        Player player = (Player) livingEntity;

                        if (player.isCreative() || player.isSpectator()) return;

                        player.getAbilities().flying = false;
                        // player.getAbilities().mayfly = false;
                        player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT).removeModifier(ResourceLocation.fromNamespaceAndPath("neoforge", "creative_flight"));

                        // player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT).removeModifier(ResourceLocation.tryParse("neoforgemod"));
                        player.onUpdateAbilities();
                    }
                }

                @Override
                public boolean canEquipFromUse(SlotContext slotContext) {
                    return true;
                }
            },
            UtilityRingMod.RING_OF_FLIGHT
            
        );
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
