package com.bmob.lostfound;

import static com.bmob.lostfound.R.id.tv_describe;
import static com.bmob.lostfound.R.id.tv_photo;
import static com.bmob.lostfound.R.id.tv_time;
import static com.bmob.lostfound.R.id.tv_title;
import java.util.List;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import com.bmob.lostfound.adapter.BaseAdapterHelper;
import com.bmob.lostfound.adapter.QuickAdapter;
import com.bmob.lostfound.base.EditPopupWindow;
import com.bmob.lostfound.bean.Found;
import com.bmob.lostfound.bean.Lost;
import com.bmob.lostfound.config.Constants;
import com.bmob.lostfound.i.IPopupItemClick;

public class MainActivity extends BaseActivity implements OnClickListener, IPopupItemClick, OnItemLongClickListener {
	RelativeLayout layout_action;//
	LinearLayout layout_all;
	TextView tv_lost;
	ListView listview;
	Button btn_add;
    View view;

	protected QuickAdapter<Lost> LostAdapter;

	protected QuickAdapter<Found> FoundAdapter;

	private Button layout_found;
	private Button layout_lost;
	PopupWindow morePop;

	RelativeLayout progress;
	LinearLayout layout_no;
	TextView tv_no;
	@Override
	public void setContentView() {
        setContentView(R.layout.activity_main);
	}

	@Override
	public void initViews() {
		progress = (RelativeLayout) findViewById(R.id.progress);
		layout_no = (LinearLayout) findViewById(R.id.layout_no);
		tv_no = (TextView) findViewById(R.id.tv_no);

		layout_action = (RelativeLayout) findViewById(R.id.layout_action);
		layout_all = (LinearLayout) findViewById(R.id.layout_all);
		tv_lost = (TextView) findViewById(R.id.tv_lost);
		tv_lost.setTag("Lost");
		listview = (ListView) findViewById(R.id.list_lost);
		btn_add = (Button) findViewById(R.id.btn_add);
		initEditPop();


	}
	public void initListeners() {
		// TODO Auto-generated method stub
		listview.setOnItemLongClickListener(this);
		btn_add.setOnClickListener(this);
		layout_all.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == layout_all) {
			showListPop();
		} else if (v == btn_add) {

			//if is student
//			BmobUser user =BmobUser.getCurrentUser();
			boolean isStudent = false;
//			isStudent = user.isStudent();

			String tag = tv_lost.getTag().toString();
			if (tag.equals("Lost")) {
				Intent intent = new Intent(this, AddActivity.class);
				intent.putExtra("from", tv_lost.getTag().toString());
				startActivityForResult(intent, Constants.REQUESTCODE_ADD);

			} else {
				if(isStudent){

					showToast("you are not the teacher!");

				}else {

					Intent intent = new Intent(this, AddActivity.class);
					intent.putExtra("from", tv_lost.getTag().toString());
					startActivityForResult(intent, Constants.REQUESTCODE_ADD);
				}
			}


		} else if (v == layout_found) {
			changeTextView(v);
			morePop.dismiss();
			queryFounds();
		} else if (v == layout_lost) {
			changeTextView(v);
			morePop.dismiss();
			queryLosts();
		}
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		if (LostAdapter == null) {
			LostAdapter = new QuickAdapter<Lost>(this, R.layout.item_list) {
				@Override
				protected void convert(BaseAdapterHelper helper, Lost lost) {
					helper.setText(tv_title, lost.getTitle())
							.setText(tv_describe, lost.getDescribe())
							.setText(tv_time, lost.getCreatedAt())
							.setText(tv_photo, lost.getPhone());
				}
			};
		}

		if (FoundAdapter == null) {
			FoundAdapter = new QuickAdapter<Found>(this, R.layout.item_list) {
				@Override
				protected void convert(BaseAdapterHelper helper, Found found) {
					helper.setText(tv_title, found.getTitle())
							.setText(tv_describe, found.getDescribe())
							.setText(tv_time, found.getCreatedAt())
							.setText(tv_photo, found.getPhone());
				}
			};
		}
		listview.setAdapter(LostAdapter);
		queryLosts();
	}

	private void changeTextView(View v) {
		if (v == layout_found) {
			tv_lost.setTag("Found");
			tv_lost.setText("Completed");
		} else {
			tv_lost.setTag("Lost");
			tv_lost.setText("Service");
		}
	}
	@SuppressWarnings("deprecation")
	private void showListPop() {
		View view = LayoutInflater.from(this).inflate(R.layout.pop_lost, null);
		// ???
		layout_found = (Button) view.findViewById(R.id.layout_found);
		layout_lost = (Button) view.findViewById(R.id.layout_lost);
		layout_found.setOnClickListener(this);
		layout_lost.setOnClickListener(this);
		morePop = new PopupWindow(view, mScreenWidth, 600);

		morePop.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					morePop.dismiss();
					return true;
				}
				return false;
			}
		});

		morePop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		morePop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		morePop.setTouchable(true);
		morePop.setFocusable(true);
		morePop.setOutsideTouchable(true);
		morePop.setBackgroundDrawable(new BitmapDrawable());
		morePop.setAnimationStyle(R.style.MenuPop);
		morePop.showAsDropDown(layout_action, 0, -dip2px(this, 2.0F));
	}

	private void initEditPop() {
		mPopupWindow = new EditPopupWindow(this, 200, 48);
		mPopupWindow.setOnPopupItemClickListner(this);
	}

	EditPopupWindow mPopupWindow;
	int position;

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		position = arg2;
		int[] location = new int[2];
		arg1.getLocationOnScreen(location);
		mPopupWindow.showAtLocation(arg1, Gravity.RIGHT | Gravity.TOP,
				location[0], getStateBar() + location[1]);
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case Constants.REQUESTCODE_ADD:
			String tag = tv_lost.getTag().toString();
			if (tag.equals("Lost")) {
				queryLosts();
			} else {
				queryFounds();
			}
			break;
		}
	}


	private void queryLosts() {
		showView();
		BmobQuery<Lost> query = new BmobQuery<Lost>();
		query.order("-createdAt").findObjects(new FindListener<Lost>(){
            public void done(List<Lost> losts, BmobException e) {
                if (e == null) {
                    LostAdapter.clear();
                    FoundAdapter.clear();
                    if (losts == null || losts.size() == 0) {
                        showErrorView(0);
                        LostAdapter.notifyDataSetChanged();
                        return;
                    }
                    progress.setVisibility(View.GONE);
                    LostAdapter.addAll(losts);
                    listview.setAdapter(LostAdapter);

                } else {
                    showErrorView(1);
                }
            }
        });}

	public void queryFounds() {
		showView();
		BmobQuery<Found> query = new BmobQuery<Found>();
		query.order("-createdAt").findObjects(new FindListener<Found>() {
                    public void done(List<Found> arg0, BmobException e) {
                        if (e == null) {
                            LostAdapter.clear();
                            FoundAdapter.clear();
                            if (arg0 == null || arg0.size() == 0) {
                                showErrorView(1);
                                FoundAdapter.notifyDataSetChanged();
                                return;
                            }
                            FoundAdapter.addAll(arg0);
                            listview.setAdapter(FoundAdapter);
                            progress.setVisibility(View.GONE);
                        } else {
                            showErrorView(1);
                        }
                    }
                });
	}


	private void showErrorView(int tag) {
		progress.setVisibility(View.GONE);
		listview.setVisibility(View.GONE);
		layout_no.setVisibility(View.VISIBLE);
		if (tag == 0) {
			tv_no.setText(getResources().getText(R.string.list_no_data_lost));
		} else {
			tv_no.setText(getResources().getText(R.string.list_no_data_found));
		}
	}

	private void showView() {
		listview.setVisibility(View.VISIBLE);
		layout_no.setVisibility(View.GONE);
	}

	@Override
	public void onEdit(View v) {
		// TODO Auto-generated method stub
		String tag = tv_lost.getTag().toString();
		Intent intent = new Intent(this, AddActivity.class);
		String title = "";
		String describe = "";
		String phone = "";
		if (tag.equals("Lost")) {
			//BmobUser user =BmobUser.getCurrentUser();
			boolean isStudent = true;
//			isStudent = user.isStudent();
			if(isStudent){
				showToast("you are not the teacher!");
			}else {
				title = FoundAdapter.getItem(position).getTitle();
				describe = FoundAdapter.getItem(position).getDescribe();
				phone = FoundAdapter.getItem(position).getPhone();

				intent.putExtra("describe", describe);
				intent.putExtra("phone", phone);
				intent.putExtra("title", title);
				intent.putExtra("from", tag);
				startActivityForResult(intent, Constants.REQUESTCODE_ADD);
			}
		} else {
				title = FoundAdapter.getItem(position).getTitle();
				describe = FoundAdapter.getItem(position).getDescribe();
				phone = FoundAdapter.getItem(position).getPhone();
				intent.putExtra("describe", describe);
				intent.putExtra("phone", phone);
				intent.putExtra("title", title);
				intent.putExtra("from", tag);
				startActivityForResult(intent, Constants.REQUESTCODE_ADD);
	}
	}
    public void onDelete(View v) {
		// TODO Auto-generated method stub
		String tag = tv_lost.getTag().toString();
		if (tag.equals("Lost")) {

			//			BmobUser user =BmobUser.getCurrentUser();
			boolean isStudent = false;
//			isStudent = user.isStudent();
			if(isStudent){
				showToast("you are not the teacher!");

			}else {
				deleteLost();
			}

		} else {
//			BmobUser user =BmobUser.getCurrentUser();
			boolean isStudent = false;
//			isStudent = user.isStudent();
			if(isStudent){
				showToast("you are not the teacher!");

			}else {
				deleteFound();
		}

		}
	}

	private void deleteLost() {
		final String id = LostAdapter.getItem(position).getObjectId();
		LostAdapter.remove(position);
					BmobQuery<Lost> query = new BmobQuery<Lost>();
					query.getObject(id, new QueryListener<Lost>() {
						@Override
						public void done(Lost object, BmobException e) {
							if(e==null){
								Found found = new Found();
								found.setDescribe(object.getDescribe());
								found.setTitle(object.getTitle());
								found.setPhone(object.getPhone());
								found.save(new SaveListener<String>() {
									@Override
									public void done(String objectId, BmobException e) {
										if (e == null) {
											Lost lost = new Lost();
											lost.setObjectId(id);
											lost.delete(new UpdateListener() {
												@Override
												public void done(BmobException e) {
													if (e == null) {
														Toast.makeText(MainActivity.this, "compelete the task", Toast.LENGTH_SHORT).show();
													} else {
														Log.i("bmob", "delete failed" + e.getMessage() + "," + e.getErrorCode());
													}

												}
											});
										}else {
											Toast.makeText(MainActivity.this, "create data failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
										}
								}});
							}else {
								Toast.makeText(MainActivity.this, "find data failed" + e.getMessage(), Toast.LENGTH_SHORT).show();

							}}
	});}
	private void deleteFound() {
		Found found = new Found();
		found.setObjectId(FoundAdapter.getItem(position).getObjectId());
		found.delete(new UpdateListener() {

			@Override
			public void done(BmobException e) {
				if(e==null){
					FoundAdapter.remove(position);
				}else{
					Toast.makeText(MainActivity.this, "delete failed" + e.getMessage(), Toast.LENGTH_SHORT).show();

				}
			}

		});
	}
	private void showToast(String mes){
		Toast.makeText(MainActivity.this,mes,Toast.LENGTH_SHORT).show();
	}

}
