package com.untiedgames.TileBeanEngine;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

import com.badlogic.gdx.files.FileHandle;

/**
 * TilemapAsset is an asset which can hold level data loaded from a Tiled *.tmx file.
 * This uses a naive loader which will only load the tile IDs in each layer, disassociated from any tileset.
 * I'm aware that libGDX has the ability to load Tiled-format maps, but I've decided to roll my own to show on a basic level how it could be done.
 */
public class TilemapAsset extends Asset {

	private class TilemapLayer {
		
		private int[] data;
		private int width = 0;
		private int height = 0;
	
	}

	private ArrayList<TilemapLayer> layers;
	
	public TilemapAsset(String name_and_path) {
		this(name_and_path, name_and_path, FILEMODE.INTERNAL);
	}

	public TilemapAsset(String name, String path) {
		this(name, path, FILEMODE.INTERNAL);
	}

	public TilemapAsset(String name, String path, FILEMODE file_mode) {
		super(name, path, file_mode);
		layers = new ArrayList<>();
	}

	public boolean isLoaded() {
		return !layers.isEmpty();
	}

	/**
	 * Loads the TilemapAsset from its path. Supports Tiled *.tmx files.
	 * Returns true on success, false otherwise.
	 * If it cannot be loaded, an error message will be printed in the console.
	 */
	public boolean load() {
		if (!layers.isEmpty()) return true; // Asset is already loaded
		try {
			FileHandle file = makeFileHandle(path, file_mode); 
			if (getExtension(path).equals("tmx")) {
				// Naively load layers of tile data from the tmx file, which are lists of ints.
				// Note that the Tiled format uses 0 to indicate an unassigned tile and begins assigned tiles at 1, while TileBeanEngine uses Integer.MAX_VALUE and begins assigned tiles at 0.
				// Therefore, we'll be offsetting each nonzero ID we load by -1, and replacing all zeroes with Integer.MAX_VALUE.
				String data = file.readString();
				try (Scanner sc = new Scanner(data)) {
					if (data.indexOf('\r') != -1) sc.useDelimiter("\r\n");
					else sc.useDelimiter("\n");
					int next_layer_w = 0;
					int next_layer_h = 0;
					while (sc.hasNext()) {
						String line = sc.next();
						if (line.contains("<layer")) {
							next_layer_w = Integer.parseInt(line.substring(line.indexOf("width=\"") + 7, line.indexOf("\"", line.indexOf("width=\"") + 7)));
							next_layer_h = Integer.parseInt(line.substring(line.indexOf("height=\"") + 8, line.indexOf("\"", line.indexOf("height=\"") + 8)));
							if (next_layer_w <= 0 || next_layer_h <= 0) throw new Exception("Layer width or height must be greater than zero.");
						} else if (line.contains("<data")) {
							if (next_layer_w <= 0 || next_layer_h <= 0) throw new Exception("Expected layer width and height before layer data.");
							TilemapLayer layer = new TilemapLayer();
							layer.width = next_layer_w;
							layer.height = next_layer_h;
							layer.data = new int[next_layer_w * next_layer_h];
							int ctr = 0;
							while (sc.hasNext()) {
								line = sc.next();
								if (line.contains("</data>")) break;
								Scanner sc2 = new Scanner(line);
								sc2.useDelimiter(",");
								while(sc2.hasNext()) {
									int id = Integer.parseInt(sc2.next());
									if (id == 0) id = Integer.MAX_VALUE;
									else id--;
									layer.data[ctr++] = id;
									if (ctr == layer.data.length) break;
								}
								sc2.close();
							}
							layers.add(layer);
							next_layer_w = 0;
							next_layer_h = 0;
						}
					}
					sc.close();
				}
				return true;
			}
		} catch (Exception e) {
			System.err.println("Failed to load tilemap asset \"" + path + "\", file mode " + file_mode.toString() + "\nDetails: " + e.getMessage());
			return false;
		}

		return false;
	}

	/**
	 * Unloads the TilemapAsset, performing any destruction of resources required.
	 */
	public void unload() {
		layers.clear();
	}

	/**
	 * Returns the number of layers in the TilemapAsset.
	 */
	public int getNumLayers() {
		return layers.size();
	}

	/**
	 * Returns the layer at the given index as a Tilemap component, ready to be added to an object.
	 */
	public Optional<Tilemap> getLayer(int index) {
		if (index < 0 || index >= layers.size()) return Optional.empty();
		TilemapLayer layer = layers.get(index);
		Tilemap ret = new Tilemap(layer.width, layer.height);
		ret.setContents(layer.data);
		return Optional.of(ret);
	}

}
