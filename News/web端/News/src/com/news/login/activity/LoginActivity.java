package com.news.login.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.news.R;
import com.news.content.bean.NewsContentBean;
import com.news.data.login.DataProvider;
import com.news.expandMenu.bean.FileBean;
import com.news.login.bean.User;
import com.news.main.activity.MainActivity;


@SuppressLint("HandlerLeak")
public class LoginActivity extends Activity {

	/**
	 * 
	 */
	private TextView tv_name;
	private TextView tv_password;
	private Button loginButton;
	private ProgressDialog mpDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		tv_name = (TextView) findViewById(R.id.name);
		tv_password = (TextView) findViewById(R.id.password);
		loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new OnClickListener() {
			@SuppressLint("HandlerLeak")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (tv_name.getText().equals("") || tv_password.equals("")) {

					Toast.makeText(getApplicationContext(), "账号或密码不能为空！",
							Toast.LENGTH_SHORT).show();
				}
				mpDialog = new ProgressDialog(LoginActivity.this);
				mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				mpDialog.setMessage("loading......");
				mpDialog.setCancelable(false);
				mpDialog.show();

				new Thread(new Thread() {
					public void run() {
						User user = new User();
						user.setName(tv_name.getText().toString());
						user.setPassword(tv_password.getText().toString());
						Message msg = handler.obtainMessage();
						if (DataProvider.GetAllData(user).size() != 0) {
							msg.obj = DataProvider.GetAllData(user);
							handler.sendMessage(msg);
						}
					}
				}).start();
			}
		});
	}

	// 当接收到数据后在这里面更新UI
	Handler handler = new Handler() {
		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {
			// 当数据加载完事之后会在这里更改UI
			SharedPreferences sharedPreference = getApplication()
					.getSharedPreferences("ShareData", Context.MODE_PRIVATE);
			// 对数据进行编辑
			SharedPreferences.Editor editor = sharedPreference.edit();
			@SuppressWarnings("unchecked")
			List<List<Object>> list = (List<List<Object>>) msg.obj;
			// 还原column
			List<Object> fileBeanList = list.get(0);
			ArrayList<FileBean> fList = new ArrayList<FileBean>();
			for (int i = 0; i < fileBeanList.size(); i++) {
				FileBean fileBean = new FileBean();
				fileBean.setId(((FileBean) (fileBeanList.get(i))).getId());
				fileBean.setLabel(((FileBean) (fileBeanList.get(i))).getLabel());
				fileBean.setpId(((FileBean) (fileBeanList.get(i))).getpId());
				fList.add(fileBean);
			}
			// 还原news
			List<Object> newsList = list.get(1);
			ArrayList<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < newsList.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("author",
						((NewsContentBean) (newsList.get(i))).getAuthor());
				map.put("cColumn",
						((NewsContentBean) (newsList.get(i))).getcColumn());
				map.put("content",
						((NewsContentBean) (newsList.get(i))).getContent());
				map.put("id", ((NewsContentBean) (newsList.get(i))).getId());
				map.put("pColumn",
						((NewsContentBean) (newsList.get(i))).getpColumn());
				map.put("status",
						((NewsContentBean) (newsList.get(i))).getStatus());
				map.put("time", ((NewsContentBean) (newsList.get(i))).getTime());
				map.put("title",
						((NewsContentBean) (newsList.get(i))).getTitle());
				listMap.add(map);
			}
			// 还原user
			List<Object> userList = list.get(2);
			ArrayList<User> uList = new ArrayList<User>();
			for (int i = 0; i < userList.size(); i++) {
				User user = new User();
				user.setId(((User) (userList.get(i))).getId());
				user.setName(((User) (userList.get(i))).getName());
				user.setStatus(((User) (userList.get(i))).getStatus());
				uList.add(user);
			}
			editor.putInt("UserStatus", uList.get(0).getStatus());
			editor.putString("author", uList.get(0).getName());

			// 将栏目进行保存
			editor.putString("pColumn", fList.get(0).getLabel());
			// 将第二级目录保存
			FileBean fileBean = fList.get(0);
			// 找到第一个叶子节点
			for (int i = 1; i < fList.size(); i++) {
				if (fileBean.getId() == fList.get(i).getpId()) {
					fileBean = fList.get(i);
					break;
				}
			}
			editor.putString("cColumn", fileBean.getLabel());
			editor.commit();

			Intent intent = new Intent(LoginActivity.this, MainActivity.class);
			intent.putExtra("fList", fList);
			intent.putExtra("listMap", listMap);
			startActivity(intent);
			mpDialog.dismiss();
		}
	};

}
