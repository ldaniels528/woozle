package com.ldaniels528.woozle.breakout;

import static com.ldaniels528.woozle.CustomColors.LIGHT_GREEN;
import static com.ldaniels528.woozle.GameState.GAME_OVER;
import static com.ldaniels528.woozle.GameState.OUT_OF_BOUNDS;
import static java.awt.Color.GREEN;
import static java.awt.Color.MAGENTA;
import static java.awt.Color.ORANGE;
import static java.awt.Color.YELLOW;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.ldaniels528.woozle.GameState;
import com.ldaniels528.woozle.SharedGameData;

/**
 * This class represents the virtual playing field for Break-Out
 * @author lawrence.daniels@gmail.com
 */
class BreakOutPlayingField {
	// the maximum number of entities
	private static final int MAX_ENTITIES = 300;
	
	// the distance from the top of the screen to the first block
	private static final int OFFSET = 25;
	
	// the block colors
	private static final Color[] COLORS = new Color[] {
		MAGENTA, ORANGE, YELLOW, LIGHT_GREEN, GREEN,  
	};
	
	// internal fields
	private final BreakOutGameManager gameManager;
	private final SharedGameData gameData;
	private final Set<Entity> entitySet;
	private final Set<Entity> addQueue;
	private final Set<Entity> removeQueue;
	private final Paddle paddle;
	private final Ball ball;
	private Entity[] entityCache;
	private boolean dirtyCache;
	private int spareBalls;
	private int blockCount;

	/**
	 * Default Constructor
	 */
	public BreakOutPlayingField( final BreakOutGameManager gameManager ) {
		this.gameManager	= gameManager;
		this.entitySet		= new HashSet<Entity>( MAX_ENTITIES );
		this.addQueue		= new HashSet<Entity>( MAX_ENTITIES );
		this.removeQueue	= new HashSet<Entity>( MAX_ENTITIES );
		this.gameData		= SharedGameData.getInstance();
		this.paddle 		= new Paddle( 160, 205 );
		this.ball			= new Ball( this, 160, 185 );
		this.spareBalls		= 3;
		this.dirtyCache		= true;
	}
	
	/**
	 * Adds the given entity
	 * @param entity the given {@link Entity entity}
	 */
	public void add( final Entity entity ) {
		synchronized( addQueue ) {
			addQueue.add( entity );
			this.dirtyCache = true;
		}
	}
	
	/**
	 * Adds the given collection of entities
	 * @param entities the given {@link Collection collection} of {@link Entity entities}
	 */
	public void addAll( final Collection<Entity> entities ) {
		synchronized( addQueue ) {
			addQueue.addAll( entities );
			this.dirtyCache = true;
		}
	}
	
	/**
	 * Adds the given array of entities
	 * @param entities the given array of {@link Entity entities}
	 */
	public void addAll( final Entity[] entities ) {
		synchronized( addQueue ) {
			addQueue.addAll( Arrays.asList( entities ) );
			this.dirtyCache = true;
		}
	}
	
	/**
	 * Removes the given entity
	 * @param entity the given {@link Entity entity}
	 */
	public void remove( final Entity entity ) {
		synchronized( removeQueue ) {
			removeQueue.add( entity );
			this.dirtyCache = true;
		}
	}
	
	/**
	 * Removes all entities from the virtual playing field
	 */
	public void removeAllEntities() {
		synchronized( addQueue ) {
			synchronized( removeQueue ) {
				synchronized( entitySet ) {
					addQueue.clear();
					removeQueue.clear();
					entitySet.clear();
				}
			}
		}
	}

	/**
	 * Setup the playing field
	 */
	public void setupLevel() {
		// clear the virtual playing field
		removeAllEntities();
		
		// add the paddle and ball
		this.add( paddle );
		this.add( ball );
		
		// get the current level
		final int level = SharedGameData.getInstance().getLevel();
		
		// create the blocks
		Block[] blocks = null;
		switch( level ) {
			case 1: 	blocks = setupBlocks( 13, 4, 25, 20 ); break;
			case 2: 	blocks = setupBlocks( 13, 6, 25, 15 ); break;
			default: 	blocks = setupBlocks( 13, 8, 25, 10 ); 
		}
		
		// reset the entities
		resetEntities();
		
		// add the blocks to the field
		addAll( blocks );
		
		// record the number of blocks
		blockCount = blocks.length;
	}
	
	/**
	 * Resets the game after an 'Out of Bounds' event
	 */
	public void resetEntities() {
		// get the entities
		final Entity[] entities = getEntities();
		
		// reset all living entities
		for( final Entity entity : entities ) {
			if( entity.isAlive() ) {
				entity.reset();
			}
		}
	}
	
	/**
	 * Returns the array of entities
	 * @return the array of entities
	 */
	public Entity[] getEntities() {
		// remove dead entities
		removeDeadEntities();

		// add new entities
		addNewEntities();
		
		// if the cache is dirty, refresh it...
		if( dirtyCache || ( entityCache == null ) ) {
			synchronized( entitySet ) {
				entityCache = entitySet.toArray( new Entity[ entitySet.size() ] );
			}
			dirtyCache = false;
		}
		
		// return the cached entities
		return entityCache;
	}
	
	/** 
	 * Returns the number of spare balls available
	 * @return the number of spare balls available
	 */
	public int getSpareBalls() {
		return spareBalls;
	}
	
	/** 
	 * Sets the number of spare balls available
	 * @param spareBalls the number of spare balls available
	 */
	public void setSpareBalls( final int spareBalls ) {
		this.spareBalls = spareBalls;
	}
	
	/** 
	 * Called when a block dies
	 */
	public void blockDied() {
		blockCount --;
		if( blockCount == 0 ) {
			gameManager.changeGameState( GameState.LEVEL_CHANGE );
		}
	}
	
	/**
	 * Moves the paddle to the given position
	 * @param position the given position
	 */
	public void movePaddle( final double position ) {
		paddle.x = position;
	}
	
	/**
	 * Called when the ball goes out of bounds
	 */
	public void outOfBounds() {
		// decrease the number of spare balls
		spareBalls--;
		
		// change the game state
		gameManager.changeGameState( ( spareBalls > 0 ) ? OUT_OF_BOUNDS : GAME_OVER );
	}
	
	/** 
	 * Updates the score based on the given entity
	 * @param entity the given {@link Scoreable entity}
	 */
	public void score( final Scoreable entity ) {
		// adjust the score
		gameData.adjustScore( entity.getScorePoints() );
	}
	
	/**
	 * Updates the playing field
	 * @param ct the given cycle time
	 */
	public void update( final double ct ) {		
		// get the array of entities
		final Entity[] entities = getEntities();
		
		// update all entities
		for( final Entity entity : entities ) {
			entity.update( ct );
		}
		
		// check for collisions
		for( final Entity entityA : entities ) {
			for( final Entity entityB : entities ) {
				if( ( entityA != entityB ) && entityA.isAlive() && entityB.isAlive() ) {
					if( entityA.intersects( entityB ) ) {
						// allow each entity to handle the collision
						entityA.handleCollsion( entityB );
						entityB.handleCollsion( entityA );
						
						// did entity A die?
						if( !entityA.isAlive() ) {
							remove( entityA );
						}
						
						// did entity B die?
						if( !entityB.isAlive() ) {
							remove( entityB );
						}
					}
				}
			}
		}
	}
	
	/** 
	 * Sets up a grid of blocks
	 * @param columns the number of block columns
	 * @param rows the number of block rows
	 * @param width the width of each block
	 * @param height the height of each block
	 * @return an array of {@link Block blocks}
	 */
	private Block[] setupBlocks( final int columns, 
							  	 final int rows, 
							  	 final int width,	
							  	 final int height ) {
		// create the blocks array
		final Block[] blocks = new Block[ rows * columns ];
		
		// create the blocks
		for( int row = 0, n = 0; row < rows; row++ ) {
			for( int col = 0; col < columns; col++ ) {
				final Block block = 
					new Block( this, 
							   col * width, 
							   row * height + OFFSET, 
							   width-1, 
							   height-1, 
							   COLORS[col % COLORS.length] );
				blocks[n++] = block;
			}
		}
		return blocks;
	}

	/** 
	 * Removes dead entities
	 */
	private void removeDeadEntities() {
		synchronized( removeQueue ) {
			// if the remove queue is not empty ...
			if( !removeQueue.isEmpty() ) {
				// remove the entities
				synchronized( entitySet ) {
					entitySet.removeAll( removeQueue );
				}
				removeQueue.clear();
				this.dirtyCache = true;
			}
		}
	}
	
	/** 
	 * Adds new entities
	 */
	private void addNewEntities() {
		synchronized( addQueue ) {
			// if the add queue is not empty ...
			if( !addQueue.isEmpty() ) {
				// add the new entities
				synchronized( entitySet ) {
					entitySet.addAll( addQueue );
				}
				addQueue.clear();
				this.dirtyCache = true;
			}
		}
	}

}
