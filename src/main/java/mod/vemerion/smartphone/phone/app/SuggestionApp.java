package mod.vemerion.smartphone.phone.app;

import java.awt.Color;

import com.mojang.blaze3d.vertex.PoseStack;

import mod.vemerion.smartphone.Main;
import mod.vemerion.smartphone.ModInit;
import mod.vemerion.smartphone.phone.Phone;
import mod.vemerion.smartphone.phone.utils.PhoneUtils;
import net.minecraft.resources.ResourceLocation;

public class SuggestionApp extends App {
	
	private static final ResourceLocation ICON = new ResourceLocation("minecraft", "textures/item/paper.png");
	private static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID,
			"textures/gui/white_background.png");
	
	String text = "Do you have:any app:suggestions?:Share them:in the:comments:below!:::<3 Vemerion";
	
	String[] words = text.split(":");


	public SuggestionApp(Phone phone) {
		super(phone);
	}
	
	@Override
	public void tick() {
		super.tick();
		
		if (ticksExisted % 3 == 0 && ticksExisted < (text.length() - 9) * 2)
			phone.playSound(ModInit.WRITE.get(), 0.3f);
	}
	
	@Override
	public void render(PoseStack matrix) {
		super.render(matrix);
		int totalLetters = ticksExisted / 2;
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			int letters = Math.min(totalLetters, word.length());
			PhoneUtils.writeOnPhone(matrix, font, word.substring(0, letters), 5, 5 + i * 13, new Color(0, 0, 0), 1f, false);
			totalLetters -= letters;
			if (totalLetters == 0)
				break;
		}
	}

	@Override
	public ResourceLocation getIcon() {
		return ICON;
	}

	@Override
	public ResourceLocation getBackground() {
		return BACKGROUND;
	}

}
