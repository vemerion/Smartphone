package mod.vemerion.smartphone.phone.app;

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;

import mod.vemerion.smartphone.phone.Phone;
import mod.vemerion.smartphone.phone.utils.PhoneUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class App implements INBTSerializable<CompoundTag> {
	
	protected int ticksExisted;
	protected Random rand = new Random();
	protected Phone phone;
	protected Font font;
	
	public App(Phone phone) {
		this.phone = phone;
	}
	
	public abstract ResourceLocation getIcon();
	
	public abstract ResourceLocation getBackground();
	
	public void tick() {
		ticksExisted++;
	}
	
	public void render(PoseStack matrix) {
		PhoneUtils.drawOnPhone(getBackground(), 0, 0, PhoneUtils.APP_WIDTH, PhoneUtils.APP_HEIGHT);
	}
	
	public void suspend() {
	}
	
	public void resume() {
	}

	// Is called for example when going from or to fullscreen
	public void startup() {
		this.font = phone.getFont();
	}
	
	public void shutdown() {
		
	}
	
	@Override
	public CompoundTag serializeNBT() {
		return new CompoundTag();
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {		
	}

}
