package www.leigq.com.xposedtest2;

import android.content.ContentValues;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import www.leigq.com.xposedtest2.constant.ProcessConstant;
import www.leigq.com.xposedtest2.constant.WxClassConstant;
import www.leigq.com.xposedtest2.util.WechatUtils;

/**
 * Xposed入口类
 * <br/>
 * 需在resource/assets下面创建xposed_init文件,然后把此类全面配置进去
 * <p>
 * 创建人：leigq <br>
 * 创建时间：2018-11-29 14:55 <br>
 * <p>
 * 修改人： <br>
 * 修改时间： <br>
 * 修改备注： <br>
 * </p>
 */
public class MainXposed implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedBridge.log(String.format("当前包名:%s,进程名:%s", lpparam.packageName, lpparam.processName));
        //com.tencent.mm为微信进程,暂时只处理微信进程
        if (!ProcessConstant.WX_PROCESS_NAME.equals(lpparam.processName)) {
            return;
        }
        XposedBridge.log(String.format("进入微信进程：%s", lpparam.processName));
//        PackageHooker packageHooker = PackageHooker.getInstance(lpparam);
//        try {
//            packageHooker.hook();
//        } catch (IOException | ClassNotFoundException e) {
//            XposedBridge.log("PackageHooker Exception :" + e);
//        }

//        if (SharedPreferencesUtil.getInstance(MyApplication.getGlobalContext()).getSP("auto").equals("yes")) {
//            //拦截数据库插入操作
//            hookDatabaseInsert(lpparam);
//        }
    }

    /**
     * 拦截数据库插入操作
     * <br/>
     * 因为一旦来了新消息，微信就会往本地数据库插入聊天消息，将聊天消息保存到本地。
     * 所以我们只要hook住消息的插入动作，就能实时的获取到聊天消息
     * <br>创建人： leigq
     * <br>创建时间： 2018-11-27 17:44
     * <br>
     *
     * @param loadPackageParam 包含有关正在加载的应用程序的信息。
     */
    private void hookDatabaseInsert(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        //获取微信操作SQLite数据库的类
        Class<?> classDb = XposedHelpers.findClassIfExists(WxClassConstant.SQLITE_DATABASE, loadPackageParam.classLoader);
        if (classDb == null) {
            XposedBridge.log(String.format("拦截数据库插入操作：未找到类->%s", WxClassConstant.SQLITE_DATABASE));
            return;
        }

        //寻找插入数据到数据库的方法
        XposedHelpers.findAndHookMethod(classDb, "insertWithOnConflict",
                String.class, String.class, ContentValues.class, int.class,

                //对方法进行回调,加入我们自己的操作
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        //获取表名
                        String tableName = (String) param.args[0];
                        //获取内容
                        ContentValues contentValues = (ContentValues) param.args[2];

                        if (tableName == null || tableName.length() == 0 || contentValues == null) {
                            return;
                        }
                        /*
                         * 过滤掉非聊天消息
                         * 微信的新聊天消息，会插入2个表：message和rconversation
                         * 1,message表：消息内容总表。所有的聊天消息，都会存入到这个表。
                         * 2,rconversation表：当前的会话表。就是进入微信，在主界面看到的列表。这个表保存的是最后一条聊天记录，每次有新消息，都会更新这个表。
                         * */
                        if (!tableName.equals("message")) {
                            return;
                        }

                        //打印出日志
                        printInsertLog(tableName, (String) param.args[1], contentValues, (Integer) param.args[3]);

                        //提取消息内容
                        //1：表示是自己发送的消息,自己发的消息，不能回复。否则会陷入死循环
                        int isSend = contentValues.getAsInteger("isSend");
                        //消息内容
                        String strContent = contentValues.getAsString("content");
                        //说话人ID
                        String strTalker = contentValues.getAsString("talker");
                        //收到消息，进行回复（要判断不是自己发送的、不是群消息、不是公众号消息，才回复）
                        /*以“@chatroom”结尾的是群消息,以“gh_”开头的是公众号消息*/
                        if (isSend != 1 && !strTalker.endsWith("@chatroom") && !strTalker.startsWith("gh_")) {
                            WechatUtils.replyTextMessage(loadPackageParam, "自动回复：" + strContent, strTalker);
                        }
                    }
                });
    }

    /**
     * 输出插入操作日志
     * <br>创建人： leigq
     * <br>创建时间： 2018-11-27 18:00
     * <br>
     *
     * @param tableName      表名
     * @param nullColumnHack 空列
     * @param contentValues  内容值
     * @param conflictValue  冲突值
     */
    private void printInsertLog(String tableName, String nullColumnHack, ContentValues contentValues, int conflictValue) {
        String[] arrayConflicValues = {"", " OR ROLLBACK ", " OR ABORT ", " OR FAIL ", " OR IGNORE ", " OR REPLACE "};
        if (conflictValue < 0 || conflictValue > 5) {
            return;
        }
        XposedBridge.log(String.format("Hook数据库insert.table：%s,nullColumnHack:%s,CONFLICT_VALUES:%s,contentValues:%s",
                tableName, nullColumnHack, arrayConflicValues[conflictValue], contentValues));
    }

}
