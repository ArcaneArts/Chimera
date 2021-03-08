package art.arcane.chimera.core.microservice;

import art.arcane.chimera.core.Chimera;
import art.arcane.chimera.core.object.ServiceJob;
import art.arcane.chimera.core.protocol.EDX;
import art.arcane.quill.collections.KList;
import art.arcane.quill.logging.L;
import art.arcane.quill.math.M;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ChimeraJobServiceWorker extends ChimeraTickingServiceWorker {
    private boolean logJobExecutions = true;

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onTick() {
        String serviceType = Chimera.delegate.getServiceName().toLowerCase();

        try {
            ResultSet s = getServiceDatabase().getSql().getConnection().prepareStatement("SELECT `id`,`function`,`parameters` FROM `jobs` WHERE `service` = '" + serviceType + "' AND `ttl` < " + M.ms() + " ORDER BY `deadline` DESC LIMIT 1;").executeQuery();

            if (s.next()) {
                String id = s.getString(1);

                if (getServiceDatabase().getSql().getConnection().prepareStatement("DELETE FROM `jobs` WHERE `id` = '" + id + "' LIMIT 1;").executeUpdate() == 1) {
                    String f;
                    KList<Object> pars = new KList<Object>(ServiceJob.builder().function(f = s.getString(2))
                            .parameters(s.getString(3)).id(id).build().decodeParameters());
                    Object r = EDX.invoke(f, pars);

                    if (logJobExecutions) {
                        String re = r == null ? "null" : r.toString();
                        L.v("Executed Job " + id + " " + f + "(" + pars.toString(", ") + ") -> " + re);
                    }
                } else {
                    L.w("Skipping job execution: " + id + " because upon deleting the job, it was already deleted. Pretty sure another " + serviceType + " is executing it already.");
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
