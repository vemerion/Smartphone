package mod.vemerion.smartphone;

import mod.vemerion.smartphone.capability.PhoneState;
import mod.vemerion.smartphone.renderer.PhoneRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class SmartphoneItem extends Item {

	public SmartphoneItem() {
		super(new Item.Properties().maxStackSize(1).group(ItemGroup.MISC).setISTER(() -> PhoneRenderer::new));
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 10;
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
		if (entityLiving instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entityLiving;
			if (!worldIn.isRemote) {
				player.getCapability(PhoneState.CAPABILITY).ifPresent(s -> {
					s.sendLoadStateMessage((ServerPlayerEntity) player);
				});
			}
		}
		return stack;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		playerIn.setActiveHand(handIn);
		return ActionResult.resultSuccess(itemstack);
	}

}
