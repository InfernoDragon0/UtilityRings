package net.infernodragon.utilityring.items;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class RingOfLife extends Item implements ICurioItem {
    
    public RingOfLife(Properties properties) {
        super(properties
                .stacksTo(1)
                .durability(4)
                .rarity(Rarity.EPIC));
    }

    public boolean checkTotemUsage(DamageSource pDamageSource, Player player, ItemStack pStack) {
        if (pDamageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        player.setHealth(1.0F);
        player.removeEffectsCuredBy(net.neoforged.neoforge.common.EffectCures.PROTECTED_BY_TOTEM);
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
        player.level().broadcastEntityEvent(player, (byte)35);

        pStack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);

        return true;
    }


    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        pTooltipComponents.add(Component.translatable("tooltip.utilityring.ring_of_life"));
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

}
