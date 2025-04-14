package com.untiedgames.TileBeanEngine.Demo;

import java.util.ArrayList;
import java.util.Optional;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.untiedgames.TileBeanEngine.*;
import com.untiedgames.TileBeanEngine.AssetSystem.*;
import com.untiedgames.TileBeanEngine.AssetSystem.TilemapAsset.*;
import com.untiedgames.TileBeanEngine.AssetSystem.TilesetAsset.*;

import imgui.ImGui;

public class DemoBasicGame extends Game {

	/**
	 * This class is a whole-screen effect which fades to or from black.
	 */
	class FadeTransition extends Drawable {

		private float time = 0;
		private float duration = 2;
		private float direction = 1;

		public void start(float direction) {
			this.direction = direction;
			if (direction > 0.0f) time = 0;
			else time = duration;
		}

		public float getDirection() {
			return direction;
		}

		public boolean isDone() {
			if (direction == 1.0f) return time >= duration;
			else return time <= 0.0f;
		}

		public void update(float delta) {
			time += direction * delta;
		}

		public void draw(SpriteBatch spritebatch) {
			spritebatch.end();
			ShapeRenderer shaperenderer = TileBeanEngine.getShapeRenderer();
			shaperenderer.setColor(0, 0, 0, time / duration);
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

			shaperenderer.begin();
			shaperenderer.set(ShapeType.Filled);
			shaperenderer.rect(0, 0, TileBeanEngine.getResolutionX(), TileBeanEngine.getResolutionY());
			shaperenderer.end();
			
			Gdx.gl.glDisable(GL20.GL_BLEND);
			spritebatch.begin();
		}
		
	}

	// Variables which keep track of entities we care about in the game world.
	Object2DHandle obj_tilemap_handle;
	Object2DHandle obj_player_handle;
	Object2DHandle obj_flag_handle;
	ArrayList<Object2DHandle> obj_coin_handles = new ArrayList<>();
	
	// The gravity to apply to the player and other game objects which should fall (if there are any).
	float gravity = 20;
	
	// Character stats, in pixels per second.
	float player_move_speed = 80;
	float player_jump_strength = 6;
	
	// Player state
	float player_velocity_y = 0; // The current Y velocity of the player. Gravity affects this, and it also changes when the player jumps.
	boolean player_is_on_ground = false; // Whether or not the player is considered to be on the ground. The player can only jump when they're on the ground*. (*See "coyote time" below.)
	boolean player_space_released_after_jump = false; // Whether or not the spacebar was released after a jump was performed. Without this, the player could hold spacebar and continue to jump when they hit the ground.
	boolean player_ignore_jump = false; // Whether or not to ignore a jump input. Without this, the player could press spacebar while falling, hit the ground, and then release it to jump. This forces an extra press.
	
	// "Coyote time" allows the player to jump even when they've recently left a platform, similar the classic cartoon character Wile E. Coyote dashing off a cliff and thinking he's still on the ground.
	// See: https://en.wikipedia.org/wiki/Glossary_of_video_game_terms#coyote_time
	float coyote_time = 0; // The current amount of coyote time remaining.
	float coyote_time_max = 10.0f / 60.0f; // Equivalent to roughly 10 frames, or 0.166 seconds.
	
	// Stores the victory "wow" sound effect ID.
	long sfx_wow_id = -1;
	
	// Variables which manage the state of the game.
	boolean win_condition = false;
	boolean loss_condition = false;
	boolean reset_game = false; // Controls the state of the game.
	
	// Timer before ending the level if the player wins or loses.
	float end_level_time = 0;
	float end_level_time_max = 1.5f;

	public void initialize() {
		////////////////////////
		// Load game graphics //
		////////////////////////
		
		TextureAsset tex_asset_char_idle = new TextureAsset("char_idle", "gfx/character/idle.anim");
		tex_asset_char_idle.load();
		TileBeanEngine.assets.add(tex_asset_char_idle);

		TextureAsset tex_asset_char_run = new TextureAsset("char_run", "gfx/character/run.anim");
		tex_asset_char_run.load();
		TileBeanEngine.assets.add(tex_asset_char_run);

		TextureAsset tex_asset_char_wow = new TextureAsset("char_wow", "gfx/character/wow.anim");
		tex_asset_char_wow.load();
		TileBeanEngine.assets.add(tex_asset_char_wow);

		TextureAsset tex_asset_char_jump = new TextureAsset("char_jump", "gfx/character/jump.png");
		tex_asset_char_jump.load();
		TileBeanEngine.assets.add(tex_asset_char_jump);

		TextureAsset tex_asset_flag = new TextureAsset("flag", "gfx/flag.anim");
		tex_asset_flag.load();
		TileBeanEngine.assets.add(tex_asset_flag);

		TextureAsset tex_asset_coin = new TextureAsset("coin", "gfx/coin/coin.anim");
		tex_asset_coin.load();
		TileBeanEngine.assets.add(tex_asset_coin);

		TextureAsset tex_asset_coin_collected = new TextureAsset("coin_collected", "gfx/coin/coin_collected.anim");
		tex_asset_coin_collected.load();
		TileBeanEngine.assets.add(tex_asset_coin_collected);

		/////////////////////
		// Load game audio //
		/////////////////////

		SoundAsset sound_asset_wow = new SoundAsset("sound_wow", "sound/wow.ogg");
		sound_asset_wow.load();
		TileBeanEngine.assets.add(sound_asset_wow);

		SoundAsset sound_asset_jump = new SoundAsset("sound_jump", "sound/jump.ogg");
		sound_asset_jump.load();
		TileBeanEngine.assets.add(sound_asset_jump);

		SoundAsset sound_asset_die = new SoundAsset("sound_die", "sound/die.ogg");
		sound_asset_die.load();
		TileBeanEngine.assets.add(sound_asset_die);

		SoundAsset sound_asset_coin = new SoundAsset("sound_coin", "sound/coin.ogg");
		sound_asset_coin.load();
		TileBeanEngine.assets.add(sound_asset_coin);

		//////////////////////////////
		// Load tileset and tilemap //
		//////////////////////////////

		TilesetAsset tileset_asset = new TilesetAsset("tileset", "map/grasslands_tileset/grasslands_tileset.tsx");
		tileset_asset.load();
		TileBeanEngine.assets.add(tileset_asset);

		TilemapAsset tilemap_asset = new TilemapAsset("tilemap", "map/game_demo_map.tmx");
		tilemap_asset.load();
		TileBeanEngine.assets.add(tilemap_asset);

		//////////////////////////////////
		// Verify all assets are loaded //
		//////////////////////////////////
		
		if (!TileBeanEngine.assets.allAssetsLoaded()) {
			throw new Error("Failed to load one or more assets for the demo.");
		}
		
		/////////////////
		// Level setup //
		/////////////////
		
		setupLevel();
	}

	private void setupLevel() {
		// Reset some player variables (This method is called when we start up, but also when the player wins or loses.)
		sfx_wow_id = -1;
		end_level_time = 0;
		player_velocity_y = 0;
		obj_coin_handles.clear();

		TilesetAssetHandle tileset_handle = TileBeanEngine.assets.getTilesetAssetHandle("tileset");
		TilemapAsset tilemap_asset = TileBeanEngine.assets.getTilemapAsset("tilemap").get();

		// The level for this demo has multiple tile layers.
		float layer_depth = -tilemap_asset.getNumLayers();
		for (int i = 0; i < tilemap_asset.getNumLayers(); i++) {
			// Create a new game object to represent this tile layer
			Object2D obj_tilemap = new Object2D();
			Object2DHandle handle = TileBeanEngine.world.add(obj_tilemap);
			obj_tilemap.z = layer_depth + i;

			// Add a Tilemap component. Tilemaps can display a grid of tiles from a tileset.
			Optional<Tilemap> opt_tilemap = tilemap_asset.getLayer(i);
			if (!opt_tilemap.isPresent()) throw new Error("Failed to load demo tilemap.");
			Tilemap tilemap = opt_tilemap.get();
			tilemap.setTileset(tileset_handle);
			TileBeanEngine.world.addComponent(handle, tilemap);

			if (tilemap_asset.getLayerName(i).equals("terrain")) {
				// We want to use the terrain layer for collision, so we'll save its handle.
				obj_tilemap_handle = handle;
			}
		}
		
		// Place objects in the level.
		// We'll do this by iterating all of the TilemapObjects in the tilemap asset, and handling them based on their names.
		TilemapObject[] tilemap_objects = tilemap_asset.getObjects();
		for (TilemapObject t_obj : tilemap_objects) {
			if (t_obj.getName().equals("Player")) {
				obj_player_handle = createPlayer(t_obj);
			} else if (t_obj.getName().equals("Flag")) {
				obj_flag_handle = createFlag(t_obj);
			} else if (t_obj.getName().equals("Coin")) {
				createCoin(t_obj);
			}
		}

		// Set the camera for the demo.
		Object2D cam = TileBeanEngine.world.get(TileBeanEngine.getCameraHandle());
		cam.x = 240;
		cam.y = 135;
		cam.z = 4;
	}

	private Object2DHandle createPlayer(TilemapObject t_obj) {
		// Create a game object representing our player.
		Object2D obj = new Object2D();
		Object2DHandle obj_handle = TileBeanEngine.world.add(obj);
		obj.x = t_obj.getX();
		obj.y = t_obj.getY();

		// Add a Sprite component. Sprites can display images and animations.
		Sprite sprite = new Sprite();
		sprite.setGraphics(TileBeanEngine.assets.getTextureAssetHandle("char_idle"));
		sprite.play();
		TileBeanEngine.world.addComponent(obj_handle, sprite);

		// Add a Collider component to the player object.
		Collider c = Collider.makeBoxCollider(20, 32);
		c.offset(0, 8);
		TileBeanEngine.world.addComponent(obj_handle, c);

		// It looks better if the player's placed on the ground.
		placeOnGround(obj_handle);
		obj.y++; // Force a collision with the ground on the first frame, so we stay in the idle animation.

		return obj_handle;
	}

	private Object2DHandle createFlag(TilemapObject t_obj) {
		// Create a game object representing a flag which is the player's goal in the level.
		Object2D obj = new Object2D();
		Object2DHandle obj_handle = TileBeanEngine.world.add(obj);
		obj.x = t_obj.getX();
		obj.y = t_obj.getY();
		obj.z = -1;
		
		// Retrieve the texture handle and texture of the first frame of the flag animation, we'll need them later.
		TextureAssetHandle tex_handle = TileBeanEngine.assets.getTextureAssetHandle("flag");
		Texture tex = TileBeanEngine.assets.get(tex_handle).getTexture().get();
		
		// Add a Sprite component. Sprites can display images and animations.
		Sprite sprite = new Sprite();
		sprite.setGraphics(tex_handle);
		sprite.play();
		TileBeanEngine.world.addComponent(obj_handle, sprite);

		// Add a Collider component to the flag object.
		Collider c = Collider.makeBoxCollider(tex.getWidth(), tex.getHeight());
		TileBeanEngine.world.addComponent(obj_handle, c);

		// The flag needs to be flush with the ground.
		placeOnGround(obj_handle);
		obj.y += 2; // ...And then we'll put it down a little further.

		return obj_handle;
	}

	private Object2DHandle createCoin(TilemapObject t_obj) {
		// Create a game object representing a coin which the player can collect.
		Object2D obj = new Object2D();
		Object2DHandle obj_handle = TileBeanEngine.world.add(obj);
		obj.x = t_obj.getX();
		obj.y = t_obj.getY();
		obj.z = 1;
		
		// Retrieve the texture handle and texture of the first frame of the coin animation, we'll need them later.
		TextureAssetHandle tex_handle = TileBeanEngine.assets.getTextureAssetHandle("coin");
		Texture tex = TileBeanEngine.assets.get(tex_handle).getTexture().get();
		
		// Add a Sprite component. Sprites can display images and animations.
		Sprite sprite = new Sprite();
		sprite.setGraphics(tex_handle);
		sprite.gotoAndPlay((int)(Math.random() * (double)sprite.getTotalFrames()));
		TileBeanEngine.world.addComponent(obj_handle, sprite);

		// Add a Collider component to the coin object.
		Collider c = Collider.makeBoxCollider(tex.getWidth(), tex.getHeight());
		TileBeanEngine.world.addComponent(obj_handle, c);

		obj_coin_handles.add(obj_handle);
		return obj_handle;
	}

	/**
	 * Moves an object placed in the level down until it's flush with the ground.
	 * The object must have a Sprite and its Sprite must have graphics set.
	 * This method is used during level setup. (See createFlag)
	 */
	private void placeOnGround(Object2DHandle obj_handle) {
		Optional<Object2D> opt_obj = TileBeanEngine.world.tryGet(obj_handle);
		if (!opt_obj.isPresent()) return;
		Object2D obj = opt_obj.get();

		Optional<Component> opt_sprite = TileBeanEngine.world.tryGetComponent(obj_handle, Sprite.class);
		if (!opt_sprite.isPresent()) return;
		
		Sprite sprite = (Sprite)opt_sprite.get();
		Optional<TextureAsset> opt_tex_asset = TileBeanEngine.assets.tryGet(sprite.getGraphics());
		if (!opt_tex_asset.isPresent()) return;
		
		TextureAsset tex_asset = (TextureAsset)opt_tex_asset.get();
		Optional<Texture> opt_tex = tex_asset.getTexture();
		if (!opt_tex.isPresent()) return;

		Texture tex = opt_tex.get();

		Tilemap tilemap = (Tilemap)TileBeanEngine.world.getComponent(obj_tilemap_handle, Tilemap.class);
		TilesetAsset tileset = TileBeanEngine.assets.get(tilemap.getTileset());
		float bottom = obj.y + tex.getHeight() / 2;
		int tile_x = (int)(obj.x / tilemap.getTileWidth());
		while (true) {
			int tile_bottom = (int)(bottom / tilemap.getTileHeight());
			if (tile_bottom >= tilemap.getHeight()) {
				// Give up, no tile below
				bottom = obj.y + tex.getHeight() / 2;
				break;
			}
			TileInfo tile_info = tileset.getTileInfo(tilemap.getTileID(tile_x, tile_bottom));
			if (!tile_info.isEmpty()) {
				bottom = tile_bottom * tilemap.getTileHeight();
				break;
			}
			bottom += tilemap.getTileHeight();
		}
		obj.y = bottom - tex.getHeight() / 2;
	}

	public void shutdown() {
		TileBeanEngine.assets.clear();
		TileBeanEngine.world.clear();
		TileBeanEngine.show_colliders = false;
	}

	public void update(float delta) {
		if (!reset_game) {
			// Retrieve the objects and components we need to work with
			Object2D obj_player = TileBeanEngine.world.get(obj_player_handle);
			Object2D obj_flag = TileBeanEngine.world.get(obj_flag_handle);
			Sprite sprite_player = (Sprite)TileBeanEngine.world.getComponent(obj_player_handle, Sprite.class);
			Tilemap tilemap = (Tilemap)TileBeanEngine.world.getComponent(obj_tilemap_handle, Tilemap.class);
			Collider collider_player = (Collider)TileBeanEngine.world.getComponent(obj_player_handle, Collider.class);
			Collider collider_flag = (Collider)TileBeanEngine.world.getComponent(obj_flag_handle, Collider.class);
			
			// Retrieve the asset handles we need to work with
			TextureAssetHandle tex_asset_char_idle = TileBeanEngine.assets.getTextureAssetHandle("char_idle");
			TextureAssetHandle tex_asset_char_run = TileBeanEngine.assets.getTextureAssetHandle("char_run");
			TextureAssetHandle tex_asset_char_wow = TileBeanEngine.assets.getTextureAssetHandle("char_wow");
			TextureAssetHandle tex_asset_char_jump = TileBeanEngine.assets.getTextureAssetHandle("char_jump");

			/////////////////////
			// Player controls //
			/////////////////////

			boolean moved = false;
			boolean can_move = !sprite_player.getGraphics().equals(tex_asset_char_wow); // If the character is doing the "wow" animation, he can't move.

			if (TileBeanEngine.input.isKeyDown(Input.Keys.SPACE)) {
				if (!player_ignore_jump) {
					if (player_is_on_ground && player_space_released_after_jump) {
						player_velocity_y = -player_jump_strength;
						player_space_released_after_jump = false;

						SoundAsset sound_asset_jump = TileBeanEngine.assets.getSoundAsset("sound_jump").get();
						Sound sound = sound_asset_jump.getSound().get();
						sound.play();
					} else if (!player_is_on_ground) {
						player_ignore_jump = true;
					}
				}
			} else {
				if (player_is_on_ground) {
					player_space_released_after_jump = true;
					player_ignore_jump = false;
				}
			}

			CollisionInfo player_vs_flag = Collision.detect(collider_player, collider_flag);
			if (player_vs_flag.exists) {
				if (Math.abs(obj_player.x - obj_flag.x) < 10f) { // The player needs to be close to the flag to win.
					if (!win_condition) {
						// You won!!
						win_condition = true;
						if (!sprite_player.getGraphics().equals(tex_asset_char_wow)) {
							sprite_player.setGraphics(tex_asset_char_wow);
							sprite_player.gotoAndPlay(0);
						}
					}
				}
			} else {
				win_condition = false;
			}

			if (win_condition || loss_condition) can_move = false;

			if (can_move) {
				if (TileBeanEngine.input.isKeyDown(Input.Keys.LEFT)) {
					obj_player.x -= player_move_speed * delta;
					obj_player.scale_x = -1;
					moved = true;
				}

				if (TileBeanEngine.input.isKeyDown(Input.Keys.RIGHT)) {
					obj_player.x += player_move_speed * delta;
					obj_player.scale_x = 1;
					moved = true;
				}
			}

			if (can_move) {
				if (!player_is_on_ground) {
					sprite_player.setGraphics(tex_asset_char_jump);
				} else {
					if (!moved) {
						// Character has not moved, ensure the idle animation is playing
						if (!sprite_player.getGraphics().equals(tex_asset_char_idle)) {
							sprite_player.setGraphics(tex_asset_char_idle);
							sprite_player.play();
						}
					} else {
						// Character is moving, ensure the run animation is playing
						if (!sprite_player.getGraphics().equals(tex_asset_char_run)) {
							sprite_player.setGraphics(tex_asset_char_run);
							sprite_player.play();
						}
					}
				}
			} else {
				if (sprite_player.isPlaying()) {
					if (sfx_wow_id == -1 && (int)sprite_player.getCurrentFrame() == 6) {
						// Play a nice, crusty "WOW" sound effect
						SoundAsset sound_asset_wow = TileBeanEngine.assets.getSoundAsset("sound_wow").get();
						Sound sound = sound_asset_wow.getSound().get();
						sfx_wow_id = sound.play();
					}
				}
			}

			/////////////
			// Gravity //
			/////////////
			
			obj_player.y += player_velocity_y;
			if (moved || player_velocity_y != 0.0f) player_velocity_y += gravity * delta; // This check prevents the player from sliding down slopes.

			//////////////////////////////////////////////
			// Collision detection (Player vs. terrain) //
			//////////////////////////////////////////////
			
			TileCollisionInfo[] info_list = Collision.detect(collider_player, tilemap);

			float y_prev = obj_player.y; // Save the player's previous Y position
			
			// Resolve collision(s)
			Collision.resolve(info_list);
			
			if (obj_player.y < y_prev) {
				if (player_velocity_y >= 0.0f) {
					// If the player was moved *up* out of a tile and wasn't jumping, they must be standing on something, so reset their Y velocity.
					player_velocity_y = 0;
					player_is_on_ground = true;
					coyote_time = coyote_time_max;
				}
			}
			
			if (player_velocity_y > 0.0f) {
				coyote_time -= delta;
				if (coyote_time <= 0) {
					player_is_on_ground = false;
				}
			} else if (player_velocity_y < 0.0f) player_is_on_ground = false;

			////////////////////////////////////////////
			// Collision detection (Player vs. coins) // (And updating collected coins displaying sparkle animations)
			////////////////////////////////////////////
			
			for (int i = 0; i < obj_coin_handles.size();) {
				Object2DHandle obj_coin_handle = obj_coin_handles.get(i);
				Optional<Component> opt_collider_coin = TileBeanEngine.world.tryGetComponent(obj_coin_handle, Collider.class);
				if (opt_collider_coin.isPresent()) {
					CollisionInfo info = Collision.detect(collider_player, (Collider)opt_collider_coin.get());
					if (info.exists) {
						// Coin was collected! We're going to make it play a sparkle animation before removing it from the world.

						// Remove the coin's collider.
						TileBeanEngine.world.removeComponent(obj_coin_handle, Collider.class);
						
						// Change the coin's graphics to the sparkly coin collected graphics.
						Sprite sprite = (Sprite)TileBeanEngine.world.getComponent(obj_coin_handle, Sprite.class);
						sprite.setGraphics(TileBeanEngine.assets.getTextureAssetHandle("coin_collected"));
						sprite.play();

						// Play coin collection sound
						Optional<SoundAsset> opt_sound_asset_coin = TileBeanEngine.assets.getSoundAsset("sound_coin");
						if (opt_sound_asset_coin.isPresent()) {
							SoundAsset sound_asset = opt_sound_asset_coin.get();
							sound_asset.play();
						}
					}
					i++;
				} else {
					// This coin has been collected already and is displaying a sparkle animation.

					Sprite sprite = (Sprite)TileBeanEngine.world.getComponent(obj_coin_handle, Sprite.class);
					if (!sprite.isPlaying()) { // The sparkle animation doesn't loop, so if the sprite isn't playing we know the animation is done.
						// Remove the coin from the world
						TileBeanEngine.world.remove(obj_coin_handle);
						obj_coin_handles.remove(i);
					} else {
						i++;
					}
				}
			}

			///////////////////
			// Update camera //
			///////////////////
			
			Object2D obj_cam = TileBeanEngine.world.get(TileBeanEngine.getCameraHandle());
			obj_cam.x = obj_player.x;

			float bound_left = 240;
			float bound_right = tilemap.getWidth() * tilemap.getTileWidth() - 240;
			
			if (obj_cam.x < bound_left) obj_cam.x = bound_left;
			if (obj_cam.x > bound_right) obj_cam.x = bound_right;

			///////////////////////////////////
			// Check win and loss conditions //
			///////////////////////////////////
			
			loss_condition = obj_player.y > tilemap.getHeight() * tilemap.getTileHeight();

			if (loss_condition && end_level_time == 0.0f) {
				// Play "die" sound effect
				Optional<SoundAsset> opt_sound_asset_die = TileBeanEngine.assets.getSoundAsset("sound_die");
				if (opt_sound_asset_die.isPresent()) {
					SoundAsset sound_asset = opt_sound_asset_die.get();
					sound_asset.play();
				}
			}

			if (win_condition || loss_condition) {
				end_level_time += delta;
				if (end_level_time >= end_level_time_max) {
					reset_game = true;
				}
			}
		} else { // reset_game == true
			Object2DHandle obj_fade_transition_handle = TileBeanEngine.world.getHandle("fade_transition");
			if (obj_fade_transition_handle.isEmpty()) {
				Object2D obj_fade_transition = new Object2D();
				obj_fade_transition.z = 9999;
				obj_fade_transition_handle = TileBeanEngine.world.add(obj_fade_transition, "fade_transition");
				FadeTransition fade_transition = new FadeTransition();
				TileBeanEngine.world.addComponent(obj_fade_transition_handle, fade_transition);
				fade_transition.start(1);
			}

			FadeTransition fade_transition = (FadeTransition)TileBeanEngine.world.getComponent(obj_fade_transition_handle, FadeTransition.class);
			if (fade_transition.isDone()) {
				if (fade_transition.getDirection() == 1.0f) {
					// Reset the game objects
					TileBeanEngine.world.clear();
					setupLevel();
					// Start a new fade transition (we just removed it with world.clear)
					Object2D obj_fade_transition = new Object2D();
					obj_fade_transition.z = 9999;
					obj_fade_transition_handle = TileBeanEngine.world.add(obj_fade_transition, "fade_transition");
					fade_transition = new FadeTransition();
					TileBeanEngine.world.addComponent(obj_fade_transition_handle, fade_transition);
					fade_transition.start(-1);
				} else {
					// Remove the transition and start the game
					reset_game = false;
					TileBeanEngine.world.remove(obj_fade_transition_handle);
				}
			}
		}
	}

	public void runGUI() {
		ImGui.textWrapped("This is a demonstration of everything needed to make a basic game.\nHelp Mr. TileBean collect all the coins and reach the flag!\n\nUse the keyboard's left/right arrows to move Mr. TileBean.\nPress spacebar to jump!\n\n(To minimize this window, use the button in the upper-left, and then click back into the game area.)");
	}

}
