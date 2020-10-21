package mod.vemerion.smartphone.phone.app;

import com.mojang.blaze3d.matrix.MatrixStack;

import mod.vemerion.smartphone.Main;
import mod.vemerion.smartphone.phone.Phone;
import mod.vemerion.smartphone.phone.utils.PhoneUtils;
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

	private static final int MAP_WIDTH = 64;
	private static final int MAP_HEIGHT = 128;

	private DynamicTexture mapTexture;
	private ResourceLocation map;

	public MapApp(Phone phone) {
		super(phone);
	}

	@Override
	public void startup() {
		super.startup();

		mapTexture = new DynamicTexture(MAP_WIDTH, MAP_HEIGHT, true);
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
		BlockPos topLeft = center.add(-MAP_WIDTH / 2, 0, -MAP_HEIGHT / 2);
		
		// Go throught nearby chunks
		for (int i = -16; i < MAP_WIDTH + 16; i += 16) {
			for (int j = -16; j < MAP_HEIGHT + 16; j += 16) {
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

							if (mapX >= 0 && mapX < MAP_WIDTH && mapY >= 0 && mapY < MAP_HEIGHT) {
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

	@Override
	public void render(MatrixStack matrix) {
		super.render(matrix);

		if (map != null)
			PhoneUtils.drawOnPhone(map, 0, 0, PhoneUtils.APP_WIDTH, PhoneUtils.APP_HEIGHT);

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
