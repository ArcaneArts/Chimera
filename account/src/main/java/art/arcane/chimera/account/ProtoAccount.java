package art.arcane.chimera.account;

import art.arcane.chimera.core.object.account.AccessToken;
import art.arcane.chimera.core.object.account.User;
import art.arcane.chimera.core.object.account.UserAuthentication;
import art.arcane.chimera.core.protocol.ChimeraContext;
import art.arcane.chimera.core.protocol.EDN;
import art.arcane.chimera.core.protocol.EDX;
import art.arcane.chimera.core.protocol.generation.GatewayFunction;
import art.arcane.chimera.core.protocol.generation.ServiceFunction;
import art.arcane.quill.collections.ID;
import art.arcane.quill.execution.J;
import art.arcane.quill.logging.L;
import art.arcane.quill.math.M;
import art.arcane.quill.service.QuillServiceWorker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;

public class ProtoAccount extends QuillServiceWorker {
    private boolean isContextAuthenticated() {
        return EDX.getContext().hasAccessToken();
    }

    private ID getContextUserID() {
        AccessToken a = EDX.getContext().getAccessToken();

        if (a != null) {
            return a.getAccount();
        }

        return null;
    }

    private User getContextUser() {
        return getUser(getContextUserID());
    }

    private UserAuthentication getContextUserAuthentication() {
        return getUserAuthentication(getContextUserID());
    }

    public Boolean isContextPasswordValid(String password) {
        return getContextUserAuthentication().auth(password);
    }

    @GatewayFunction
    public Boolean validateToken(AccessToken token) {
        AccessToken fresh = getAccessToken(token.getId());
        String session = getContext().getSessionId();
        if (fresh != null) {
            if (getContext() != null) {
                getContext().setAccessToken(fresh);
                getContext().push();
            }

            return true;
        }

        return false;
    }

    @GatewayFunction
    public User getMe() {
        return getContextUser();
    }

    @GatewayFunction
    public Boolean requestChangePassword(String currentPassword, String newPassword) {
        User u = getContextUser();

        if (isContextPasswordValid(currentPassword)) {
            changePassword(u.getId(), newPassword);
            return true;
        }

        return false;
    }

    @GatewayFunction
    public User signUp(String firstName, String lastName, String email, String password) {
        // If the current context is already signed in?
        if (isContextAuthenticated()) {
            EDN.CLIENT.Hawkeye.snack2(getContext().getSessionId(), "Failed to Create Account", "Your session is already authenticated. Please restart the app.", true);
            return null;
        }

        if (accountEmailExists(email)) {
            EDN.CLIENT.Hawkeye.snack2(getContext().getSessionId(), "Failed to Create Account", "The email " + email + " is already being used by an existing account.", true);
            return null;
        }

        User u = createUser(ID.randomUUID(), email, password);
        u.setFirstName(firstName);
        u.setLastName(lastName);
        getDatabase().set(u);
        EDN.CLIENT.Hawkeye.snack(getContext().getSessionId(), "Welcome Aboard " + firstName, true);

        return u;
    }

    @GatewayFunction
    public InputStream getSomeStream() {
        try {
            return new FileInputStream(new File("derp.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    @GatewayFunction
    public Boolean releaseToken() {
        AccessToken a = EDX.getContext().getAccessToken();

        if (a != null) {
            EDX.getContext().setAccessToken(null);
            EDX.getContext().push();
            J.a(() -> getDatabase().delete(a));
            return true;
        }

        return false;
    }

    @GatewayFunction
    public AccessToken aquireToken(String email, String password) {
        ChimeraContext context = EDX.getContext();
        ID id = getUserByEmail(email);

        if (id != null && authenticate(id, password)) {
            AccessToken a = createAccessToken(id);
            context.setAccessToken(a);
            EDN.CLIENT.Hawkeye.snack(getContext().getSessionId(), "Welcome Back " + getUser(id).getFirstName() + "!", true);
            return a;
        } else {
            EDN.CLIENT.Hawkeye.snack2(getContext().getSessionId(), "Failed to Sign In", "Verify your email & password are correct.", true);
        }

        return null;
    }

    @ServiceFunction
    public Boolean accountEmailExists(String email) {
        return getUserByEmail(email) != null;
    }

    @ServiceFunction
    public ID getUserByEmail(String email) {
        User u = new User(ID.randomUUID());
        u.setEmail(email);

        try {
            if (getDatabase().getSql().getWhere(u, "email", email)) {
                return u.getId();
            }
        } catch (Throwable throwables) {
            L.ex(throwables);
        }

        return null;
    }

    @ServiceFunction
    public Boolean setPhone(ID id, String carrier, String phone) {
        UserPersonal u = getUserPersonal(id);

        if (u != null) {
            u.setCarrier(carrier);
            u.setPhone(phone);
            getDatabase().set(u);
            return true;
        }

        return false;
    }

    @ServiceFunction
    public Boolean setFirstName(ID id, String firstName) {
        User u = getUser(id);

        if (u != null) {
            u.setFirstName(firstName);
            getDatabase().set(u);
            return true;
        }

        return false;
    }

    @ServiceFunction
    public Boolean setLastName(ID id, String lastName) {
        User u = getUser(id);

        if (u != null) {
            u.setLastName(lastName);
            getDatabase().set(u);
            return true;
        }

        return false;
    }

    @ServiceFunction
    public Long getUserCount() {
        try {
            return getDatabase().getSql().getTableSize(User.class);
        } catch (SQLException | IllegalAccessException throwables) {
            throwables.printStackTrace();
        }

        return -1l;
    }

    @ServiceFunction
    public Boolean changeEmail(ID id, String newEmail) {
        User u = getUser(id);

        if (u != null) {
            u.setEmail(newEmail);
            getDatabase().set(u);
            return true;
        }

        return false;
    }

    @ServiceFunction
    public Boolean changePassword(ID id, String newPassword) {
        if (newPassword.trim().length() != 64) {
            L.w("Password length not 64");
            return false;
        }

        return getDatabase().set(new UserAuthentication(id, newPassword));
    }

    @ServiceFunction
    public User createUser(ID id, String email, String password) {
        if (password.trim().length() != 64) {
            L.w("Password length not 64");
            return null;
        }

        User u = new User(id, email);
        u.setCreatedDate(M.ms());
        UserAuthentication a = new UserAuthentication(id, password);
        UserPersonal p = new UserPersonal(id);
        if (!getDatabase().set(u, a, p)) {
            getDatabase().delete(u, a, p);
            return null;
        }

        return u;
    }

    @ServiceFunction
    public User getUser(ID id) {
        return getDatabase().get(User.class, id);
    }

    @ServiceFunction
    public UserPersonal getUserPersonal(ID id) {
        return getDatabase().get(UserPersonal.class, id);
    }

    @ServiceFunction
    public UserAuthentication getUserAuthentication(ID id) {
        return getDatabase().get(UserAuthentication.class, id);
    }

    @ServiceFunction
    public Boolean authenticate(ID id, String password) {
        UserAuthentication a = getDatabase().get(UserAuthentication.class, id);
        return a != null && a.auth(password);
    }

    @ServiceFunction
    public Long getAccessTokenCount() {
        try {
            return getDatabase().getSql().getTableSize(AccessToken.class);
        } catch (SQLException | IllegalAccessException throwables) {
            throwables.printStackTrace();
        }

        return -1l;
    }

    @ServiceFunction
    public Boolean setUserSuspension(ID account, Boolean suspended) {
        User u = getUser(account);
        u.setSuspended(suspended);

        return getDatabase().set(u);
    }

    @ServiceFunction
    public AccessToken createAccessToken(ID account) {
        return createAccessTokenWithType(account, "normal");
    }

    @ServiceFunction
    public AccessToken createAccessTokenWithType(ID account, String type) {
        AccessToken a = new AccessToken(ID.randomUUID());
        a.setAccount(account);
        a.setType(type);
        a.setLastUse(M.ms());
        getDatabase().set(a);

        return a;
    }

    @ServiceFunction
    public AccessToken getAccessToken(ID id) {
        return getDatabase().get(AccessToken.class, id);
    }

    @ServiceFunction
    public Boolean updateAccessToken(ID id) {
        AccessToken a = getAccessToken(id);

        if (a != null) {
            a.setLastUse(M.ms());
            getDatabase().setAsync(a);
            return true;
        }

        return false;
    }

    @Override
    public void onEnable() {
        getDatabase().validate(new User(ID.randomUUID()));
        getDatabase().validate(new UserAuthentication(ID.randomUUID()));
        getDatabase().validate(new UserPersonal(ID.randomUUID()));
    }

    @Override
    public void onDisable() {

    }
}
