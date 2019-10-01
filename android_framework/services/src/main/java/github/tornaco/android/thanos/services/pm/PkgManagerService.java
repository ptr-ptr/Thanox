package github.tornaco.android.thanos.services.pm;

import android.content.Context;
import android.os.IBinder;
import android.os.UserHandle;
import github.tornaco.android.thanos.core.pm.AppInfo;
import github.tornaco.android.thanos.core.pm.IPkgManager;
import github.tornaco.android.thanos.core.util.Noop;
import github.tornaco.android.thanos.core.util.Timber;
import github.tornaco.android.thanos.services.BackgroundThread;
import github.tornaco.android.thanos.services.S;
import github.tornaco.android.thanos.services.SystemService;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class PkgManagerService extends SystemService implements IPkgManager {
    private final S s;

    @Getter
    private final PkgCache pkgCache = new PkgCache();

    @Getter
    private final PackageMonitor monitor = new PackageMonitor() {
        @Override
        public void onPackageAdded(String packageName, int uid) {
            super.onPackageAdded(packageName, uid);
            getPkgCache().invalidate();
        }

        @Override
        public void onPackageRemoved(String packageName, int uid) {
            super.onPackageRemoved(packageName, uid);
            getPkgCache().invalidate();
        }

        @Override
        public boolean onPackageChanged(String packageName, int uid, String[] components) {
            getPkgCache().invalidate();
            return super.onPackageChanged(packageName, uid, components);
        }
    };

    public PkgManagerService(S s) {
        this.s = s;
    }

    @Override
    public void onStart(Context context) {
        super.onStart(context);
        getPkgCache().onStart(context);
    }

    @Override
    public void systemReady() {
        super.systemReady();
        getPkgCache().invalidate();
        getMonitor().register(getContext(), UserHandle.CURRENT, true, BackgroundThread.getHandler());
    }

    @Override
    public void shutdown() {
        super.shutdown();
        getMonitor().unregister();
    }

    @Override
    public String[] getPkgNameForUid(int uid) {
        return getPkgCache().getUid2Pkg().containsKey(uid)
                ? getPkgCache().getUid2Pkg().get(uid).toArray(new String[0])
                : null;
    }

    @Override
    public int getUidForPkgName(String pkgName) {
        Integer uid = getPkgCache().getPkg2Uid().get(pkgName);
        return uid == null ? -1 : uid;
    }

    public int getThanosAppUid() {
        return getPkgCache().getThanosAppUid();
    }

    @Override
    public AppInfo[] getInstalledPkgs(int flags) {
        List<AppInfo> res = new ArrayList<>();
        if ((flags & AppInfo.FLAGS_SYSTEM) != 0) {
            res.addAll(pkgCache.getSystemApps());
            Timber.d("getInstalledPkgs, adding FLAGS_SYSTEM");
        }
        if ((flags & AppInfo.FLAGS_SYSTEM_MEDIA) != 0) {
            res.addAll(pkgCache.getMediaUidApps());
            Timber.d("getInstalledPkgs, adding FLAGS_SYSTEM_MEDIA");
        }
        if ((flags & AppInfo.FLAGS_SYSTEM_PHONE) != 0) {
            res.addAll(pkgCache.getPhoneUidApps());
            Timber.d("getInstalledPkgs, adding FLAGS_SYSTEM_PHONE");
        }
        if ((flags & AppInfo.FLAGS_SYSTEM_UID) != 0) {
            res.addAll(pkgCache.getSystemUidApps());
            Timber.d("getInstalledPkgs, adding FLAGS_SYSTEM_UID");
        }
        if ((flags & AppInfo.FLAGS_USER) != 0) {
            res.addAll(pkgCache.get_3rdApps());
            Timber.d("getInstalledPkgs, adding FLAGS_USER");
        }
        if ((flags & AppInfo.FLAGS_WEB_VIEW_PROVIDER) != 0) {
            res.addAll(pkgCache.getWebViewProviderApps());
            Timber.d("getInstalledPkgs, adding FLAGS_WEB_VIEW_PROVIDER");
        }
        if ((flags & AppInfo.FLAGS_WHITE_LISTED) != 0) {
            res.addAll(pkgCache.getWhiteListApps());
            Timber.d("getInstalledPkgs, adding FLAGS_WHITE_LISTED");
        }
        return res.toArray(new AppInfo[0]);
    }

    @Override
    public AppInfo getAppInfo(String pkgName) {
        return pkgCache.getAllApps().get(pkgName);
    }

    @Override
    public String[] getWhiteListPkgs() {
        return pkgCache.getWhiteList().toArray(new String[0]);
    }

    @Override
    public boolean isPkgInWhiteList(String pkg) {
        return pkgCache.getWhiteList().contains(pkg);
    }

    @Override
    public IBinder asBinder() {
        return Noop.notSupported();
    }
}
