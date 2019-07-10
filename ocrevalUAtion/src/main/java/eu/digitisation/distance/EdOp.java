/*
 * Copyright (C) 2013 Universidad de Alicante
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package eu.digitisation.distance;

/**
 *
 * Basic edit operations on a single character
 *
 * @author R.C.C.
 */
@SuppressWarnings("javadoc")
public enum EdOp {

    KEEP, INSERT, SUBSTITUTE, DELETE;

    @Override
    public String toString() {
        switch (this) {
            case KEEP:
                return "K";
            case INSERT:
                return "I";
            case SUBSTITUTE:
                return "S";
            case DELETE:
                return "D";
            default:
                return null;
        }
    }
}
