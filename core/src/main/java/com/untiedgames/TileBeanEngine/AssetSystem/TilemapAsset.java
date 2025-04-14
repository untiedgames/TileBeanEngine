package com.untiedgames.TileBeanEngine.AssetSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;

import com.badlogic.gdx.files.FileHandle;
import com.untiedgames.TileBeanEngine.Tilemap;

/**
 * TilemapAsset is an asset which can hold level data loaded from a Tiled *.tmx file.
 * This uses a naive loader which will only load the tile IDs in each layer, disassociated from any tileset.
 * I'm aware that libGDX has the ability to load Tiled-format maps, but I've decided to roll my own to show on a basic level how it could be done.
 * 
 * TMX FILE SUPPORT DETAILS:
 * - Reads each tile layer in order. You can retrieve the layer count and retrieve layers by their index as Tilemaps.
 * - You need to assign a Tileset to the Tilemap(s) manually. (However, this also means you can swap same-format tilesets easily!)
 * - Objects are read sequentially and what layer they're on is disregarded.
 * - Only Tiled "point" objects are supported.
 * - Objects know their ID, name, type ("class"), x, and y.
 * - Object properties are read and can be retrieved by their property name.
 * - The following Tiled object properties are supported:
 * 		- Boolean
 * 		- Float
 * 		- Integer
 * 		- Object (as integer ID)
 * 		- String
 */
public class TilemapAsset extends Asset {
	
	enum PROPERTYTYPE {
		BOOLEAN,
		FLOAT,
		INTEGER,
		OBJECT,
		STRING,
		UNDEFINED
	}

	private class TilemapLayer {
		
		private String name;
		private int[] data;
		private int width = 0;
		private int height = 0;
	
	}

	private class TilemapObjectProperty {

		PROPERTYTYPE type;
		Object value;

		@SuppressWarnings("unchecked")
		<T> T get(Class<T> class_type) {
			if (class_type.equals(Boolean.class) && type == PROPERTYTYPE.BOOLEAN) return (T)value;
			if (class_type.equals(Float.class) && type == PROPERTYTYPE.FLOAT) return (T)value;
			if (class_type.equals(Integer.class) && type == PROPERTYTYPE.INTEGER) return (T)value;
			if (class_type.equals(Integer.class) && type == PROPERTYTYPE.OBJECT) return (T)value; // The value is the integer ID of the referred-to object
			if (class_type.equals(String.class) && type == PROPERTYTYPE.STRING) return (T)value;
			return null;
		}

		PROPERTYTYPE getType() {
			return type;
		}

	}

	public class TilemapObject {

		private HashMap<String, TilemapObjectProperty> properties;
		private int id;
		private String name;
		private String type;
		private float x;
		private float y;

		public TilemapObject() {
			properties = new HashMap<>();
		}

		public int getID() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getType() {
			return type;
		}

		public float getX() {
			return x;
		}

		public float getY() {
			return y;
		}

		public boolean hasProperty(String name) {
			return properties.containsKey(name);
		}

		public PROPERTYTYPE getPropertyType(String name) {
			if (!properties.containsKey(name)) return PROPERTYTYPE.UNDEFINED;
			return properties.get(name).getType();
		}

		public <T> T getProperty(String name, Class<T> class_type) {
			if (!properties.containsKey(name)) return null;
			return properties.get(name).get(class_type);
		}
		
	}

	private ArrayList<TilemapLayer> layers;
	private ArrayList<TilemapObject> objects;
	
	public TilemapAsset(String name_and_path) {
		this(name_and_path, name_and_path, FILEMODE.INTERNAL);
	}

	public TilemapAsset(String name, String path) {
		this(name, path, FILEMODE.INTERNAL);
	}

	public TilemapAsset(String name, String path, FILEMODE file_mode) {
		super(name, path, file_mode);
		layers = new ArrayList<>();
		objects = new ArrayList<>();
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
				String data = file.readString();
				// Parse the *.tmx file
				try (Scanner sc = new Scanner(data)) {
					if (data.indexOf('\r') != -1) sc.useDelimiter("\r\n");
					else sc.useDelimiter("\n");
					String next_layer_name = "";
					int next_layer_w = 0;
					int next_layer_h = 0;
					TilemapObject next_object = null;
					while (sc.hasNext()) {
						String line = sc.next();
						if (line.contains("<layer")) {
							// Discovered a layer.
							// Get the layer name
							if (line.indexOf("name=") != -1) next_layer_name = line.substring(line.indexOf("name=\"") + 6, line.indexOf("\"", line.indexOf("name=\"") + 6));
							// Get the width and height in tiles
							next_layer_w = Integer.parseInt(line.substring(line.indexOf("width=\"") + 7, line.indexOf("\"", line.indexOf("width=\"") + 7)));
							next_layer_h = Integer.parseInt(line.substring(line.indexOf("height=\"") + 8, line.indexOf("\"", line.indexOf("height=\"") + 8)));
							if (next_layer_w <= 0 || next_layer_h <= 0) throw new Exception("Layer width or height must be greater than zero.");
						} else if (line.contains("<data")) {
							// Naively load layers of tile data from the tmx file, which are lists of ints.
							// Note that the Tiled format uses 0 to indicate an unassigned tile and begins assigned tiles at 1, while TileBeanEngine uses Integer.MAX_VALUE and begins assigned tiles at 0.
							// Therefore, we'll be offsetting each nonzero ID we load by -1, and replacing all zeroes with Integer.MAX_VALUE.
							if (next_layer_w <= 0 || next_layer_h <= 0) throw new Exception("Expected layer width and height before layer data.");
							TilemapLayer layer = new TilemapLayer();
							layer.name = next_layer_name;
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
							next_layer_name = "";
							next_layer_w = 0;
							next_layer_h = 0;
						} else if (line.contains("<object ")) {
							// Discovered an object
							if (next_object != null) throw new Exception("Encountered unexpected object begin tag while reading object.");
							next_object = new TilemapObject();
							next_object.id = Integer.parseInt(line.substring(line.indexOf("id=\"") + 4, line.indexOf("\"", line.indexOf("id=\"") + 4)));
							next_object.name = line.substring(line.indexOf("name=\"") + 6, line.indexOf("\"", line.indexOf("name=\"") + 6));
							next_object.type = line.substring(line.indexOf("type=\"") + 6, line.indexOf("\"", line.indexOf("type=\"") + 6));
							next_object.x = Float.parseFloat(line.substring(line.indexOf("x=\"") + 3, line.indexOf("\"", line.indexOf("x=\"") + 3)));
							next_object.y = Float.parseFloat(line.substring(line.indexOf("y=\"") + 3, line.indexOf("\"", line.indexOf("y=\"") + 3)));
						} else if (line.contains("</object>")) {
							// End of the object
							if (next_object == null) throw new Exception("Encountered object end tag, but no object begin tag has been encountered.");
							objects.add(next_object);
							next_object = null;
						} else if (line.contains("<property ")) {
							if (next_object != null) {
								// Object property
								TilemapObjectProperty prop = new TilemapObjectProperty();
								String name = line.substring(line.indexOf("name=\"") + 6, line.indexOf("\"", line.indexOf("name=\"") + 6));
								String value_str = line.substring(line.indexOf("value=\"") + 7, line.indexOf("\"", line.indexOf("value=\"") + 7));
								if (line.indexOf("type=") != -1) {
									// Expect a non-string type
									String type = line.substring(line.indexOf("type=\"") + 6, line.indexOf("\"", line.indexOf("type=\"") + 6));
									if (type.equals("bool")) {
										prop.type = PROPERTYTYPE.BOOLEAN;
										prop.value = Boolean.parseBoolean(value_str);
									} else if (type.equals("float")) {
										prop.type = PROPERTYTYPE.FLOAT;
										prop.value = Float.parseFloat(value_str);
									} else if (type.equals("int")) {
										prop.type = PROPERTYTYPE.INTEGER;
										prop.value = Integer.parseInt(value_str);
									} else if (type.equals("object")) {
										// The expected value is the integer ID of the referred-to object
										prop.type = PROPERTYTYPE.OBJECT;
										prop.value = Integer.parseInt(value_str);
									}
								} else {
									// String properties do not have a type
									prop.value = value_str;
								}
								next_object.properties.put(name, prop);
							}
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
	 * If the index is out of bounds, returns an empty Optional.
	 */
	public Optional<Tilemap> getLayer(int index) {
		if (index < 0 || index >= layers.size()) return Optional.empty();
		TilemapLayer layer = layers.get(index);
		Tilemap ret = new Tilemap(layer.width, layer.height);
		ret.setContents(layer.data);
		return Optional.of(ret);
	}

	/**
	 * Returns the name of the layer at the given index.
	 * If index is out of bounds, returns an empty string.
	 */
	public String getLayerName(int index) {
		if (index < 0 || index >= layers.size()) return "";
		return layers.get(index).name;
	}

	/**
	 * Returns an array of all TilemapObjects loaded from the *.tmx file, if any.
	 */
	public TilemapObject[] getObjects() {
		return objects.toArray(new TilemapObject[0]);
	}

}
