package art.arcane.chimera.account;

import art.arcane.chimera.core.object.account.AccessToken;
import art.arcane.chimera.core.object.account.User;
import art.arcane.chimera.core.object.account.UserAuthentication;
import art.arcane.chimera.core.object.account.UserPersonal;
import art.arcane.chimera.core.protocol.ChimeraContext;
import art.arcane.chimera.core.protocol.EDN;
import art.arcane.chimera.core.protocol.EDX;
import art.arcane.chimera.core.protocol.generation.GatewayFunction;
import art.arcane.chimera.core.protocol.generation.ServiceFunction;
import art.arcane.quill.collections.ID;
import art.arcane.quill.logging.L;
import art.arcane.quill.math.M;
import art.arcane.quill.service.QuillService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProtoAccount extends QuillService {
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

    private UserPersonal getContextUserPersonal() {
        return getUserPersonal(getContextUserID());
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
        String session = EDX.getContext().getSessionId();
        if (fresh != null) {
            if (EDX.getContext() != null) {
                EDX.getContext().setAccessToken(fresh);
                EDX.getContext().push();
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
            EDN.CLIENT.Hawkeye.snack2(EDX.getContext().getSessionId(), "Failed to Create Account", "Your session is already authenticated. Please restart the app.", true);
            return null;
        }

        if (accountEmailExists(email)) {
            EDN.CLIENT.Hawkeye.snack2(EDX.getContext().getSessionId(), "Failed to Create Account", "The email " + email + " is already being used by an existing account.", true);
            return null;
        }

        User u = createUser(new ID(), email, password);
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.push();
        EDN.CLIENT.Hawkeye.snack(EDX.getContext().getSessionId(), "Welcome Aboard " + firstName, true);

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
            a.delete();
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
            EDN.CLIENT.Hawkeye.snack(EDX.getContext().getSessionId(), "Welcome Back " + getUser(id).getFirstName() + "!", true);
            return a;
        } else {
            EDN.CLIENT.Hawkeye.snack2(EDX.getContext().getSessionId(), "Failed to Sign In", "Verify your email & password are correct.", true);
        }

        return null;
    }

    @ServiceFunction
    public Boolean accountEmailExists(String email) {
        return getUserByEmail(email) != null;
    }

    @ServiceFunction
    public ID getUserByEmail(String email) {

        return User.builder().build().getIdentityWhere("email", email);
    }

    @ServiceFunction
    public Boolean setPhone(ID id, String carrier, String phone) {
        UserPersonal u = getUserPersonal(id);

        if (u != null) {
            u.setCarrier(carrier);
            u.setPhone(phone);
            u.push();
            return true;
        }

        return false;
    }

    @ServiceFunction
    public Boolean setFirstName(ID id, String firstName) {
        User u = getUser(id);

        if (u != null) {
            u.setFirstName(firstName);
            u.push();
            return true;
        }

        return false;
    }

    @ServiceFunction
    public Boolean setLastName(ID id, String lastName) {
        User u = getUser(id);

        if (u != null) {
            u.setLastName(lastName);
            u.push();
            return true;
        }

        return false;
    }

    @ServiceFunction
    public Long getUserCount() {
        return User.builder().build().tableSize();
    }

    @ServiceFunction
    public Boolean changeEmail(ID id, String newEmail) {
        User u = getUser(id);

        if (u != null) {
            u.setEmail(newEmail);
            u.push();
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

        new UserAuthentication(id, newPassword).push();
        return true;
    }

    @ServiceFunction
    public User createUser(ID id, String email, String password) {
        if (password.trim().length() != 64) {
            L.w("Password length not 64");
            return null;
        }

        User u = User.builder().id(id).email(email).build();
        L.w("It is " + u.getFirstName());
        u.push();
        UserAuthentication.builder().id(id).build().setPassword(password).push();
        UserPersonal.builder().id(id).build().push();

        return u;
    }

    @ServiceFunction
    public User getUser(ID id) {
        User u = User.builder().id(id).build();

        if (u.pull()) {
            return u;
        }

        return null;
    }

    @ServiceFunction
    public UserPersonal getUserPersonal(ID id) {
        UserPersonal u = UserPersonal.builder().id(id).build();
        if (u.pull()) {
            return u;
        }

        return null;
    }

    @ServiceFunction
    public UserAuthentication getUserAuthentication(ID id) {
        UserAuthentication u = UserAuthentication.builder().id(id).build();

        if (u.pull()) {
            return u;
        }

        return null;
    }

    @ServiceFunction
    public Boolean authenticate(ID id, String password) {
        UserAuthentication a = getUserAuthentication(id);
        return a != null && a.auth(password);
    }

    @ServiceFunction
    public Long getAccessTokenCount() {
        return AccessToken.builder().build().tableSize();
    }

    @ServiceFunction
    public Boolean setUserSuspension(ID account, Boolean suspended) {
        User u = getUser(account);
        u.setSuspended(suspended);
        u.push();
        return true;
    }

    @ServiceFunction
    public AccessToken createAccessToken(ID account) {
        return createAccessTokenWithType(account, "normal");
    }

    @ServiceFunction
    public AccessToken createAccessTokenWithType(ID account, String type) {
        AccessToken a = AccessToken.builder()
                .account(account)
                .type(type)
                .build();
        a.push();

        return a;
    }

    @ServiceFunction
    public AccessToken getAccessToken(ID id) {
        AccessToken a = AccessToken.builder().id(id).build();

        if (a.pull()) {
            return a;
        }

        return null;
    }

    @ServiceFunction
    public Boolean updateAccessToken(ID id) {
        AccessToken a = getAccessToken(id);

        if (a != null) {
            a.setLastUse(M.ms());
            a.push();
            return true;
        }

        return false;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
