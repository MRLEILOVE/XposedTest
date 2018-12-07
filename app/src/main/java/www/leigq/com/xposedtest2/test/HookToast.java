package www.leigq.com.xposedtest2.test;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import www.leigq.com.xposedtest2.util.HandleHookUtil;

/**
 *  Hook自己创建的Toast案例
 * <p>
 * 创建人：leigq <br>
 * 创建时间：2018-11-30 14:38 <br>
 * <p>
 * 修改人： <br>
 * 修改时间： <br>
 * 修改备注： <br>
 * </p>
 */
public class HookToast implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        XposedBridge.log("进入handleLoadPackage....");
        XposedBridge.log(String.format("loadPackageParam.packageName is %s", loadPackageParam.packageName));

//        HandleHookUtil.invokeHandleHookMethod("www.leigq.com.xposedtest2", "www.leigq.com.xposedtest2.test.ToastActivity", "getToastMsg", loadPackageParam);

        if (loadPackageParam.packageName.equals("www.leigq.com.xposedtest2")) {

            XposedBridge.log("匹配到www.leigq.com.xposedtest2包");

            Class clazz = loadPackageParam.classLoader.loadClass("www.leigq.com.xposedtest2.test.ToastActivity");

            XposedHelpers.findAndHookMethod(clazz, "getToastMsg", new XC_MethodHook() {
                protected void beforeHookedMethod(MethodHookParam param) {
                    XposedBridge.log("beforeHookedMethod.....");
                }

                protected void afterHookedMethod(MethodHookParam param) {
                    XposedBridge.log("afterHookedMethod.....");
                    try {
                        XposedBridge.log(String.format("param.getResultOrThrowable() is %s", param.getResultOrThrowable()));
                    } catch (Throwable throwable) {
                        XposedBridge.log("获取返回结果异常" + throwable);
                    }
                    param.setResult("Toast已被劫持");
                    try {
                        XposedBridge.log(String.format("param.getResultOrThrowable() is %s", param.getResultOrThrowable()));
                    } catch (Throwable throwable) {
                        XposedBridge.log("获取返回结果异常" + throwable);
                    }

                }
            });
        }
    }
}
