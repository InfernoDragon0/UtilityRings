package net.infernodragon.utilityring.items;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class RingOfReverseHopper extends Item implements ICurioItem {

    int delay = 10;
    int currentDelay = 0;
    
    public RingOfReverseHopper(Properties properties) {
        super(properties
                .stacksTo(1)
                .durability(4096)
                .rarity(Rarity.EPIC));
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        pTooltipComponents.add(Component.translatable("tooltip.utilityring.ring_of_reverse_hopper"));
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (currentDelay < delay) {
            currentDelay++;
            return;
        }

        currentDelay = 0;

        if (slotContext.entity() instanceof Player) {
            Player player = ((Player) slotContext.entity());
            

            BlockEntity blockEntity = player.level().getBlockEntity(player.getOnPos());

            if (blockEntity instanceof BaseContainerBlockEntity) {
                BaseContainerBlockEntity baseContainerBlockEntity = (BaseContainerBlockEntity) blockEntity;
                int size = baseContainerBlockEntity.getContainerSize();

                for (int i = 0; i < size; i++) {
                    ItemStack itemStack = baseContainerBlockEntity.getItem(i);
                    if (!itemStack.isEmpty()) {
                        if (!player.getInventory().add(itemStack)) continue;
                        // baseContainerBlockEntity.removeItem(i, itemStack.getCount());
                        return;
                    }
                }
            }
        

            if (player.getKnownMovement().length() > 0 && player.isFallFlying()) {
                Vec3 movement = player.getKnownMovement();

                Vec3 newMovent = new Vec3(movement.x * 0.08F, 0, movement.z * 0.08F);

                if (!(newMovent.length() > 0.25F)) {
                    stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                    player.addDeltaMovement(newMovent);
                }

            }
                

        }
    }
}
