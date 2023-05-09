/**
 * Copyright (c) 2012, 2013 Fraunhofer Institute FOKUS
 *
 * This file is part of Open Data Platform.
 *
 * Open Data Platform is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * Open Data Plaform is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with Open Data Platform.  If not, see <http://www.gnu.org/licenses/agpl-3.0>.
 */

/**
 * 
 */
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
