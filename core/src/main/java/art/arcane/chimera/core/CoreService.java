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

package art.arcane.chimera.core;

import art.arcane.quill.Quill;
import art.arcane.quill.logging.L;
import art.arcane.quill.service.QuillService;

public class CoreService extends QuillService {
    public static void main(String[] a) {
        L.i("Starting Core Service for Utility Purposes Only!");
        Chimera.start(a);
    }

    @Override
    public void onEnable() {
        Quill.crashStack("You cannot start the core service! It is for build tools only!");
    }

    @Override
    public void onDisable() {
        Quill.crashStack("You cannot stop the core service! It is for build tools only!");
    }
}
