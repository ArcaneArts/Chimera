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

package art.arcane.chimera.core.util.web;

import art.arcane.quill.collections.KList;

public interface FancyParcelable extends Parcelable {
    public static FancyParcelable of(String html) {
        return new FancyParcelable() {
            @Override
            public String getParcelType() {
                return "fancyparcel";
            }

            @Override
            public KList<String> getParameterNames() {
                return new KList<String>();
            }

            @Override
            public String getHTML() {
                return html;
            }

            @Override
            public String getExample() {
                return "";
            }
        };
    }

    public String getHTML();
}
