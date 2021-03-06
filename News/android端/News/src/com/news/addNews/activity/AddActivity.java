package com.news.addNews.activity;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.news.R;
import com.news.addNews.bo.ImageBo;
import com.news.addNews.bo.InitData;
import com.news.close.SysApplication;
import com.news.code.NewsCode;
import com.news.content.bean.NewsContentBean;
import com.news.data.news.DataProvider;

public class AddActivity extends Activity implements OnClickListener {

	private EditText editNewsContent;
	private EditText editNewsTitle;
	private Button add_img_button;
	private Button submit_add_button;
	private Button cancel_add_button;
	private TextView column;
	private TextView time;
	private TextView author;
	private Intent intent;
	private String editModel = null;
	NewsContentBean newsContentBean = new NewsContentBean();
	// 记录editText中的图片，用于单击时判断单击的是那一个图片
	private List<Map<String, String>> imgList = new ArrayList<Map<String, String>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this); 
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.addactivity);
		cancel_add_button = (Button) findViewById(R.id.cancel_add_button);
		submit_add_button = (Button) findViewById(R.id.submit_add_button);
		add_img_button = (Button) findViewById(R.id.add_img_button);
		editNewsContent = (EditText) findViewById(R.id.editNewsContent);
		editNewsTitle = (EditText) findViewById(R.id.editNewsTitle);
		column = (TextView) findViewById(R.id.column);
		time = (TextView) findViewById(R.id.time);
		author = (TextView) findViewById(R.id.author);

		// 添加监听事件
		cancel_add_button.setOnClickListener(this);
		submit_add_button.setOnClickListener(this);
		add_img_button.setOnClickListener(this);
		// 接收传过来的参数
		intent = getIntent();
		editModel = intent.getStringExtra("editModel");
		if (editModel.equals("update")) {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> map = (HashMap<String, Object>) intent
					.getSerializableExtra("News");
			newsContentBean.setAuthor(map.get("author").toString());
			newsContentBean.setcColumn(map.get("cColumn").toString());
			newsContentBean.setContent(map.get("content").toString());
			newsContentBean.setId(Integer.parseInt(map.get("id").toString()));
			newsContentBean.setpColumn(map.get("pColumn").toString());
			newsContentBean.setStatus(Integer.parseInt(map.get("status")
					.toString()));
			newsContentBean.setTime(map.get("time").toString());
			newsContentBean.setTitle(map.get("title").toString());
		}
		imgList = InitData
				.loadData(imgList, editNewsContent, AddActivity.this,
						editModel, newsContentBean, editNewsTitle, column,
						time, author);
	}

	@SuppressLint({ "NewApi", "SimpleDateFormat" })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancel_add_button:
			AddActivity.this.finish();
			break;
		case R.id.submit_add_button:// 若点击提交按钮
			SharedPreferences sharedPreferences = (getApplicationContext())
					.getSharedPreferences("ShareData", Context.MODE_PRIVATE);
			String pColumn = sharedPreferences.getString("pColumn", "");
			String cColumn = sharedPreferences.getString("cColumn", "");
			String author = sharedPreferences.getString("author", "");
			String time = sharedPreferences.getString("time", "");
			// 取得EditText中的内容
			String context = editNewsContent.getText().toString();
			if (context.isEmpty()) {
				Toast.makeText(AddActivity.this, "记事为空!", Toast.LENGTH_LONG)
						.show();
			} else {
				// 将title中的内容取出来作为标题
				String title = editNewsTitle.getText().toString();
				String content = editNewsContent.getText().toString();
				// 判断是更新还是新增记事
				if (editModel.equals("newsAdd")) {
					newsContentBean.setContent(context);
					newsContentBean.setTitle(title);
					newsContentBean.setpColumn(pColumn);
					newsContentBean.setcColumn(cColumn);
					newsContentBean.setStatus(NewsCode.USER);
					newsContentBean.setTime(time);
					newsContentBean.setAuthor(author);
				}
				// 如果是编辑则更新记事即可
				if (editModel.equals("update")) {
					System.out.println("有没有进到这里来更新！");
					System.out.println("");
					
					newsContentBean.setContent(content);
					newsContentBean.setTitle(title);
				}
				// 将新的数据添加到服务端去
				new Thread(new Thread() {
					public void run() {
						try {
							if (editModel.equals("update")) {
								DataProvider.UpdateNews(newsContentBean);
							}
							if (editModel.equals("newsAdd")) {
								DataProvider.AddNews(newsContentBean);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
						}
					}
				}).start();
				AddActivity.this.finish();
			}
			break;

		case R.id.add_img_button:
			Intent intent = new Intent();
			// 设定类型为image
			intent.setType("image/*");
			// 设置action
			intent.setAction(Intent.ACTION_GET_CONTENT);
			// 选中相片后返回本Activity
			startActivityForResult(intent, 1);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (data == null || "".equals(data)) {
			return;
		}
		if (resultCode == RESULT_OK) {
			// 取得数据
			Uri uri = data.getData();
			ContentResolver cr = AddActivity.this.getContentResolver();
			Bitmap bitmap = null;
			// 如果是选择照片
			if (requestCode == 1) {
				// 取得选择照片的路径
				String[] proj = { MediaStore.Images.Media.DATA };
				for (int i = 0; i < proj.length; i++) {
				}
				Cursor actualimagecursor = cr
						.query(uri, proj, null, null, null);
				int actual_image_column_index = actualimagecursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				actualimagecursor.moveToFirst();// 将指针定位于查询的数据的第一行
				String path = actualimagecursor
						.getString(actual_image_column_index);
				try {
					// 将对象存入Bitmap中
					bitmap = BitmapFactory
							.decodeStream(cr.openInputStream(uri));

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				// 插入图片
				ImageBo.InsertBitmap(bitmap, 480, path, AddActivity.this,
						editNewsContent, imgList);
			}
		}
	}
}
