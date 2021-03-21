/*
 * This file is part of Chimera by Arcane Arts.
 *
 * Chimera by Arcane Arts is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Chimera by Arcane Arts is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License in this package for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Chimera.  If not, see <https://www.gnu.org/licenses/>.
 */

package art.arcane.chimera.core.protocol.generation;

import lombok.Data;

@Data
public class WrappedObject {
    private String type;
    private String generic;
    private Object object;

    public static WrappedObject of(Object o) {
        WrappedObject w = new WrappedObject();
        w.setObject(o);
        w.setType(o.getClass().getSimpleName());

        return w;
    }

    public static WrappedObject ofPrimitiveType(Object o) {
        return ofTyped(o, "!");
    }

    public static WrappedObject ofTyped(Object o, String type) {
        WrappedObject w = new WrappedObject();
        w.setObject(o);
        w.setGeneric(type);
        w.setType(o.getClass().getSimpleName());

        return w;
    }
}
