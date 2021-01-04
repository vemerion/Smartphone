package mod.vemerion.smartphone.phone.app;

import com.mojang.blaze3d.matrix.MatrixStack;

import mod.vemerion.smartphone.Main;
import mod.vemerion.smartphone.phone.Phone;
import mod.vemerion.smartphone.phone.utils.Button;
import mod.vemerion.smartphone.phone.utils.PhoneUtils;
import mod.vemerion.smartphone.phone.utils.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;

public class MapApp extends App {
	private static final ResourceLocation ICON = new ResourceLocation("textures/item/compass_00.png");
	private static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID,
			"textures/gui/white_background.png");
	private static final ResourceLocation PLUS = new ResourceLocation(Main.MODID,
			"textures/gui/map_app/plus.png");
	private static final ResourceLocation MINUS = new ResourceLocation(Main.MODID,
			"textures/gui/map_app/minus.png");

	private static final int MAP_WIDTH = 32;
	private static final int MAP_HEIGHT = 64;
	private static final int MAX_SCALE = 5;

	private DynamicTexture mapTexture;
	private ResourceLocation map;
	private int scale = 1;
	private Button plusButton;
	private Button minusButton;

	public MapApp(Phone phone) {
		super(phone);
		
		plusButton = new Button(new Rectangle(PhoneUtils.APP_WIDTH / 2 + 5, PhoneUtils.APP_HEIGHT * 0.88f, 20),
				() -> PLUS, phone, () -> {
					if (scale > 1) {
						scale--;
						drawMap();
					}
				});
		minusButton = new Button(new Rectangle(PhoneUtils.APP_WIDTH / 2 - 25, PhoneUtils.APP_HEIGHT * 0.88f, 20),
				() -> MINUS, phone, () -> {
					if (scale < MAX_SCALE) {
						scale++;
						drawMap();
					}
				});
	}

	@Override
	public void startup() {
		super.startup();

		drawMap();
	}
	
	private void drawMap() {
		mapTexture = new DynamicTexture(mapWidth(), mapHeight(), true);
		fillMapTexture();

		map = Minecraft.getInstance().getTextureManager().getDynamicTextureLocation(Main.MODID + "map_app_texture",
				mapTexture);
		mapTexture.updateDynamicTexture();
	}

	@Override
	public void shutdown() {
		super.shutdown();
		mapTexture.close();
	}

	private void fillMapTexture() {
		BlockPos center = Minecraft.getInstance().player.getPosition();
		BlockPos topLeft = center.add(-mapWidth() / 2, 0, -mapHeight() / 2);
		
		// Go through nearby chunks
		for (int i = -16; i < mapWidth() + 16; i += 16) {
			for (int j = -16; j < mapHeight() + 16; j += 16) {
				int chunkX = topLeft.getX() + i;
				int chunkZ = topLeft.getZ() + j;
				BlockPos pos = new BlockPos(chunkX, center.getY(), chunkZ);
				Chunk chunk = Minecraft.getInstance().world.getChunkAt(pos);
				if (!chunk.isEmpty()) {
					
					// Go through blocks in chunk
					for (int k = 0; k < 16; k++) {
						for (int l = 0; l < 16; l++) {
							int x = chunk.getPos().x * 16 + k;
							int y = chunk.getPos().z * 16 + l;
							int mapX = x - topLeft.getX();
							int mapY = y - topLeft.getZ();

							if (mapX >= 0 && mapX < mapWidth() && mapY >= 0 && mapY < mapHeight()) {
								int height = chunk.getTopBlockY(Heightmap.Type.WORLD_SURFACE, k, l);
								BlockPos blockPos = new BlockPos(x, height, y);
								int color = chunk.getBlockState(blockPos).getMaterial().getColor().getMapColor(1);
								mapTexture.getTextureData().setPixelRGBA(mapX, mapY, color);
							}
						}
					}
				}
			}
		}
	}
	
	private int mapWidth() {
		return MAP_WIDTH * scale;
	}
	
	private int mapHeight() {
		return MAP_HEIGHT * scale;
	}
	
	@Override
	public void tick() {
		super.tick();
		plusButton.tick();
		minusButton.tick();
	}

	@Override
	public void render(MatrixStack matrix) {
		super.render(matrix);

		if (map != null)
			PhoneUtils.drawOnPhone(map, 0, 0, PhoneUtils.APP_WIDTH, PhoneUtils.APP_HEIGHT);
		
		plusButton.render();
		minusButton.render();

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
