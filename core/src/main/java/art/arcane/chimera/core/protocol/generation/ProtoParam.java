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

import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Parameter;

@Data
@Builder
public class ProtoParam {
    private String name;
    private ProtoType type;
    private String description;
    private String realType;
    private String t1;
    private String t2;

    public static ProtoParam of(Parameter i) {
        //@builder
        return ProtoParam.builder()
                .name(i.getName())
                .description("No Description Provided")
                .realType(i.getType().getCanonicalName())
                .type(ProtoType.of(i.getType()))
                .t1(ProtoExport.listTypeOfJava(i, 0, i.getDeclaringExecutable().getDeclaringClass().getCanonicalName()))
                .t2(ProtoExport.listTypeOfJava(i, 1, i.getDeclaringExecutable().getDeclaringClass().getCanonicalName()))
                .build();
        //@done
    }

    public String getFixedType() {
        if (getRealType().equals("double")) {
            return Double.class.getCanonicalName();
        }
        if (getRealType().equals("int")) {
            return Integer.class.getCanonicalName();
        }
        if (getRealType().equals("long")) {
            return Long.class.getCanonicalName();
        }
        if (getRealType().equals("boolean")) {
            return Boolean.class.getCanonicalName();
        }

        return getRealType();
    }
}
