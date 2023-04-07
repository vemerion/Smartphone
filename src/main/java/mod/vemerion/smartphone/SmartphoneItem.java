package mod.vemerion.smartphone;

import java.util.function.Consumer;

import mod.vemerion.smartphone.capability.PhoneState;
import mod.vemerion.smartphone.renderer.PhoneRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class SmartphoneItem extends Item {

	public SmartphoneItem() {
		super(new Item.Properties().stacksTo(1));
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 10;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
		if (entityLiving instanceof Player) {
			var player = (Player) entityLiving;
			if (!level.isClientSide) {
				player.getCapability(PhoneState.CAPABILITY).ifPresent(s -> {
					s.sendLoadStateMessage((ServerPlayer) player);
				});
			}
		}
		return stack;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
		var itemstack = pPlayer.getItemInHand(pHand);
		pPlayer.startUsingItem(pHand);
		return InteractionResultHolder.success(itemstack);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				var mc = Minecraft.getInstance();
				return new PhoneRenderer(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels());
			}
		});
	}
}
