package com.ldaniels528.woozle.invasion;

import static com.ldaniels528.woozle.GameDisplayPane.BOARD_WIDTH;
import static java.awt.Color.WHITE;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;

import com.ldaniels528.woozle.ContentManager;

/**
 * Represents a space ship
 * @author lawrence.daniels@gmail.com
 */
class SpaceShip extends Actor {
	private static final int[][] SHIP_POINTS = { 
		{  0, 15 }, {  0,  0 }, {  5, 10 }, { 10, 10 }, 
		{ 15, -5 },
		{ 20, 10 }, { 25, 10 }, { 30,  0 }, { 30, 15 }
	};
	private static final int WIDTH	= 30;
	private static final int HEIGHT = 15;
	private final Image shipImage;

	/**
	 * Creates a new space ship instance
	 * @param x the given x-axis coordinate 
	 * @param y the given y-axis coordinate
	 */
	public SpaceShip( final double x, final double y ) {
		super( x, y, WIDTH, HEIGHT );
		this.shipImage = ContentManager.loadImage( "/images/invasion/ship.gif" );
	}

	/* 
	 * (non-Javadoc)
	 * @see com.ldaniels528.woozle.invasion.Entity#handleCollsion(com.ldaniels528.woozle.invasion.Entity)
	 */
	@Override
	public void handleCollsion( final Entity entity ) {
		// TODO Auto-generated method stub
	}

	/* 
	 * (non-Javadoc)
	 * @see com.ldaniels528.woozle.invasion.Entity#render(java.awt.Graphics2D)
	 */
	@Override
	public void render( final Graphics2D g ) {
		// compute the polygons for the ship
		//final Polygon polygon = createShipShape( (int)x, (int)y );
		
		// draw the ship
		//g.setColor( WHITE );
		//g.fillPolygon( polygon );
		
		g.drawImage( shipImage, (int)x, (int)y, null );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see com.ldaniels528.woozle.invasion.Actor#fire()
	 */
	@Override
	public void fire() {
		
	}

	/* 
	 * (non-Javadoc)
	 * @see com.ldaniels528.woozle.invasion.Entity#reset()
	 */
	@Override
	public void reset() {
		this.x = BOARD_WIDTH / 2;
	}

	/* 
	 * (non-Javadoc)
	 * @see com.ldaniels528.woozle.invasion.Entity#update(double)
	 */
	@Override
	public void update( final double ct ) {
		// do nothing
	}
	
	/**
	 * Returns the shape of the space ship
	 * @return the {@link Polygon shape} of the space ship
	 */
	private static Polygon createShipShape( final int x, final int y ) {
		final Polygon polygon = new Polygon();
		for( final int[] p : SHIP_POINTS ) {
			final int xp = x + p[0];
			final int yp = y + p[1];
			polygon.addPoint( xp, yp );
		}
		return polygon;
	}

}
