package de.seitenbau.govdata.odp.registry.ckan.impl;

import java.io.Serializable;

import de.seitenbau.govdata.odp.registry.model.Coordinate;

/**
 * Implementation for the interface {@link Coordinate}.
 * 
 */
public class CoordinateImpl implements Coordinate, Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 5701258626041995655L;

    private double x;

    private double y;

    /**
     * Constructor with coordinates.
     * 
     * @param x
     * @param y
     */
    public CoordinateImpl(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.seitenbau.govdata.odp.registry.model.Coordinate#getX()
     */
    @Override
    public double getX()
    {
        return x;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.seitenbau.govdata.odp.registry.model.Coordinate#getY()
     */
    @Override
    public double getY()
    {
        return y;
    }

}
