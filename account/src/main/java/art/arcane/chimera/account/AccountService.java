package art.arcane.chimera.account;


import art.arcane.chimera.core.Chimera;
import art.arcane.chimera.core.microservice.ChimeraService;
import art.arcane.chimera.core.object.account.AccessToken;
import art.arcane.chimera.core.object.account.User;
import art.arcane.chimera.core.object.account.UserAuthentication;
import art.arcane.chimera.core.object.account.UserPersonal;
import art.arcane.chimera.core.protocol.EDN;
import art.arcane.chimera.core.protocol.generation.Protocol;
import art.arcane.quill.collections.ID;
import art.arcane.quill.io.IO;
import art.arcane.quill.logging.L;
import art.arcane.quill.service.Service;

public class AccountService extends ChimeraService {

    public static void main(String[] a) {
        Chimera.start(a);
    }

    @Service
    @Protocol
    private ProtoAccount account = new ProtoAccount();

    public AccountService() {

    }

    @Override
    public void onEnable() {
        super.onEnable();
        getConsole().registerCommand("create-user", (a) ->
        {
            if (a.length != 2) {
                L.f("create-user <email> <password>");
                return true;
            }

            if (EDN.SERVICE.Account.accountEmailExists(a[0])) {
                L.f("Email account " + a[0] + " already exists!");
                return true;
            }

            User u = EDN.SERVICE.Account.createUser(new ID(), a[0], IO.hash(a[1]).toLowerCase());

            if (u != null) {
                L.i("Created " + u.toString());
            } else {
                L.f("Failed? Null!");
            }

            return true;
        });

        new User().sync();
        new AccessToken().sync();
        new UserAuthentication().sync();
        new UserPersonal().sync();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
