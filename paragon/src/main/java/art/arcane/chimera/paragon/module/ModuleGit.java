package art.arcane.chimera.paragon.module;

import art.arcane.chimera.core.Chimera;
import art.arcane.chimera.paragon.api.ParagonModule;
import art.arcane.quill.logging.L;
import lombok.Data;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;

@Data
public class ModuleGit extends ParagonModule {
    private String username = "cyberpwnn";
    private String accessToken = "c5769c05b8e8e8fe10796d69486569583707c476";
    private String remote = "https://github.com/MPMTeam/chimera.git";
    private transient CredentialsProvider credentials;
    private transient Git git;

    public ModuleGit() {
        setCredentials(new UsernamePasswordCredentialsProvider(getUsername(), getAccessToken()));
    }

    public void discardChanges() {
        call(getGit().reset().setMode(ResetCommand.ResetType.HARD));
        i("Discarded Changes");
    }

    public void push() {
        PushResult r = call(getGit().push());
        i("Pushed " + getName() + " Successfully");
    }

    public File getFileRepo() {
        return new File(getModuleDirectory(), "chimera");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        try {
            if (getFileRepo().exists() && getFileRepo().listFiles().length > 0) {
                setGit(new Git(new FileRepositoryBuilder()
                        .setGitDir(new File(getFileRepo(), ".git"))
                        .build()));
            } else {
                i("Cloning to " + getFileRepo());
                setGit(Git.cloneRepository()
                        .setCloneAllBranches(true)
                        .setURI(getRemote())
                        .setCredentialsProvider(getCredentials())
                        .setDirectory(getFileRepo()).call());
            }

            pull();
        } catch (Throwable e) {
            f("Failed to clone chimera!");
            L.ex(e);
            Chimera.crashStack("Failed to clone!");
        }
    }

    public void sync() {
        discardChanges();
        pull();
    }

    public void commit(String message) {
        call(getGit().add().addFilepattern("."));
        call(getGit().commit().setMessage(message));
    }

    public void pull() {
        PullResult r = call(getGit().pull());
        if (r.isSuccessful()) {
            i("Pulled " + getName() + " Successfully");
        } else {
            f("Failed to pull");
        }
    }

    private <T> T call(GitCommand<?> c) {
        if (c instanceof TransportCommand) {
            TransportCommand<?, T> t = (TransportCommand<?, T>) c;
            t.setCredentialsProvider(getCredentials());
        }

        try {
            return (T) c.call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean isInstalled() {
        return true;
    }

    @Override
    public void onInstall() {

    }

    @Override
    public void onUninstall() {

    }
}
