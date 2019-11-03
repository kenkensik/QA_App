package jp.techacademy.kento.saka.qa_app



import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ListView
import android.support.design.widget.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_question_detail.*
import kotlinx.android.synthetic.main.activity_question_send.*
import android.util.Log




import java.util.HashMap

class QuestionDetailActivity : AppCompatActivity() {

    private lateinit var mQuestion: Question
    private lateinit var mAdapter: QuestionDetailListAdapter
    private lateinit var mAnswerRef: DatabaseReference
    var count=0




    private val mEventListener = object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    val map = dataSnapshot.value as Map<String, String>

            val answerUid = dataSnapshot.key ?: ""

            for (answer in mQuestion.answers) {
                // 同じAnswerUidのものが存在しているときは何もしない
                if (answerUid == answer.answerUid) {
                    return
                }
            }

            val body = map["body"] ?: ""
            val name = map["name"] ?: ""
            val uid = map["uid"] ?: ""

            val answer = Answer(body, name, uid, answerUid)
            mQuestion.answers.add(answer)
            mAdapter.notifyDataSetChanged()
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {

        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onCancelled(databaseError: DatabaseError) {

        }
    }


    private val EventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            //val map = dataSnapshot.value as Map<String, String>

            Log.d("test",dataSnapshot.key)
            if(count==0){count=1}
            else{count=0}

        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {

        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onCancelled(databaseError: DatabaseError) {

        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_detail)

        val user = FirebaseAuth.getInstance().currentUser



        val extras = intent.extras
        mQuestion = extras.get("question") as Question
        var mGenre = extras.getInt("genre")



        favorite.setOnClickListener{

            val dataBaseReference = FirebaseDatabase.getInstance().reference
            //val favoriteRef =dataBaseReference.child(FavoritePATH).child(user!!.uid).child(mQuestion.questionUid)
            val favoriteRef =dataBaseReference.child(FavoritePATH).child(user!!.uid)

            val data = HashMap<String, String>()
            //val title = titleText.text.toString()
            //val body = bodyText.text.toString()
            //Log.d("test",favoriteRef.key)

            if (favoriteRef != null) {
                favoriteRef!!.removeEventListener(EventListener)
            }

            favoriteRef!!.addChildEventListener(EventListener)

            if(favorite.text=="登録"){
                favorite.text="解除"

                Snackbar.make(it,"お気に入りに登録されました", Snackbar.LENGTH_LONG).show()

                //data["uid"] = FirebaseAuth.getInstance().currentUser!!.uid

                data["category"]=mGenre.toString()



                favoriteRef.child(mQuestion.questionUid).setValue(data)


                //fab.hide()



            }else{
                favoriteRef.child(mQuestion.questionUid).removeValue()
                dataBaseReference.child(FavoritePATH).child(user!!.uid).child(mQuestion.questionUid)
                favorite.text="登録"

                Snackbar.make(it,"お気に入りから削除されました", Snackbar.LENGTH_LONG).show()

                
                //fab.show()
            }

        }


        // 渡ってきたQuestionのオブジェクトを保持する
        //val extras = intent.extras
        //mQuestion = extras.get("question") as Question

        title = mQuestion.title

        // ListViewの準備
        mAdapter = QuestionDetailListAdapter(this, mQuestion)
        listView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()

        fab.setOnClickListener {
            // ログイン済みのユーザーを取得する

            if (user == null) {
                // ログインしていなければログイン画面に遷移させる
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            } else {
                // Questionを渡して回答作成画面を起動する
                val intent = Intent(applicationContext, AnswerSendActivity::class.java)
                intent.putExtra("question", mQuestion)
                startActivity(intent)
                // TODO:
            }
        }

        val dataBaseReference = FirebaseDatabase.getInstance().reference
        mAnswerRef = dataBaseReference.child(ContentsPATH).child(mQuestion.genre.toString()).child(mQuestion.questionUid).child(AnswersPATH)
        mAnswerRef.addChildEventListener(mEventListener)
    }
    override fun onResume() {


        super.onResume()

        val extras = intent.extras
        var mGenre = extras.getInt("genre")


        if(mGenre ==5){
            fab.hide()
        }

        val user = FirebaseAuth.getInstance().currentUser




        //val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            // ログインしていなければお気に入りボタンを非表示
            favorite.setVisibility(View.INVISIBLE)
        } else {
            // お気に入りボタンを表示
            favorite.setVisibility(View.VISIBLE)
        }

    }

}