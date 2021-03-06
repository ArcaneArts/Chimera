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

package art.arcane.chimera.core.protocol;

import art.arcane.chimera.core.microservice.ChimeraService;
import art.arcane.chimera.core.protocol.generation.ProtoFunction;
import art.arcane.quill.Quill;
import art.arcane.quill.collections.KList;
import art.arcane.quill.logging.L;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

public class EDX {
    public static final String TYPE_JOB = "job";
    public static final String TYPE_SERVICE = "service";
    public static final String TYPE_GATEWAY = "gateway";
    public static final String TYPE_CLIENT = "client";
    private static final LoadingCache<Long, ChimeraContext> threadContext = Caffeine
            .newBuilder()
            .expireAfterAccess(60, TimeUnit.SECONDS)
            .build((i) -> null);

    public static ChimeraContext getContext() {
        ChimeraContext c = threadContext.get(Thread.currentThread().getId());

        if (c == null) {
            L.w("WARNING: CONTEXT IS NULL AT");
            L.pss();
        }

        return c;
    }

    public static void pushContext(ChimeraContext context) {
        if (context == null) {
            return;
        }

        threadContext.put(Thread.currentThread().getId(), context);
    }

    public static Object invoke(String function, KList<Object> objects) {
        return invokeType(null, function, objects);
    }

    public static Object invokeType(String type, String function, KList<Object> objects) {
        return ((ChimeraService) Quill.delegate).getProtocolAccess().executeType(type, function, objects.toArray(new Object[0]));
    }

    public static Object invokeTypeWithContext(ChimeraContext context, String type, String function, KList<Object> objects) {
        return ((ChimeraService) Quill.delegate).getProtocolAccess().executeTypeWithContext(context, type, function, objects.toArray(new Object[0]));
    }

    public static KList<ProtoFunction> getAllFunctions() {
        return ((ChimeraService) Quill.delegate).getProtocolAccess().getAllFunctions();
    }

    public static KList<ProtoFunction> getAllFunctionsOfType(String type) {
        return getAllFunctions().filter((i) -> i.getType().equals(type));
    }

    public static KList<ProtoFunction> getAllFunctionsOfService(String svc) {
        return getAllFunctions().filter((i) -> i.getService().equals(svc));
    }

    public static KList<ProtoFunction> getAllFunctionsOfTypeAndService(String type, String svc) {
        return getAllFunctions().filter((i) -> i.getType().equals(type)).filter((i) -> i.getService().equals(svc));
    }
}
