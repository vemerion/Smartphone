package mod.vemerion.smartphone.phone.app;

import java.util.Random;

import mod.vemerion.smartphone.phone.Phone;
import mod.vemerion.smartphone.phone.utils.PhoneUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class App implements INBTSerializable<CompoundNBT> {
	
	protected int ticksExisted;
	protected Random rand = new Random();
	protected Phone phone;
	protected FontRenderer font;
	
	public App(Phone phone) {
		this.phone = phone;
	}
	
	public abstract ResourceLocation getIcon();
	
	public abstract ResourceLocation getBackground();
	
	public void tick() {
		ticksExisted++;
	}
	
	public void render() {
		PhoneUtils.drawOnPhone(getBackground(), 0, 0, PhoneUtils.APP_WIDTH, PhoneUtils.APP_HEIGHT);
	}
	
	public void suspend() {
	}
	
	public void resume() {
	}

	public void startup() {
		this.font = phone.getFont();
	}
	
	public void shutdown() {
		
	}
	
	@Override
	public CompoundNBT serializeNBT() {
		return new CompoundNBT();
	}
	
	@Override
	public void deserializeNBT(CompoundNBT nbt) {		
	}

}
