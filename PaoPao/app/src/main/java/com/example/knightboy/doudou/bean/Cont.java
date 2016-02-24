package com.example.knightboy.doudou.bean;

/**
 * Created by knightBoy on 2015/8/25.
 */
public class Cont {
    //一般常量
    public static final int SEX_BOY = 1;
    public static final int SEX_GIRL = 0;

    public static final int XMPP_PORT = 5222;

    //主机名
    public static final String HOST_NAME = "http://1.zilaishu.sinaapp.com/zilaishu";
    public static final String XMPP_HOST = "192.168.191.1";

    //数据访问URL
    public static final String EDIT_STATE_REQUEST_URL = HOST_NAME + "/upload.php";
    public static final String GET_STATE_REQUEST_URL = HOST_NAME + "/page.php";
    public static final String LOGIN_URL = HOST_NAME + "/login.php";
    public static final String REGISTER_URL = HOST_NAME + "/register.php";
    public static final String GET_COMMENTS_URL = HOST_NAME + "/comments.php";
    public static final String EDIT_COMMENTS_URL = HOST_NAME + "/comment.php";
    public static final String SET_USER_INFO_URL = HOST_NAME + "/setDetail.php";
    public static final String GET_USER_INFO_URL = HOST_NAME + "/getDetail.php";
    public static final String SET_USER_POSITION = "http://1.kbdouni.sinaapp.com/doudou/uploadPosition.php" ; //上传用户位置
    public static final String GET_AROUND_USER_URL = HOST_NAME;

    //客户端要保存的用户信息
    public static final String IS_REMEMBERED = "remember";
    public static final String USER_ID = "userId";
    public static final String USER_NAME = "userName";
    public static final String AVATAR_URL = "avatarUrl";
    public static final String PASSWORD = "pwd";
    public static final String USER_SEX = "sex";
    public static final String USER_POSITION = "position";
    public static final String BIRTHDAY = "birthday";
    public static final String REGISTER_TIME = "register_time";
    public static final String SIGNE = "signe";

    //广播Action类型
    //登录状态广播
    public static final String ACTION_IS_LOGIN_SUCCESS = "com.android.doudou.is_login_success";
    //添加好友请求广播
    public static final String ACTION_ADDFRIEND_SUCCESS= "com.android.doudou.addfriend_success";
    //新消息广播
    public static final String ACTION_NEW_MSG= "com.android.doudou.newmsg";
    //聊天界面新消息广播
    public static final String ACTION_CHAT_NEW_MSG= "com.android.doudou.chat_newmsg";
    //好友在线状态更新广播
    public static final String ACTION_FRIENDS_ONLINE_STATUS_CHANGE= "com.android.doudou.friends_online_status_change";

    //广播变量类型
    public static final String IS_LOGIN_SUCCESS = "doudou.is_login_success";
    public static final String PRESENCE_FROM = "doudou.presence_from";
    public static final String ON_LINE_STATUAS = "doudou.on_line_status";

    //消息分隔符
    public static final String SPLIT="卍";
    //个人信息分隔符
    public static final String USER_SPLIT = "@";
    //通知的ID
    public static final int NOTIFY_ID=0x90;

    //通讯发送的消息类型
    public static final String MSG_TYPE_TEXT = "msg_type_text";//文本消息
    public static final String MSG_TYPE_IMG = "msg_type_img";//图片
    public static final String MSG_TYPE_VOICE = "msg_type_voice";//语音
    public static final String MSG_TYPE_ADD_FRIEND = "msg_type_add_friend";//添加好友
    public static final String MSG_TYPE_ADD_FRIEND_SUCCESS = "msg_type_add_friend_success";//同意添加好友

    //是否开启声音
    public static final String MSG_IS_VOICE = "msg_is_voice";
    //是否开启振动
    public static final String MSG_IS_VIBRATE = "msg_is_vibrate";
}
