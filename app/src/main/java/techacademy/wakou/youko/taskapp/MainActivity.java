package techacademy.wakou.youko.taskapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;



import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_TASK = "techacademy.wakou.youko.taskapp.TASK";
    private Realm mRealm;
    private RealmChangeListener mRealmListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            reloadListView();
        }
    };
    private ListView mListView;
    private TaskAdapter mTaskAdapter;
    private SearchView search;
    private String searchWord;
    private final MainActivity self = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(MainActivity.this,InputActivity.class);
               startActivity(intent);
            }
        });
        this.search = (SearchView)findViewById(R.id.searchView1);
        this.search.setIconifiedByDefault(false);
        this.search.setSubmitButtonEnabled(true);
        this.search.setQueryHint("検索文字を入力してください");
        this.search.setOnQueryTextListener(searchListener);
        mRealm = Realm.getDefaultInstance();
        mRealm.addChangeListener(mRealmListener);

        mTaskAdapter = new TaskAdapter(MainActivity.this);
        mListView = (ListView) findViewById(R.id.listView1);
        mListView.setTextFilterEnabled(true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = (Task)parent.getAdapter().getItem(position);

                Intent intent = new Intent(MainActivity.this,InputActivity.class);
                intent.putExtra(EXTRA_TASK,task.getId());

                startActivity(intent);
            }
        });

       mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final Task task = (Task) parent.getAdapter().getItem(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("削除");
                builder.setMessage(task.getTitle() + "を削除しますか");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RealmResults<Task>results = mRealm.where(Task.class).equalTo("id",task.getId()).findAll();
                        mRealm.beginTransaction();
                        results.deleteAllFromRealm();
                        mRealm.commitTransaction();

                        Intent resultIntent = new Intent(getApplicationContext(),TaskAlarmReceivar.class);
                        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(
                                MainActivity.this,
                                task.getId(),
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                        alarmManager.cancel(resultPendingIntent);
                        reloadListView();
                    }
                });
                builder.setNegativeButton("CANCEL",null);

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
        reloadListView();

    }


//    public void setOnQueryTextListener(SearchView.OnQueryTextListener callress){
//        if(!this.searchWord.equals("")){
////        空文字でない場合
//            this.search.setQuery(this.searchWord,false);
//        }else{
////        空文字だった場合
//            this.search.setQueryHint("検索文字を入力してください");
//        }
//    }

    SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {

        @Override
//        enterが押されるかsubmitが押されると呼びだされる
        public boolean onQueryTextSubmit(String s) {
            return true;
        }

        @Override
//        文字を入力すると呼び出される
        public boolean onQueryTextChange(String queryText) {
                reloadListView();
            return true;
        }

//           @Override
           public boolean OnSubmitQuery(String s){
               return false;
           }
    };

    private void reloadListView() {
//        SearchViewに入力された文字を取得する
        String word = this.search.getQuery().toString();
//        wordが０文字だったら
        if(word.length()== 0){
            RealmResults<Task> taskRealmResults = mRealm.where(Task.class).findAllSorted("date", Sort.DESCENDING);
            mTaskAdapter.setTaskList(mRealm.copyFromRealm(taskRealmResults));

            mListView.setAdapter(mTaskAdapter);

            mTaskAdapter.notifyDataSetChanged();
//            wordに入力があったら
        }else{
            RealmResults<Task> taskRealmResults = mRealm.where(Task.class).contains("category",word).findAllSorted("date", Sort.DESCENDING);
            mTaskAdapter.setTaskList(mRealm.copyFromRealm(taskRealmResults));

            mListView.setAdapter(mTaskAdapter);

            mTaskAdapter.notifyDataSetChanged();

        }

           }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRealm.close();
    }


}