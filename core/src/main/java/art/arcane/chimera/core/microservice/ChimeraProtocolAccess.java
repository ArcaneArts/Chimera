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

package art.arcane.chimera.core.microservice;

import art.arcane.chimera.core.Chimera;
import art.arcane.chimera.core.net.parcels.ParcelInvoke;
import art.arcane.chimera.core.net.parcels.ParcelInvokeDownstream;
import art.arcane.chimera.core.net.parcels.ParcelResult;
import art.arcane.chimera.core.object.HostedService;
import art.arcane.chimera.core.protocol.ChimeraContext;
import art.arcane.chimera.core.protocol.EDN;
import art.arcane.chimera.core.protocol.generation.ProtoFunction;
import art.arcane.chimera.core.protocol.generation.ProtoParam;
import art.arcane.chimera.core.protocol.generation.ProtoType;
import art.arcane.quill.Quill;
import art.arcane.quill.collections.KList;
import art.arcane.quill.collections.KMap;
import art.arcane.quill.logging.L;
import art.arcane.quill.service.QuillService;
import com.google.gson.Gson;

import java.io.InputStream;

/**
 * Responsible for connecting network functions to this service and vice-versa
 */
public class ChimeraProtocolAccess extends QuillService {
    private transient KMap<String, ProtoFunction> localFunctions = new KMap<>();
    private transient KMap<String, KList<ProtoFunction>> remoteFunctionGroups = new KMap<>();

    /**
     * Registers remote functions from data sources provided
     *
     * @param service   the service to key it with
     * @param functions the functions it is registering
     */
    public void registerRemoteFunctions(HostedService service, KList<ProtoFunction> functions) {
        if (service.getType().equals(Quill.getDelegateModuleName())) {
            return;
        }

        remoteFunctionGroups.put(service.getType(), functions);
        L.v("Registered " + functions.size() + " Functions from " + service.getType());
    }

    /**
     * Check if we have a protocol for a given service
     * (meaning we have functions registered) that came from the given service
     *
     * @param type the service type
     * @return true if there are functions registered for that service type
     */
    public boolean hasProtocolFor(String type) {
        return remoteFunctionGroups.containsKey(type);
    }

    /**
     * Returns all network functions in a list
     *
     * @return the list of functions registered
     */
    public KList<ProtoFunction> getAllFunctions() {
        KList<ProtoFunction> functions = new KList<>();

        functions.addAll(localFunctions.values());

        for (KList<ProtoFunction> i : remoteFunctionGroups.v()) {
            functions.addAll(i);
        }

        return functions;
    }

    /**
     * Execute a network function with type. This is capable of
     * executing local & remote functions
     *
     * @param name       the name
     * @param parameters the params
     * @return the result
     */
    public Object execute(String name, Object... parameters) {
        return executeType(null, name, parameters);
    }

    /**
     * Execute a network function with downstream. This is capable of
     * executing local & remote functions
     *
     * @param name       the name
     * @param parameters the params
     * @return the result downstream
     */
    public InputStream executeDownstream(String name, Object... parameters) {
        return executeDownstreamType(null, name, parameters);
    }

    /**
     * Executes a function type with context
     *
     * @param context    the connection context
     * @param type       the type
     * @param name       the name
     * @param parameters the parameters
     * @return the result
     */
    public Object executeTypeWithContext(ChimeraContext context, String type, String name, Object... parameters) {
        if (localFunctions.containsKey(name)) {
            try {
                Object[] locals = new Object[parameters.length];
                ProtoFunction f = localFunctions.get(name);

                if (type == null || type.equals(f.getType())) {
                    if (f.getParams().size() != locals.length) {
                        L.f("Parameter Mismatch on " + f.toString());
                    }

                    for (int i = 0; i < locals.length; i++) {
                        ProtoParam p = f.getParams().get(i);

                        if (p.getType().equals(ProtoType.JSON_OBJECT)) {
                            locals[i] = new Gson().fromJson(new Gson().toJson(parameters[i]), Class.forName(p.getRealType()));
                        }

                        if (p.getType().equals(ProtoType.JSON_LIST)) {
                            locals[i] = new Gson().fromJson(new Gson().toJson(parameters[i]), Class.forName(p.getRealType()));
                        } else {
                            locals[i] = parameters[i];
                        }
                    }

                    return localFunctions.get(name).invokeWithContext(context, locals);
                }
            } catch (Throwable throwable) {
                L.ex(throwable);
            }
        }

        for (String i : remoteFunctionGroups.keySet()) {
            for (ProtoFunction j : remoteFunctionGroups.get(i)) {
                if (j.getName().equals(name) && (type == null || type.equals(j.getType()))) {
                    return invokeRemoteWithContext(context, i, j, parameters);
                }
            }
        }

        if (context != null) {
            try {
                EDN.CLIENT.Hawkeye.snackConnectionError(context.getSessionId(), "Partial Service Outage", "Your Connection could not access the function '" + name + "' on any active service.", true);
            } catch (Throwable ignored) {

            }
        }

        L.w("Cannot find function " + name);

        return null;
    }

    /**
     * Executes a function type with context downstream
     *
     * @param context    the connection context
     * @param type       the type
     * @param name       the name
     * @param parameters the parameters
     * @return the result downstream
     */
    public InputStream executeTypeDownstreamWithContext(ChimeraContext context, String type, String name, Object... parameters) {
        if (localFunctions.containsKey(name)) {
            try {
                Object[] locals = new Object[parameters.length];
                ProtoFunction f = localFunctions.get(name);

                if (type == null || type.equals(f.getType())) {
                    if (f.getParams().size() != locals.length) {
                        L.f("Parameter Mismatch on " + f.toString());
                    }

                    for (int i = 0; i < locals.length; i++) {
                        ProtoParam p = f.getParams().get(i);

                        if (p.getType().equals(ProtoType.JSON_OBJECT)) {
                            locals[i] = new Gson().fromJson(new Gson().toJson(parameters[i]), Class.forName(p.getRealType()));
                        }

                        if (p.getType().equals(ProtoType.JSON_LIST)) {
                            locals[i] = new Gson().fromJson(new Gson().toJson(parameters[i]), Class.forName(p.getRealType()));
                        } else {
                            locals[i] = parameters[i];
                        }
                    }

                    ProtoFunction ff = localFunctions.get(name);

                    if (ff.isDownstreamResult()) {
                        return ff.invokeDownstreamWithContext(context, locals);
                    } else {
                        L.w("Cannot find downstream function " + name + " (but we did find " + name + " as a normal function)");
                    }
                }
            } catch (Throwable throwable) {
                L.ex(throwable);
            }
        }

        for (String i : remoteFunctionGroups.keySet()) {
            for (ProtoFunction j : remoteFunctionGroups.get(i)) {
                if (j.getName().equals(name) && (type == null || type.equals(j.getType())) && j.isDownstreamResult()) {
                    return invokeRemoteDownstreamWithContext(context, i, j, parameters);
                }
            }
        }

        if (context != null) {
            try {
                EDN.CLIENT.Hawkeye.snackConnectionError(context.getSessionId(), "Partial Service Outage", "Your Connection could not access the function '" + name + "' on any active service.", true);
            } catch (Throwable ignored) {

            }
        }

        L.w("Cannot find downstream function " + name);

        return null;
    }

    public InputStream executeDownstreamType(String type, String name, Object... parameters) {
        return executeTypeDownstreamWithContext(null, type, name, parameters);
    }

    public Object executeType(String type, String name, Object... parameters) {
        return executeTypeWithContext(null, type, name, parameters);
    }

    private Object invokeRemoteWithContext(ChimeraContext context, String service, ProtoFunction j, Object[] parameters) {
        ParcelInvoke invoke = new ParcelInvoke();
        invoke.setMethod(j.getName());
        invoke.setContext(context);
        invoke.setParameters(new KList<>(parameters));
        ChimeraService c = Chimera.backend;
        ParcelResult result = (ParcelResult) c.request(j.getService(), invoke);

        if (j.getResult().equals(ProtoType.JSON_OBJECT)) {
            try {
                result.setResult(new Gson().fromJson(new Gson().toJson(result.getResult()), Class.forName(j.getResultType())));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return result.getResult();
    }

    private InputStream invokeRemoteDownstreamWithContext(ChimeraContext context, String service, ProtoFunction j, Object[] parameters) {
        ParcelInvokeDownstream invoke = new ParcelInvokeDownstream();
        invoke.setMethod(j.getName());
        invoke.setContext(context);
        invoke.setParameters(new KList<>(parameters));

        return Chimera.backend.requestDownstream(j.getService(), invoke);
    }

    private Object invokeRemote(String service, ProtoFunction j, Object[] parameters) {
        return invokeRemoteWithContext(null, service, j, parameters);
    }

    @Override
    public void onEnable() {
        Quill.postJob(() -> {
            if (Chimera.backend.getFunctions() == null) {
                Quill.crashStack("Did you forget to add super.onEnable() and super.onDisable() in your chimera service?");
            }

            for (ProtoFunction i : Chimera.backend.getFunctions()) {
                if (localFunctions.containsKey(i.getName())) {
                    Quill.crashStack("Duplicate Function! " + i.toString() + " AND " + localFunctions.get(i.getName()).toString());
                    return;
                }

                localFunctions.put(i.getName(), i);
            }
        });
    }

    @Override
    public void onDisable() {

    }
}
