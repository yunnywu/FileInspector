package com.cy.wu.library.fileinspector;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class FileListActivity extends AppCompatActivity {

    public static final String EXTRA_FILE_PATH = "file_path";

    public static final String ACTION_FILE_MANAGER = "com.wu.cy.action.file.manager";

    private BottomSheetBehavior behavior;

    Toolbar mTbToolbar;
    TextView mTvPath;
    RecyclerView mRecycleView;
    ImageView mIvBack;
    RelativeLayout mRlNoFile;
    FileAdapter mFileAdapter;
    String rootDir;
    List<FileInfo> mFileInfoList = new ArrayList<>();
    private String mCurrentPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        initView();

        rootDir = getFilesDir().getParent();
        Intent intent = getIntent();
        String filePath = intent.getStringExtra(EXTRA_FILE_PATH);
        if(TextUtils.isEmpty(filePath)){
            filePath = rootDir;
        }

        loadFiles(filePath);
        if(mFileInfoList.isEmpty()){
            mRecycleView.setVisibility(View.GONE);
            mRlNoFile.setVisibility(View.VISIBLE);
        }else {
            mRecycleView.setVisibility(View.VISIBLE);
            mRlNoFile.setVisibility(View.GONE);
            mFileAdapter = new FileAdapter();
            mRecycleView.setAdapter(mFileAdapter);
        }
    }

    private void setBackVisible(final String filePath) {
        if(rootDir.equals(filePath)){
            mIvBack.setVisibility(View.GONE);
        }else {
            mIvBack.setVisibility(View.VISIBLE);
            mIvBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reloadFilePath(filePath.substring(0,filePath.lastIndexOf(File.separator)));
                }
            });
        }
    }

    private void loadFiles(String filePath) {
        mCurrentPath = filePath;
        mFileInfoList.clear();
        File file = new File(mCurrentPath);
        mTvPath.setText(file.getPath().replace(rootDir, getPackageName()));
        File[] files = file.listFiles();
        if (files != null) {

            for (File f : files) {
                FileInfo fileInfo = new FileInfo();
                fileInfo.fileName = f.getName();
                fileInfo.filePath = f.getAbsolutePath();
                fileInfo.isFold = f.isDirectory();
                fileInfo.lastModifyTime = parseDateTime(f.lastModified());
                mFileInfoList.add(fileInfo);
            }
        }

        setBackVisible(filePath);
    }

    private void initView() {
        mTbToolbar = (Toolbar) findViewById(R.id.tb_toolbar);
        setSupportActionBar(mTbToolbar);
        mTvPath = (TextView) findViewById(R.id.tv_path);
        mRecycleView = (RecyclerView) findViewById(R.id.recycle_view);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mRlNoFile = (RelativeLayout) findViewById(R.id.rl_no_file);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycleView.setLayoutManager(linearLayoutManager);
        mRecycleView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

    }


    public  final String parseDateTime(long datetime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(datetime);
    }


    class FileAdapter extends RecyclerView.Adapter<FileViewHolder>{

        @Override
        public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(FileListActivity.this);
            View view = inflater.inflate(R.layout.item_file_info, parent, false);
            return new FileViewHolder(view);
        }

        @Override
        public void onBindViewHolder(FileViewHolder holder, int position) {
            holder.setFileInfo(mFileInfoList.get(position));
        }

        @Override
        public int getItemCount() {
            return mFileInfoList.size();
        }
    }

    class FileViewHolder extends RecyclerView.ViewHolder {
        ImageView iconView;
        TextView fileName;
        TextView modifyTime;
        ImageView arrowView;
        RelativeLayout mRlContent;


        public FileViewHolder(View itemView) {
            super(itemView);
            iconView = (ImageView) itemView.findViewById(R.id.iv_icon);
            fileName = (TextView) itemView.findViewById(R.id.tv_fileName);
            modifyTime = (TextView) itemView.findViewById(R.id.tv_fileModify);
            arrowView = (ImageView) itemView.findViewById(R.id.iv_arrow);
            mRlContent = (RelativeLayout) itemView.findViewById(R.id.rl_content);
        }

        public void setFileInfo(final FileInfo info){
            arrowView.setVisibility(info.isFold ? View.VISIBLE : View.GONE);
            iconView.setImageResource(info.isFold ? R.drawable.ic_folder : R.drawable.ic_file);
            fileName.setText(info.fileName);
            modifyTime.setText(info.lastModifyTime);
            mRlContent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showThePopupWindow(info);
                    return true;
                }
            });
            if(info.isFold){
                mRlContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       reloadFilePath(info.filePath);
                    }
                });
            }else {
                mRlContent.setOnClickListener(null);
            }
        }
    }

    private void reloadFilePath(String filePath) {
        loadFiles(filePath);
        if(mFileInfoList.isEmpty()){
            mRecycleView.setVisibility(View.GONE);
            mRlNoFile.setVisibility(View.VISIBLE);
        }else {
            mRecycleView.setVisibility(View.VISIBLE);
            mRlNoFile.setVisibility(View.GONE);
            mFileAdapter.notifyDataSetChanged();
        }
    }

    private void showThePopupWindow(final FileInfo fileInfo) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        final List<Action> mItemAction = new ArrayList<>(4);
        mItemAction.add(new FileCopyAction(this, "Copy to sdcard"));
        mItemAction.add(new MD5Action(this, "Show the file MD5"));
        mItemAction.add(new DeleteAction(this, "Delete this file"));

        View contentView = LayoutInflater.from(this).inflate(R.layout.popupwindow_unit_link_detail, null);
        ListView listView = (ListView) contentView.findViewById(R.id.bankcard_popup_list);
        listView.setAdapter(new ArrayAdapter<>(this,
                R.layout.item_popup_list, R.id.tv_list_simple_item, mItemAction));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                mItemAction.get(position).doAction(fileInfo, new Action.ActionCallBack() {
                    @Override
                    public void callBack(boolean needReload) {
                        if(needReload){
                            reloadFilePath(mCurrentPath);
                        }
                    }
                });
            }
        });

        bottomSheetDialog.setContentView(contentView);
        View parent = (View) contentView.getParent();
        behavior = BottomSheetBehavior.from(parent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        bottomSheetDialog.show();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if (!TextUtils.equals(rootDir, mCurrentPath)) {
                reloadFilePath(mCurrentPath.substring(0, mCurrentPath.lastIndexOf(File.separator)));
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }
}
