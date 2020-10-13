package mod.vemerion.smartphone.phone.app;

import java.util.Random;

import mod.vemerion.smartphone.phone.Phone;
import mod.vemerion.smartphone.phone.utils.PhoneUtils;
import net.minecraft.util.ResourceLocation;

public abstract class App {
	
	protected int ticksExisted;
	protected Random rand = new Random();
	protected Phone phone;
	
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
		
	}
	
	public void shutdown() {
		
	}

}
