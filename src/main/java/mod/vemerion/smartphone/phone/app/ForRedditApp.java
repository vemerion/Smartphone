package mod.vemerion.smartphone.phone.app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import mod.vemerion.smartphone.Main;
import mod.vemerion.smartphone.phone.Phone;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class ForRedditApp extends App {

	private static final Logger LOGGER = LogManager.getLogger();

	private static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID,
			"textures/gui/white_background.png");
	private static final ResourceLocation ICON = new ResourceLocation(Main.MODID,
			"textures/gui/for_reddit_app/icon.png");
	private static final long HOUR = 1000 * 60 * 60;

	private UUID id;
	private String token;
	private long timestamp;
	private ConnectionThread thread;

	public ForRedditApp(Phone phone) {
		super(phone);
	}

	@Override
	public ResourceLocation getIcon() {
		return ICON;
	}

	@Override
	public ResourceLocation getBackground() {
		return BACKGROUND;
	}

	@Override
	public void resume() {
		if (id == null)
			id = UUID.randomUUID();
		thread = new ConnectionThread(token, id, timestamp);
		thread.start();
	}

	@Override
	public void tick() {
		super.tick();

		if (thread != null && !thread.isAlive()) {
			token = thread.token;
			timestamp = thread.timestamp;
			thread = null;
		}
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT compound = new CompoundNBT();
		if (id != null)
			compound.putUniqueId("id", id);
		if (token != null) {
			compound.putString("token", token);
			compound.putLong("timestamp", timestamp);
		}
		return compound;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		if (nbt.hasUniqueId("id"))
			id = nbt.getUniqueId("id");
		if (nbt.contains("token")) {
			token = nbt.getString("token");
			timestamp = nbt.getLong("timestamp");
		}
	}

	private static class ConnectionThread extends Thread {

		private static final String TOKEN_URL = "https://www.reddit.com/api/v1/access_token";
		private static final String APP_ID = "Bajzj3AxnbwURg:";
		private static final String ATTRIBUTES = "grant_type=https://oauth.reddit.com/grants/installed_client&device_id=";
		private static final String MINECRAFT_SUBREDDIT = "https://oauth.reddit.com/r/minecraft/";
		private static final String USER_AGENT = "minecraft:mod.vemerion.smartphone:v1.2.0 (by /u/vemerion)";

		private String token;
		private UUID id;
		private long timestamp;

		private ConnectionThread(String token, UUID id, long timestamp) {
			this.token = token;
			this.id = id;
			this.timestamp = timestamp;
		}

		@Override
		public void run() {
			try {
				connect();
			} catch (Exception e) {
				LOGGER.error("Could not connect to Reddit: " + e.getMessage());
			}
		}

		private void connect() throws Exception {
			if (token == null || System.currentTimeMillis() - timestamp > HOUR) {
				System.out.println("REQUESTING TOKEN" + " " + token + " " + (System.currentTimeMillis() - timestamp));
				HttpURLConnection connection = (HttpURLConnection) new URL(TOKEN_URL).openConnection();
				connection.setRequestMethod("POST");
				byte[] encodedAuth = Base64.getEncoder().encode(APP_ID.getBytes(StandardCharsets.UTF_8));
				String authHeaderValue = "Basic " + new String(encodedAuth);
				connection.setRequestProperty("Authorization", authHeaderValue);
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				connection.setRequestProperty("User-Agent", USER_AGENT);
				connection.setDoOutput(true);
				OutputStream os = connection.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
				osw.write(ATTRIBUTES + id.toString());
				osw.flush();
				osw.close();
				os.close();

				Integer responseCode = connection.getResponseCode();
				System.out.println("Response Code : " + responseCode);

				if (responseCode == HttpURLConnection.HTTP_OK) {
					BufferedReader inputReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String inputLine;
					StringBuffer response = new StringBuffer();

					while ((inputLine = inputReader.readLine()) != null) {
						response.append(inputLine);
					}

					inputReader.close();
					JsonObject json = new JsonParser().parse(response.toString()).getAsJsonObject();
					token = JSONUtils.getString(json, "access_token");
					timestamp = System.currentTimeMillis();
					System.out.println("TOKEN: " + token);
				}
			}

			if (token != null) {
				HttpURLConnection connection = (HttpURLConnection) new URL(MINECRAFT_SUBREDDIT).openConnection();
				connection.setRequestProperty("Authorization", "Bearer " + token);
				connection.setRequestProperty("User-Agent", USER_AGENT);
				connection.setRequestMethod("GET");

				int responseCode = connection.getResponseCode();
				System.out.println("Response Code : " + responseCode);

				if (responseCode == HttpURLConnection.HTTP_OK) {
					BufferedReader inputReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String inputLine;
					StringBuffer response = new StringBuffer();

					while ((inputLine = inputReader.readLine()) != null) {
						response.append(inputLine);
					}

					inputReader.close();
					System.out.println(response.toString());
				}
			}
		}
	}

}
