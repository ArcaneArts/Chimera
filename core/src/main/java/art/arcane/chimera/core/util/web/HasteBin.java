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

import art.arcane.quill.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Paste to the web
 *
 * @author cyberpwn
 */
public class HasteBin {
    /**
     * Paste to hastebin.com
     *
     * @param s the paste text (use newline chars for new lines)
     * @return the url to access the paste
     * @throws Exception shit happens
     */
    public static String paste(String s) throws Exception {
        URL url = new URL("https://hastebin.com/documents");
        HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setDoOutput(true);
        httpCon.setRequestMethod("POST");
        httpCon.getOutputStream().write(s.getBytes());
        BufferedReader in = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
        JSONObject jso = new JSONObject(in.readLine());

        return "https://hastebin.com/" + jso.getString("key");
    }
}