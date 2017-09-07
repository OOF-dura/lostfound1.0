package com.bmob.lostfound;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import com.bmob.lostfound.bean.Found;
import com.bmob.lostfound.bean.Lost;
import com.bmob.lostfound.bean.User;

//add lost information
public class AddActivity extends BaseActivity implements OnClickListener {

	EditText edit_title, edit_photo, edit_describe;
	Button btn_back, btn_true;
	Button main_button;

	TextView tv_add;
	String from = "";

	String old_title = "";
	String old_describe = "";
	String old_phone = "";

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_add);
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		tv_add = (TextView) findViewById(R.id.tv_add);
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_true = (Button) findViewById(R.id.btn_true);
		edit_photo = (EditText) findViewById(R.id.edit_photo);
		edit_describe = (EditText) findViewById(R.id.edit_describe);
		edit_title = (EditText) findViewById(R.id.edit_title);
	}

	@Override
	public void initListeners() {
		// TODO Auto-generated method stub
		btn_back.setOnClickListener(this);
		btn_true.setOnClickListener(this);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		from = getIntent().getStringExtra("from");
		old_title = getIntent().getStringExtra("title");
		old_phone = getIntent().getStringExtra("phone");
		old_describe = getIntent().getStringExtra("describe");

		edit_title.setText(old_title);
		edit_describe.setText(old_describe);
		edit_photo.setText(old_phone);

		if (from.equals("Lost")) {
			tv_add.setText("添加报修信息");
		} else {
			tv_add.setText("编辑完成公告");
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == btn_true) {
			addByType();
		} else if (v == btn_back) {
			finish();
		}
	}

	String title = "";
	String describe = "";
	String phone = "";

	private void addByType() {
		title = edit_title.getText().toString();
		describe = edit_describe.getText().toString();
		phone = edit_photo.getText().toString();

		if (TextUtils.isEmpty(title)) {
			ShowToast("请添加标题");
			return;
		}
		if (TextUtils.isEmpty(describe)) {
			ShowToast("请添加描述");
			return;
		}
		if (TextUtils.isEmpty(phone)) {
			ShowToast("请添加照片");
			return;
		}
		if (from.equals("Lost")) {
			addLost();
		} else {
			addFound();
		}
	}

	private void addLost() {
		Lost lost = new Lost();
		lost.setDescribe(describe);
		lost.setPhone(phone);
		lost.setTitle(title);
		lost.save(new SaveListener<String>() {
			@Override
			public void done(String objectId, BmobException e) {
				if (e == null) {
					ShowToast("添加报修信息成功");
					setResult(RESULT_OK);
					finish();
				} else {
					ShowToast("添加报修信息失败" + e);

				}

			}
		});
	}
	private void addFound() {
		Found found = new Found();
		found.setDescribe(describe);
		found.setPhone(phone);
		found.setTitle(title);
		found.save(new SaveListener<String>() {
			@Override
			public void done(String objectId, BmobException e) {
				if (e == null) {
					ShowToast("添加完成公告成功");
					setResult(RESULT_OK);
					finish();
				} else {
					ShowToast("添加完成公告失败" + e);

				}

			}
		});
	}






}