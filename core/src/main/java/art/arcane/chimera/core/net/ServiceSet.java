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

package art.arcane.chimera.core.net;

import art.arcane.chimera.core.object.HostedService;
import art.arcane.quill.collections.KList;

public class ServiceSet extends KList<HostedService> {
    private int atIndex = 0;

    public HostedService getNextService() {
        if (size() == 0) {
            return null;
        }

        if (!hasIndex(atIndex)) {
            atIndex = 0;
        }

        return get(atIndex++);
    }
}
