package net.infernodragon.utilityring.items;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;

public class RingOfFlight extends Item {

    public RingOfFlight(Properties properties) {
        super(properties
                .stacksTo(1)
                .durability(256)
                .rarity(Rarity.RARE));
    }
    
    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        pTooltipComponents.add(Component.translatable("tooltip.utilityring.ring_of_flight"));
    }
}
