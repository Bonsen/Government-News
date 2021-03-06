package Code;

public class OperateCode {
	//数据传递相关
	public static final int ADD_NEWS=1;//添加一条新闻
	public static final int UPDATE_NEWS=2;//更新一条新闻
	public static final int DELETE_NEWS=3;//删除一条新闻
	public static final int CHECK_NEWS=4;//审核一条新闻
	public static final int SEARCH_NEWS=5;//查询码
	public static final int LOGIN=6;//登陆验证码
	public static final int COLUMN=7;//获得栏目码
	public static final int NEWS_BY_COLUMN=8;//根据栏目获得新闻码
	public static final int NEWS_BY_COLUMN_ADMIN=9;//管理员会获得所有的新闻

	//新闻处理相关
	public static final int CHECK=3;//新闻已被审核
	public static final int REFUTE=2;//新闻被驳回
	public static final int NOT_CHECK=1;//新闻未被审核
	
	
	//客户端与服务器交互码
	public static final int SUCCESS = 1;
	public static final int FAIL = 2;
	
	//添加码
	public static final int ADD_NEWS_SUCCESS=1;
	public static final int ADD_NEWS_FAIL=2;
	public static final int UPDATE_NEWS_SUCCESS=3;
	public static final int UPDATE_NEWS_FAIL=4;
	
}
