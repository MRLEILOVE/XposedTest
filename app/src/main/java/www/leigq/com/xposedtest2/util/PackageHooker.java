package www.leigq.com.xposedtest2.util;

import dalvik.system.DexFile;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;

/**
 * XPosed暴力列举Package下所有的方法调用,测试了微信直接停止运行,不可用,可能是微信版本问题
 * <p>
 * 创建人：leigq <br>
 * 创建时间：2018-12-03 16:14 <br>
 * <p>
 * 修改人： <br>
 * 修改时间： <br>
 * 修改备注： <br>
 * </p>
 */
public class PackageHooker {
    private static PackageHooker packageHooker;
    private final XC_LoadPackage.LoadPackageParam loadPackageParam;

    public static PackageHooker getInstance(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        if (null == packageHooker) {
            packageHooker = new PackageHooker(loadPackageParam);
        }
        return packageHooker;
    }

    private PackageHooker(XC_LoadPackage.LoadPackageParam param) {
        loadPackageParam = param;
    }

    public void hook() throws IOException, ClassNotFoundException {
        DexFile dexFile = new DexFile(loadPackageParam.appInfo.sourceDir);
        Enumeration<String> classNames = dexFile.entries();
        while (classNames.hasMoreElements()) {
            String className = classNames.nextElement();

            if (isClassNameValid(className)) {
                final Class clazz = Class.forName(className, false, loadPackageParam.classLoader);

                for (Method method : clazz.getDeclaredMethods()) {
                    //过滤掉抽象修饰符
                    if (!Modifier.isAbstract(method.getModifiers())) {
                        XposedBridge.hookMethod(method, new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) {
                                XposedBridge.log("HOOKED: " + clazz.getName() + "\\" + param.method.getName());

                                XposedBridge.log("Dump class " + clazz.getName());

                                XposedBridge.log("Methods");
                                Method[] m = clazz.getDeclaredMethods();
                                for (Method aM : m) {
                                    XposedBridge.log(aM.toString());
                                }
                                XposedBridge.log("Fields");
                                Field[] f = clazz.getDeclaredFields();
                                for (Field aF : f) {
                                    XposedBridge.log(aF.toString());
                                }
                                XposedBridge.log("Classes");
                                Class[] c = clazz.getDeclaredClasses();
                                for (Class aC : c) {
                                    XposedBridge.log(aC.toString());
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    private boolean isClassNameValid(String className) {
        return className.startsWith(loadPackageParam.packageName)
                && !className.contains("$")
                && !className.contains("BuildConfig")
                && !className.equals(loadPackageParam.packageName + ".R");
    }

}
