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

public class RingOfHopper extends Item implements ICurioItem {

    int delay = 10;
    int currentDelay = 0;
    
    public RingOfHopper(Properties properties) {
        super(properties
                .stacksTo(1)
                .durability(4096)
                .rarity(Rarity.EPIC));
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        pTooltipComponents.add(Component.translatable("tooltip.utilityring.ring_of_hopper"));
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
                int size = player.getInventory().items.size();
                int containerSize = baseContainerBlockEntity.getContainerSize();

                for (int i = 0; i < size; i++) {
                    ItemStack itemStack = player.getInventory().items.get(i);
                    if (itemStack.isEmpty()) continue;

                    for (int j = 0; j < containerSize; j++) {
                        if (baseContainerBlockEntity.getItem(j).isEmpty()) {
                            ItemStack newItemStack = itemStack.copyAndClear();
                            baseContainerBlockEntity.setItem(j, newItemStack);
                            return;
                        }
                        else if (baseContainerBlockEntity.getItem(j).getItem() == itemStack.getItem() && baseContainerBlockEntity.getItem(j).getCount() < baseContainerBlockEntity.getItem(j).getMaxStackSize()) {
                            if (baseContainerBlockEntity.getItem(j).getCount() + itemStack.getCount() <= baseContainerBlockEntity.getItem(j).getMaxStackSize()) {
                                baseContainerBlockEntity.getItem(j).grow(itemStack.getCount());
                                itemStack.setCount(0);
                            }
                            else {
                                int amount = baseContainerBlockEntity.getItem(j).getMaxStackSize() - baseContainerBlockEntity.getItem(j).getCount();
                                itemStack.shrink(amount);
                                baseContainerBlockEntity.getItem(j).grow(amount);
                            }
                            return;
                        }
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
