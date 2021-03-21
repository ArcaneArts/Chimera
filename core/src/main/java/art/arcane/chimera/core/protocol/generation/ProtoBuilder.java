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

import art.arcane.chimera.core.protocol.EDX;
import art.arcane.quill.collections.KList;

import java.lang.reflect.Method;

public class ProtoBuilder {
    public static KList<ProtoFunction> functions(Class<?> c, Object instance) {
        KList<ProtoFunction> f = new KList<>();

        for (Method i : c.getDeclaredMethods()) {
            if (i.isAnnotationPresent(ServiceFunction.class)) {
                f.add(ProtoFunction.of(i, instance, EDX.TYPE_SERVICE));
            }

            if (i.isAnnotationPresent(GatewayFunction.class)) {
                f.add(ProtoFunction.of(i, instance, EDX.TYPE_GATEWAY));
            }

            if (i.isAnnotationPresent(ClientFunction.class)) {
                f.add(ProtoFunction.of(i, instance, EDX.TYPE_CLIENT));
            }
        }

        return f;
    }
}
