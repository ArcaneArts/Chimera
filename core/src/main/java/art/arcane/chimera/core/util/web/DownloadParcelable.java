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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public interface DownloadParcelable extends Parcelable {
    public static DownloadParcelable of(File file) throws FileNotFoundException {
        return of(new FileInputStream(file), file.getName());
    }

    public static DownloadParcelable of(InputStream in, String fileName) {
        return new DownloadParcelable() {
            @Override
            public String getParcelType() {
                return "fancyparcel";
            }

            public String getName() {
                return fileName;
            }

            @Override
            public KList<String> getParameterNames() {
                return new KList<String>();
            }

            @Override
            public InputStream getStream() {
                return in;
            }

            @Override
            public String getExample() {
                return "";
            }
        };
    }

    public InputStream getStream();

    public String getName();
}
