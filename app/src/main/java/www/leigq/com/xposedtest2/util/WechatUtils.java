package www.leigq.com.xposedtest2.util;

import java.lang.reflect.Method;
import java.util.HashMap;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * 微信工具类,专用于微信的一些执行方法
 * <p>
 * 创建人：leigq <br>
 * 创建时间：2018-11-27 18:09 <br>
 * <p>
 * 修改人： <br>
 * 修改时间： <br>
 * 修改备注： <br>
 * </p>
 */
public class WechatUtils {

    /**
     * 回复文本消息
     *
     * @param loadPackageParam  包含有关正在加载的应用程序的信息。
     * @param strContent       消息内容
     * @param strChatroomId  说话人ID
     */
    public static void replyTextMessage(XC_LoadPackage.LoadPackageParam loadPackageParam,
                                        String strContent, final String strChatroomId) {
        XposedBridge.log(String.format("准备回复消息内容：content: %s, strChatroomId: %s", strContent, strChatroomId));

        if (strContent == null || strChatroomId == null
                || strContent.length() == 0 || strChatroomId.length() == 0) {
            return;
        }

        //构造new里面的参数：l iVar = new i(aao, str, hQ, i2, mVar.cvb().fD(talkerUserName, str));
        Class<?> classiVar = XposedHelpers.findClass("com.tencent.mm.modelmulti.i", loadPackageParam.classLoader);
        Object objectiVar = XposedHelpers.newInstance(classiVar,
                new Class[]{String.class, String.class, int.class, int.class, Object.class},
                strChatroomId, strContent, 1, 1, new HashMap<String, String>() {{
                    put(strChatroomId, strChatroomId);
                }});
        Object[] objectParamiVar = new Object[]{objectiVar, 0};

        //创建静态实例对象au.DF()，转换为com.tencent.mm.ab.o对象
        Class<?> classG = XposedHelpers.findClass("com.tencent.mm.kernel.g", loadPackageParam.classLoader);
        Object objectG = XposedHelpers.callStaticMethod(classG, "Eh");
        Object objectdpP = XposedHelpers.getObjectField(objectG, "dpP");

        //查找au.DF().a()方法
        Class<?> classDF = XposedHelpers.findClass("com.tencent.mm.ab.o", loadPackageParam.classLoader);
        Class<?> classI = XposedHelpers.findClass("com.tencent.mm.ab.l", loadPackageParam.classLoader);
        Method methodA = XposedHelpers.findMethodExact(classDF, "a", classI, int.class);

        //调用发消息方法
        try {
            XposedBridge.invokeOriginalMethod(methodA, objectdpP, objectParamiVar);
            XposedBridge.log("invokeOriginalMethod()执行成功");
        } catch (Exception e) {
            XposedBridge.log(String.format("调用微信消息回复方法异常:%s", e));
        }
    }
}
