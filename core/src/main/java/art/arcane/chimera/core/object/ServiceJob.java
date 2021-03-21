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

package art.arcane.chimera.core.object;

import art.arcane.archon.element.Element;
import art.arcane.archon.element.Identity;
import art.arcane.archon.element.Type;
import art.arcane.chimera.core.protocol.generation.Dart;
import art.arcane.quill.collections.ID;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Dart
@Builder
public class ServiceJob extends Element {
    @Identity
    @Builder.Default
    private ID id = new ID();

    @Type("VARCHAR(64)")
    private String service;

    @Type("VARCHAR(64)")
    private String function;

    @Type("TEXT")
    private String parameters;

    @Builder.Default
    private long deadline = 0;

    @Builder.Default
    private long ttl = 0;

    public ServiceJob encodeParameters(Object[] parameters) {
        setParameters(ServiceJobParameters.builder().parameters(parameters).build().toJson());
        return this;
    }

    public Object[] decodeParameters() {
        try {
            return ServiceJobParameters.fromJson(getParameters()).getParameters();
        } catch (Throwable e) {
            return new Object[0];
        }
    }

    @Override
    public String getTableName() {
        return "jobs";
    }
}
