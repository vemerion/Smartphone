package mod.vemerion.smartphone.phone.app;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import mod.vemerion.smartphone.Main;
import mod.vemerion.smartphone.phone.Phone;
import mod.vemerion.smartphone.phone.utils.Button;
import mod.vemerion.smartphone.phone.utils.PhoneUtils;
import mod.vemerion.smartphone.phone.utils.Rectangle;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class ForRedditApp extends App {

	private static final Logger LOGGER = LogManager.getLogger();

	private static final String TOKEN_URL = "https://www.reddit.com/api/v1/access_token";
	private static final String APP_ID = "Bajzj3AxnbwURg:";
	private static final String ATTRIBUTES = "grant_type=https://oauth.reddit.com/grants/installed_client&device_id=";
	private static final String REDDIT = "https://oauth.reddit.com";
	private static final String MINECRAFT_SUBREDDIT = REDDIT + "/r/minecraft/";
	private static final String USER_AGENT = "minecraft:mod.vemerion.smartphone:v1.2.0 (by /u/vemerion)";

	private static final int TOP_OFFSET = (int) (PhoneUtils.APP_HEIGHT * 0.17);

	private static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID,
			"textures/gui/for_reddit_app/background.png");
	private static final ResourceLocation ICON = new ResourceLocation(Main.MODID,
			"textures/gui/for_reddit_app/icon.png");
	private static final ResourceLocation LEFT_BUTTON = new ResourceLocation(Main.MODID,
			"textures/gui/left_button.png");
	private static final ResourceLocation RIGHT_BUTTON = new ResourceLocation(Main.MODID,
			"textures/gui/right_button.png");
	private static final ResourceLocation DOWN_BUTTON = new ResourceLocation(Main.MODID,
			"textures/gui/down_button.png");
	private static final ResourceLocation UP_BUTTON = new ResourceLocation(Main.MODID, "textures/gui/up_button.png");
	private static final ResourceLocation LINE = new ResourceLocation(Main.MODID,
			"textures/gui/for_reddit_app/line.png");

	private static final long HOUR = 1000 * 60 * 60;

	private UUID id;
	private String token;
	private long timestamp;
	private ConnectionThread thread;
	List<List<Post>> pages;
	App subApp;
	int page;
	private Button leftButton;
	private Button rightButton;

	public ForRedditApp(Phone phone) {
		super(phone);
		pages = new ArrayList<>();

		rightButton = new Button(new Rectangle(PhoneUtils.APP_WIDTH / 2 + 10, PhoneUtils.APP_HEIGHT * 0.9f, 20),
				() -> RIGHT_BUTTON, phone, () -> {
					if (page < pages.size() - 1)
						page++;
				});
		leftButton = new Button(new Rectangle(PhoneUtils.APP_WIDTH / 2 - 30, PhoneUtils.APP_HEIGHT * 0.9f, 20),
				() -> LEFT_BUTTON, phone, () -> {
					if (page > 0)
						page--;
				});
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
		subApp = null;
		pages = new ArrayList<>();
		page = 0;
		if (id == null)
			id = UUID.randomUUID();
		thread = new ConnectionThread(token, id, timestamp);
		thread.start();
	}

	@Override
	public void tick() {
		super.tick();

		if (subApp != null) {
			subApp.tick();
		} else {
			if (thread != null && !thread.isAlive()) {
				token = thread.token;
				timestamp = thread.timestamp;
				if (thread.hasData()) {
					createPosts(thread.getData());
				}
				thread = null;
			}

			if (!pages.isEmpty())
				for (Post p : pages.get(page))
					p.tickButton();
			leftButton.tick();
			rightButton.tick();
		}
	}

	@Override
	public void render() {
		super.render();

		if (subApp != null) {
			subApp.render();
		} else {

			PhoneUtils.writeOnPhone(font, "For Reddit:", PhoneUtils.APP_WIDTH / 2, 2, Color.WHITE, 1, true);
			PhoneUtils.writeOnPhone(font, "Minecraft", PhoneUtils.APP_WIDTH / 2, 14, Color.WHITE, 1, true);

			if (!pages.isEmpty())
				for (Post p : pages.get(page))
					p.renderButton();
			leftButton.render();
			rightButton.render();
		}
	}

	private void createPosts(String data) {
		JsonObject json = JSONUtils.getJsonObject(JSONUtils.fromJson(data), "data");
		pages = new ArrayList<>();
		pages.add(new ArrayList<>());
		int i = 0;
		int y = TOP_OFFSET;
		for (JsonElement e : JSONUtils.getJsonArray(json, "children")) {
			JsonObject o = JSONUtils.getJsonObject(JSONUtils.getJsonObject(e, "post"), "data");
			String title = JSONUtils.getString(o, "title");
			int height = PhoneUtils.textHeight(font, title, 0.5f, PhoneUtils.APP_WIDTH - 2) + 10;
			if (y + height > PhoneUtils.APP_HEIGHT * 0.9) {
				pages.add(new ArrayList<>());
				i++;
				y = TOP_OFFSET;
			}
			pages.get(i).add(new Post(phone, JSONUtils.getString(o, "title"), y, JSONUtils.getString(o, "selftext"),
					JSONUtils.getString(o, "permalink")));
			y += height;
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

	private class Post extends App {
		private Button button;
		private String title;
		private String selftext;
		private String permalink;
		private List<String> comments;
		private PostThread thread;
		private Button backButton;
		private Button downButton;
		private Button upButton;
		private int down;

		public Post(Phone phone, String title, int y, String selftext, String permalink) {
			super(phone);
			startup();
			this.title = title;
			this.selftext = selftext;
			this.permalink = permalink;
			this.comments = new ArrayList<>();
			this.button = new PostButton(
					new Rectangle(0, y, PhoneUtils.APP_WIDTH,
							PhoneUtils.textHeight(font, title, 0.5f, PhoneUtils.APP_WIDTH - 2)),
					null, phone, () -> enterPost(), title, font);
			this.backButton = new Button(new Rectangle(2, 2, 20), () -> LEFT_BUTTON, phone, () -> {
				subApp = null;
			});

			this.downButton = new Button(new Rectangle(PhoneUtils.APP_WIDTH / 2 + 10, PhoneUtils.APP_HEIGHT * 0.9f, 20),
					() -> DOWN_BUTTON, phone, () -> {
						down++;
					});
			this.upButton = new Button(new Rectangle(PhoneUtils.APP_WIDTH / 2 - 30, PhoneUtils.APP_HEIGHT * 0.9f, 20),
					() -> UP_BUTTON, phone, () -> {
						if (down > 0)
							down--;
					});
		}

		private void enterPost() {
			subApp = this;
			down = 0;
			thread = new PostThread(token, permalink);
			thread.start();
		}

		@Override
		public void tick() {
			super.tick();

			backButton.tick();
			upButton.tick();
			downButton.tick();

			if (thread != null && !thread.isAlive()) {
				if (thread.hasData()) {
					createComments(thread.getData());
				}
				thread = null;
			}
		}

		private void createComments(String data) {
			JsonArray jsonArray = JSONUtils.fromJson(new GsonBuilder().create(), new StringReader(data),
					JsonArray.class, false);
			JsonObject json = JSONUtils.getJsonObject(JSONUtils.getJsonObject(jsonArray.get(1), "comments"), "data");
			comments = new ArrayList<>();
			for (JsonElement e : JSONUtils.getJsonArray(json, "children")) {
				JsonObject o = JSONUtils.getJsonObject(JSONUtils.getJsonObject(e, "comment"), "data");
				if (o.has("body")) {
					String body = JSONUtils.getString(o, "body");
					comments.add(body);
				}
			}
		}

		@Override
		public void render() {
			super.render();

			backButton.render();
			upButton.render();
			downButton.render();

			PhoneUtils.writeOnPhoneTrim(font, title, 25, 6, Color.BLACK, 1f, PhoneUtils.APP_WIDTH - 25, false, false);

			int y = TOP_OFFSET - down * 50;

			if (!selftext.isEmpty()) {
				List<String> lines = font.listFormattedStringToWidth(selftext,
						(int) (PhoneUtils.fromVirtualWidth(PhoneUtils.APP_WIDTH - 2) / 0.6f));
				for (String line : lines) {
					if (y >= TOP_OFFSET && y < PhoneUtils.APP_HEIGHT * 0.85)
						PhoneUtils.writeOnPhone(font, line, 1, y, Color.BLACK, 0.6f, false);
					y += PhoneUtils.textHeight(font, line, 0.6f, PhoneUtils.APP_WIDTH - 2) * 1.2f;
				}
				
				if (y >= TOP_OFFSET &&y < PhoneUtils.APP_HEIGHT * 0.85)
					PhoneUtils.drawOnPhone(LINE, 0, y + 5, PhoneUtils.APP_WIDTH, 2);
				y += 10;
			}

			for (String c : comments) {
				List<String> lines = font.listFormattedStringToWidth(c,
						(int) (PhoneUtils.fromVirtualWidth(PhoneUtils.APP_WIDTH - 2) / 0.5f));
				for (String line : lines) {
					if (y >= TOP_OFFSET && y < PhoneUtils.APP_HEIGHT * 0.85)
						PhoneUtils.writeOnPhone(font, line, 1, y, Color.BLACK, 0.5f, false);
					y += PhoneUtils.textHeight(font, line, 0.5f, PhoneUtils.APP_WIDTH - 2) * 1.2f;
				}
				if (y > PhoneUtils.APP_HEIGHT * 0.85)
					break;
				
				if (y >= TOP_OFFSET)
				PhoneUtils.drawOnPhone(LINE, 0, y + 5, PhoneUtils.APP_WIDTH, 2);
				y += 10;
			}
		}

		private void tickButton() {
			button.tick();
		}

		private void renderButton() {
			button.render();
		}

		@Override
		public ResourceLocation getIcon() {
			return null;
		}

		@Override
		public ResourceLocation getBackground() {
			return BACKGROUND;
		}

	}

	private static class PostButton extends Button {

		private String title;
		private FontRenderer font;

		public PostButton(Rectangle rectangle, Supplier<ResourceLocation> icon, Phone phone, Runnable runnable,
				String title, FontRenderer font) {
			super(rectangle, icon, phone, runnable);
			this.title = title;
			this.font = font;
		}

		@Override
		public void render() {
			Color c = rectangle.contains(phone.getMouseX(), phone.getMouseY()) ? Color.BLUE : Color.BLACK;
			PhoneUtils.writeOnPhoneWrap(font, title, 1, rectangle.y, c, 0.5f, PhoneUtils.APP_WIDTH - 2, false);
			PhoneUtils.drawOnPhone(LINE, 0, rectangle.y + rectangle.height + 5, PhoneUtils.APP_WIDTH, 2);
		}
	}

	private static abstract class RedditThread extends Thread {

		protected String token;
		private String data;

		protected RedditThread(String token) {
			this.token = token;
		}

		@Override
		public void run() {
			try {
				connect();
			} catch (Exception e) {
				LOGGER.error("Could not connect to Reddit: " + e.getMessage());
			}
		}

		protected abstract void connect() throws Exception;

		protected String getResponse(HttpURLConnection conn) throws IOException {
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = inputReader.readLine()) != null) {
				response.append(inputLine);
			}

			inputReader.close();
			return response.toString();
		}

		public void setData(String data) {
			this.data = data;
		}

		public String getData() {
			return data;
		}

		public boolean hasData() {
			return data != null;
		}
	}

	private static class ConnectionThread extends RedditThread {

		private UUID id;
		private long timestamp;

		private ConnectionThread(String token, UUID id, long timestamp) {
			super(token);
			this.id = id;
			this.timestamp = timestamp;
		}

		@Override
		protected void connect() throws Exception {
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
					JsonObject json = new JsonParser().parse(getResponse(connection)).getAsJsonObject();
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
					String response = getResponse(connection);
					System.out.println(response);
					setData(response);
				}
			}
		}
	}

	private static class PostThread extends RedditThread {

		private String permalink;

		private PostThread(String token, String permalink) {
			super(token);
			this.permalink = permalink;
		}

		@Override
		protected void connect() throws Exception {
			if (token != null) {
				HttpURLConnection connection = (HttpURLConnection) new URL(REDDIT + permalink).openConnection();
				connection.setRequestProperty("Authorization", "Bearer " + token);
				connection.setRequestProperty("User-Agent", USER_AGENT);
				connection.setRequestMethod("GET");

				int responseCode = connection.getResponseCode();
				System.out.println("Response Code : " + responseCode);

				if (responseCode == HttpURLConnection.HTTP_OK) {
					String response = getResponse(connection);
					System.out.println(response.toString());
					setData(response);
				}
			}
		}
	}

}
