package www.leigq.com.xposedtest2.util;

import android.annotation.TargetApi;
import android.os.Build;
import dalvik.system.PathClassLoader;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Xposed模块开发,免重启改进方案,测试了没有用
 * <p>
 * 创建人：leigq <br>
 * 创建时间：2018-11-30 14:22 <br>
 * <p>
 * 修改人： <br>
 * 修改时间： <br>
 * 修改备注： <br>
 * </p>
 */
public class HandleHookUtil {

    /**
     * 需要在模块的入口处调用此方法
     * <br/>
     * 参考:
     * <ul>
     * <li>
     * <a href='https://blog.csdn.net/u011956004/article/details/78612502?locationNum=8&fps=1'>Xposed模块开发,免重启改进方案</a>
     * </li>
     * <br>创建人： leigq
     * <br>创建时间： 2018-11-30 14:16
     * <br>
     *
     * @param handleHookClass  处理Hook的类
     * @param loadPackageParam 加载包参数
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void invokeHandleHookMethod(String pkg, String handleHookClass, String handleHookMethodName, XC_LoadPackage.LoadPackageParam loadPackageParam) throws FileNotFoundException {

        File apkFile;
        File path = new File(String.format("/data/app/%s-%s", pkg, "1"));
        if (!path.exists()) {
            path = new File(String.format("/data/app/%s-%s", pkg, "2"));
        }
        if (!path.exists() || !path.isDirectory()) {
            throw new FileNotFoundException(String.format("没找到目录/data/app/%s-%s", pkg, "1/2"));
        }
        apkFile = new File(path, "base.apk");
        if (!apkFile.exists() || apkFile.isDirectory()) {
            throw new FileNotFoundException(String.format("没找到文件/data/app/%s-%s/base.apk", pkg, "1/2"));
        }

        XposedBridge.log("在/data/app找到了APK文件, path:" + apkFile.getPath());

        try {
            //使用反射的方式去调用具体的Hook逻辑
            PathClassLoader pathClassLoader = new PathClassLoader(apkFile.getPath(), ClassLoader.getSystemClassLoader());
            Class<?> cls = Class.forName(handleHookClass, true, pathClassLoader);
            Method method = cls.getDeclaredMethod(handleHookMethodName, XC_LoadPackage.LoadPackageParam.class);
            method.invoke(cls.newInstance(), loadPackageParam);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            XposedBridge.log("调用Handle Hook方法异常:" + e);
        }
    }
}
