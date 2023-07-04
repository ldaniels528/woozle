package com.ldaniels528.woozle.invasion;

import com.ldaniels528.woozle.GameState;
import com.ldaniels528.woozle.SharedGameData;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.ldaniels528.woozle.GameDisplayPane.BOARD_WIDTH;

/** 
 * Invasion Virtual World
 * @author lawrence.daniels@gmail.com
 */
class InvasionVirtualWorld {
	// the maximum number of entities
	private static final int MAX_ENTITIES = 300;
	
	// internal fields
	private final InvasionGameManager gameManager;
	private final SharedGameData gameData;
	private final Set<Entity> entitySet;
	private final Set<Entity> addQueue;
	private final Set<Entity> removeQueue;
	private Entity[] entityCache;
	private SpaceShip spaceShip;
	private boolean dirtyCache;
	private int spareShips;
	private int alienCount;
	
	/**
	 * Default Constructor
	 */
	public InvasionVirtualWorld( final InvasionGameManager gameManager ) {
		this.gameManager 	= gameManager;
		this.entitySet		= new HashSet<Entity>( MAX_ENTITIES );
		this.addQueue		= new HashSet<Entity>( MAX_ENTITIES );
		this.removeQueue	= new HashSet<Entity>( MAX_ENTITIES );
		this.gameData		= SharedGameData.getInstance();
		this.spaceShip		= new SpaceShip( BOARD_WIDTH / 2, 160 );
		this.spareShips		= 3;
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
	 * Setup the level for the virtual world
	 */
	public void setupLevel() {
		// add the ship
		add( spaceShip );
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
	 * Allows the player to fire
	 */
	public void playerFire() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Moves the player's ship to the given position
	 * @param x the given x-coordinate position
	 * @param y the given y-coordinate position
	 */
	public void movePlayer( final double x, final double y ) {
		spaceShip.x = x - 15;
		spaceShip.y = y - 35;
	}
	
	/** 
	 * Called when a alien dies
	 */
	public void alienDied() {
		alienCount --;
		if( alienCount == 0 ) {
			gameManager.changeGameState( GameState.LEVEL_CHANGE );
		}
	}

	/** 
	 * Returns the number of spare ships available
	 * @return the number of spare ships available
	 */
	public int getSpareShips() {
		return spareShips;
	}

	/** 
	 * Sets the number of spare ships available
	 * @param spareShips the number of spare ships available
	 */
	public void setSpareShips( final int spareShips ) {
		this.spareShips = spareShips;
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
