package display;

import javax.swing.Timer;

import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JPanel;

import entity.Entity;
import entity.Player;
import tiles.*;

/** This is a class of constants. It will contain global variables. */
public final class World {
	// Game state: 0 = overworld, 1 = bossfight, 2 = dead, 3 = dungeon
	// 4 = title screen / loading
	private static final int WORLD_HEIGHT = 11;
	private static final int WORLD_WIDTH = 16;

	private static int gameState = 0;
	private static Set<Entity> entities = new HashSet<Entity>();
	private static WorldTile[][] tiles = new WorldTile[WORLD_WIDTH][WORLD_HEIGHT]; // 24x32
																					// matrix
	private static Player thePlayer;
	private static Timer ticker;

	static {
		thePlayer = new Player();
	}

	/**
	 * This method adds an entity to the world. It should only be called from
	 * the Entity constructor.
	 * 
	 * @param e
	 *            the Entity to add
	 */
	public static void registerEntity(Entity e) {
		entities.add(e);
	}

	/**
	 * For drawing methods. Given the abstract float coordinates x and y, return
	 * the coordinates for where on the screen to draw.
	 * 
	 * @param x
	 *            The x coordinate (in-world)
	 * @param y
	 *            The y coordinate (in-world)
	 * @return int[] of {x, y} which tells you where on the screen to draw
	 */
	public static int[] getScreenCoordinates(double x, double y) {
		return new int[] { (int) (Math.round(x * 32)), (int) (Math.round(y * 32 + 112)) };
	}

	/**
	 * Get the WorldTile at the specified x,y coordiantes
	 * 
	 * @param x
	 *            the x coordinate of tile
	 * @param y
	 *            the y coordinate of tile
	 * @return the WorldTile at specified location
	 */
	public static WorldTile getTileAt(int x, int y) {
		if (tiles[x][y] != null)
			return tiles[x][y];
		else
			return OverworldTile.ground;
	}

	/**
	 * Get the WorldTile at the specified x,y coordiantes
	 * 
	 * @param x
	 *            the x coordinate of tile
	 * @param y
	 *            the y coordinate of tile
	 * @return the WorldTile at specified location
	 */
	public static WorldTile getTileAt(double x, double y) {
		int nx = (int) Math.floor(x);
		int ny = (int) Math.floor(y);
		if (tiles[nx][ny] != null)
			return tiles[nx][ny];
		else
			return OverworldTile.ground;
	}

	/**
	 * Call this method to delete an entity from the world.
	 * 
	 * @param e
	 *            the Entity to delete
	 */
	public static void deregisterEntity(Entity e) {
		entities.remove(e);
	}

	/** @return a Set view of all current Entities */
	public static Set<Entity> getAllEntities() {
		return new HashSet<Entity>(entities);
	}

	/**
	 * @return the gameState
	 */
	public static int getGameState() {
		return gameState;
	}

	/**
	 * @param gameState
	 *            the new GameState
	 */
	public static void setGameState(int gameState) {
		World.gameState = gameState;
	}

	/**
	 * @return The player for the current world.
	 */
	public static Player getThePlayer() {
		return thePlayer;
	}

	/**
	 * Called when the player walks off the screen, loads the new section of
	 * world in the specified direction
	 */
	public static void loadWorld(Direction direction) {
		System.out.println("Fix me");
		String nextWorld = null;
		loadWorld(nextWorld);
	}

	public static void loadWorld(String filename) {
		// Delete all entites and tiles
		entities = new HashSet<Entity>();
		entities.add(thePlayer);
		Scanner theScanner = null;
		try {
			theScanner = new Scanner(new File("assets/worlds/" + filename));
		} catch (IOException e) {
			System.err.println("Error reading file assets/worlds/" + filename);
			System.exit(1);
		}
		String tileFileName = theScanner.nextLine().replace("tiles ", "");
		loadTiles(tileFileName);

		System.out.println("Fix me");
	}

	private static void loadTiles(String filename) {
		Scanner s = null;
		try {
			s = new Scanner(new File("assets/worlds/" + filename));
		} catch (FileNotFoundException e) {
		}
		for (int y = 0; y < WORLD_HEIGHT; y++) {
			String[] nums = s.nextLine().split("\\s+");
			for (int x = 0; x < WORLD_WIDTH; x++) {
				tiles[x][y] = OverworldTile.getTileFromCode(Integer.parseInt(nums[x], 16));
			}
		}
	}

	public static void renderTiles(Graphics g) {
		for (int x = 0; x < WORLD_WIDTH; x++) {
			for (int y = 0; y < WORLD_HEIGHT; y++) {
				World.getTileAt(x, y).draw(x, y, g);
			}
		}
	}

	public static void startTicker(JPanel parent, KeyboardHandler kb) {
		ticker = new Timer(33, new Tick(parent, kb));
		ticker.start();
	}

	public static void stopTicker() {
		ticker.stop();
	}

	public static boolean willCollideTile(Entity e, double movement) {
		double[] newCoords = e.getFacing().moveInDirection(e.getX(), e.getY(), movement);
		double newX = newCoords[0];
		double newY = newCoords[1];
		return World.getTileAt(newX, newY).isSolid();
	}

	/** World may not be instantiated. */
	private World() {
	}

}