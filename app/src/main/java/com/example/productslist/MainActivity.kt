package com.example.productslist

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.whenResumed
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.productslist.CosmosTheme.CosmosLikes
import com.example.productslist.CosmosTheme.CosmosTheme
import com.example.productslist.Holders.AcceptLists
import com.example.productslist.Holders.ListsHolder
import com.example.productslist.Models.AcceptModel
import com.example.productslist.Models.ListModel
import com.example.productslist.Models.User
import com.example.productslist.databinding.ActivityMainBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.collections.set


class MainActivity : AppCompatActivity() {
    var mAuth = Firebase.auth
    val currentUser = mAuth.currentUser
    var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<ListModel, ListsHolder>? = null
    var linearLayoutManager: LinearLayoutManager? = null
    lateinit var recyclerView: RecyclerView

    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mDrawer: Drawer
    private lateinit var mHeader: AccountHeader
    var firebaseRecyclerAdapterAccept: FirebaseRecyclerAdapter<AcceptModel, AcceptLists>? = null
    var linearLayoutManagerAccept: LinearLayoutManager? = null
    lateinit var recyclerViewAccept: RecyclerView
    lateinit var nameList: TextInputEditText

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        databaseReferenceUsers.child(UID).child("theme").get().addOnSuccessListener {
            if (it.value.toString() == "Cosmos") {

                startActivity(Intent(this, CosmosTheme::class.java))
                finish()
            } else {
                findViewById<ConstraintLayout>(R.id.main).visibility = VISIBLE
            }
        }
        setContentView(R.layout.activity_main)

        UID = AUTH.currentUser?.uid.toString()


        val alertDialog = AlertDialog.Builder(this)
        val inflater: LayoutInflater = (this as Activity).getLayoutInflater()
        val dialoglayout: View = inflater.inflate(R.layout.alert_layout, null)
        if (currentUser == null) {
            startActivity(Intent(this, Login::class.java))
            finish()
        }

        linearLayoutManagerAccept = LinearLayoutManager(this)
        linearLayoutManagerAccept!!.stackFromEnd = true
        linearLayoutManagerAccept!!.reverseLayout = true
        recyclerViewAccept = findViewById(R.id.acceptLists)

        showAcceptData()

        firebaseRecyclerAdapterAccept!!.startListening()


        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager!!.reverseLayout = true
        linearLayoutManager!!.stackFromEnd = true
        recyclerView = findViewById(R.id.recyclerView)

        showData()
        firebaseRecyclerAdapter!!.startListening()

        firebaseRecyclerAdapter!!.notifyDataSetChanged()
        firebaseRecyclerAdapterAccept!!.notifyDataSetChanged()

        val menuToolbar = findViewById<MaterialToolbar>(R.id.materialToolbar)
        setSupportActionBar(menuToolbar)
        menuToolbar.setNavigationIcon(R.drawable.baseline_menu_24)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.background = null
        bottomNavigationView.menu.getItem(2).isEnabled = false

        nameList = dialoglayout.findViewById(R.id.nameList)
        val add = findViewById<FloatingActionButton>(R.id.add).setOnClickListener {
            AlertDialogAdd(alertDialog, dialoglayout, nameList)
        }

        bottomNavigationView.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener {
            when (it.itemId) {
                R.id.search -> {
                    val search = findViewById<TextInputLayout>(R.id.inputLayout)
                    if (search.visibility == VISIBLE) {
                        search.visibility = GONE
                    } else {
                        search.visibility = VISIBLE
                        val searchInput = findViewById<TextInputEditText>(R.id.search_list)

                        searchInput.setOnEditorActionListener { v, actionID, event ->
                            if (actionID == EditorInfo.IME_ACTION_DONE) {
                                showData()
                            }

                            return@setOnEditorActionListener false
                        }
                    }
                }
                R.id.find_friend -> {
                    startActivity(Intent(this@MainActivity, AddFriend::class.java))
                }
                R.id.likes -> {
                    startActivity(Intent(this@MainActivity, Likes::class.java))
                    return@OnItemSelectedListener true
                }
                R.id.receipts -> {
                    databaseReferenceUsers.child(UID).child("theme").setValue("Cosmos")
                    startActivity(Intent(this@MainActivity, CosmosTheme::class.java))
                    finish()
                    return@OnItemSelectedListener true
                }

            }
            false
        })

        mDrawer = DrawerBuilder()
            .withTranslucentStatusBar(false)
            .withActivity(this)
            .withToolbar(menuToolbar)
            .withActionBarDrawerToggle(true)
            .withSelectedItem(-1)
            .withHeader(R.layout.nav_header)
            .addDrawerItems(

                PrimaryDrawerItem().withIdentifier(98)
                    .withIconTintingEnabled(true)
                    .withName("Мои друзья")
                    .withSelectable(false)
                    .withIcon(R.drawable.baseline_group_24),
                PrimaryDrawerItem().withIdentifier(99)
                    .withIconTintingEnabled(true)
                    .withName("Любимые продукты")
                    .withSelectable(false)
                    .withIcon(R.drawable.baseline_favorite_24),
                PrimaryDrawerItem().withIdentifier(101)
                    .withIconTintingEnabled(true)
                    .withName("Избранные списки")
                    .withSelectable(false)
                    .withIcon(R.drawable.baseline_bookmark_24),
                PrimaryDrawerItem().withIdentifier(102)
                    .withIconTintingEnabled(true)
                    .withName("Мои рецепты")
                    .withSelectable(false)
                    .withIcon(R.drawable.baseline_receipt_long_24),
                DividerDrawerItem(),
                PrimaryDrawerItem().withIdentifier(108)
                    .withIconTintingEnabled(true)
                    .withName("Настройки")
                    .withSelectable(false)
                    .withIcon(R.drawable.baseline_settings_24),
                PrimaryDrawerItem().withIdentifier(109)
                    .withIconTintingEnabled(true)
                    .withName("Выйти из аккаунта")
                    .withSelectable(false)
                    .withIcon(R.drawable.baseline_logout_24)
            ).withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(
                    view: View?,
                    position: Int,
                    drawerItem: IDrawerItem<*>
                ): Boolean {
                    when (position) {
                        1 -> {
                            startActivity(Intent(this@MainActivity, Friends::class.java))

                        }
                        2 -> {
                        }
                        3 -> {

                        }
                        4 -> {

                        }
                        5 -> {

                        }
                        6 -> {

                        }
                        7 -> {
                            FirebaseAuth.getInstance().signOut()
                            startActivity(Intent(this@MainActivity, Login::class.java))
                            finish()
                        }
                        
                    }
                    return false
                }
            }).build()

        val header = mDrawer.header
        mAvatar = header!!.findViewById<CircleImageView>(R.id.myAvatar)
        val mNickname = header!!.findViewById<TextView>(R.id.nickname)
        databaseReferenceUsers.child(UID).child("username").get().addOnSuccessListener {
            mNickname.text = it.value.toString()
        }
        mAvatar.setOnClickListener {
            uploadImage()
        }
        databaseReferenceUsers.child(UID).child("image").get().addOnSuccessListener {
            val mImageUri = it.value.toString()
            Picasso.get()
                .load(mImageUri)
                .placeholder(R.drawable.baseline_account_circle_24)
                .into(mAvatar)


        }


    }

    lateinit
    var menuToolbar: MaterialToolbar


    lateinit
    var mAvatar: CircleImageView
    val PICK_IMAGE_REQUEST = 1

    private fun uploadImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val mImageUri = data.data
            val path = databaseReferenceStorage.child("ProfileImages")
                .child(UID)
            path.putFile(mImageUri!!).addOnCompleteListener { task1 ->
                if (task1.isSuccessful) {
                    path.downloadUrl.addOnCompleteListener { task2 ->
                        if (task2.isSuccessful) {
                            val photoUrl = task2.result.toString()
                            databaseReferenceUsers.child(UID)
                                .child("image").setValue(photoUrl)
                            recreate()
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun alertHave(nameList: TextInputEditText): Boolean {
        val inflater = (this as Activity).layoutInflater
        var dialogLayout = inflater.inflate(R.layout.already_list, null)
        var alertDialog = AlertDialog.Builder(this)
        alertDialog.setView(dialogLayout)
        val show = alertDialog.show()
        val overwrite =
            dialogLayout.findViewById<Button>(R.id.overwrite).setOnClickListener {
                val dateMap: MutableMap<String, Any> = LinkedHashMap()
                dateMap["list"] = nameList.text.toString()

                databaseReferenceLists.child(UID).child(nameList.text.toString())
                    .removeValue()
                databaseReferenceLists.child(UID).child(nameList.text.toString())
                    .updateChildren(dateMap)
                databaseReferenceListUsers.child(UID).child(nameList.text.toString())
                    .child("list")
                    .removeValue()
                databaseReferenceListUsers.child(UID).child(nameList.text.toString())
                    .child("list")
                    .setValue(nameList.text.toString())

                show.dismiss()

            }
        val cancel = dialogLayout.findViewById<Button>(R.id.rename).setOnClickListener {
            show.dismiss()
        }
        return true

    }


    fun alertHaveAccept(nameList: String, id: String): Boolean {
        val inflater = (this as Activity).layoutInflater
        var dialogLayout = inflater.inflate(com.example.productslist.R.layout.already_list, null)
        var alertDialog = AlertDialog.Builder(this)
        alertDialog.setView(dialogLayout)
        val show = alertDialog.show()
        val overwrite =
            dialogLayout.findViewById<Button>(com.example.productslist.R.id.overwrite)
                .setOnClickListener {
                    val dateMap: MutableMap<String, Any> = LinkedHashMap()
                    dateMap["list"] = nameList

                    databaseReferenceLists.child(UID).child(nameList)
                        .removeValue()
                    databaseReferenceListUsers.child(UID).child(nameList)
                        .child("list")
                        .removeValue()
                    databaseReferenceListUsers.child(UID).child(nameList)
                        .child("list")
                        .setValue(nameList)
                    databaseReferenceLists.child(id).child(nameList).get().addOnSuccessListener {
                        Log.i("id", id)
                        databaseReferenceLists.child(UID).child(nameList).setValue(it.value)
                    }

                    databaseReferenceRequests.child(UID).child(nameList).removeValue()

                    show.dismiss()

                }
        val cancel = dialogLayout.findViewById<Button>(com.example.productslist.R.id.rename)
            .setOnClickListener {
                show.dismiss()
            }
        return true

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    fun showAcceptData() {
        val options = FirebaseRecyclerOptions.Builder<AcceptModel>()
            .setQuery(databaseReferenceRequests.child(UID), AcceptModel::class.java).build()
        firebaseRecyclerAdapterAccept =
            object : FirebaseRecyclerAdapter<AcceptModel, AcceptLists>(options) {
                override fun onBindViewHolder(
                    holder: AcceptLists, position: Int, model: AcceptModel
                ) {
                    databaseReferenceRequests.child(UID)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (ds in snapshot.children) {
                                    val keyName = ds.key.toString()
                                    model.name = keyName
                                    holder.setDetails(
                                        model.Users, model.name, model.buttonAccept, model.buttonDeny
                                    )
                                }

                            }

                            override fun onCancelled(error: DatabaseError) {
                            }
                        })

                }

                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcceptLists {
                    val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.accept_list, parent, false)

                    val inflater: LayoutInflater =
                        (this@MainActivity as Activity).getLayoutInflater()
                    val dialoglayout: View = inflater.inflate(R.layout.alert_layout, null)
                    nameList = dialoglayout.findViewById(R.id.nameList)



                    return AcceptLists(itemView, AcceptModel(), this@MainActivity)
                }
            }
        recyclerViewAccept.layoutManager = linearLayoutManagerAccept
        (firebaseRecyclerAdapterAccept as FirebaseRecyclerAdapter<AcceptModel, AcceptLists>).startListening()
        recyclerViewAccept.adapter = firebaseRecyclerAdapterAccept


    }


    fun showData() {

        val options = FirebaseRecyclerOptions.Builder<ListModel>()
            .setQuery(databaseReferenceListUsers.child(UID), ListModel::class.java).build()
        firebaseRecyclerAdapter =
            object : FirebaseRecyclerAdapter<ListModel, ListsHolder>(options) {
                override fun onBindViewHolder(
                    holder: ListsHolder, position: Int, model: ListModel
                ) {
                    holder.setDetails(
                        model.list,
                    )
                    val searchInput = findViewById<TextInputEditText>(R.id.search_list)
                    if (!model.list.contains(searchInput.text.toString())) {
                        holder.itemView.visibility = View.GONE
                        holder.itemView.setLayoutParams(
                            RecyclerView.LayoutParams(
                                0, 0
                            )
                        )
                    }
                    if (searchInput.text == null){
                        holder.itemView.visibility = View.VISIBLE
                    }

                }

                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListsHolder {
                    val itemView =
                        LayoutInflater.from(parent.context)
                            .inflate(R.layout.lists, parent, false)

                    return ListsHolder(itemView, ListModel())
                }
            }
        recyclerView.layoutManager = linearLayoutManager
        (firebaseRecyclerAdapter as FirebaseRecyclerAdapter<ListModel, ListsHolder>).startListening()
        recyclerView.adapter = firebaseRecyclerAdapter


    }

    override fun onStart() {
        super.onStart()
        firebaseRecyclerAdapter!!.startListening()
    }


    fun AlertDialogAdd(
        alertDialog: AlertDialog.Builder,
        dialoglayout: View,
        nameList: TextInputEditText
    ) {

        alertDialog.setView(dialoglayout)
        alertDialog.setCancelable(false)

        val show = alertDialog.show()

        var listItem = dialoglayout.findViewById<ListView>(R.id.list)

        val ok = dialoglayout.findViewById<Button>(R.id.ok)
        ok.setOnClickListener(View.OnClickListener() {
            databaseReferenceLists.child(UID).child(nameList.text.toString()).get()
                .addOnSuccessListener {
                    Log.i("check", it.value.toString())
                    if (it.value != null) {
                        show.dismiss()
                        alertHave(nameList)

                    }

                    if (nameList.text.toString() != "") {
                        val dateMap: MutableMap<String, Any> = LinkedHashMap()
                        dateMap["list"] = nameList.text.toString()

                        databaseReferenceLists.child(UID).child(nameList.text.toString())
                            .updateChildren(dateMap)

                        databaseReferenceListUsers.child(UID).child(nameList.text.toString())
                            .child("list")
                            .setValue(nameList.text.toString())



                        show.dismiss()

                        (dialoglayout.parent as ViewGroup).removeView(dialoglayout)

                    } else {
                        showError(nameList, "Name your list, please")

                    }
                }

        })
        val checkBox = dialoglayout.findViewById<CheckBox>(R.id.checkBox)
        val addPartner = dialoglayout.findViewById<LinearLayout>(R.id.addPerson)

        val deny = dialoglayout.findViewById<Button>(R.id.dissmis).setOnClickListener {
            show.dismiss()
            (dialoglayout.parent as ViewGroup).removeView(dialoglayout)
            nameList.text = null

        }


        checkBox.setOnClickListener() {
            if (addPartner.visibility == GONE) {
                addPartner.visibility = VISIBLE
                lateinit var listUsernames: List<String>
                listUsernames = listOf<String>()

                var arrayUsernames: Array<String> = arrayOf<String>()

                val inputUsername = dialoglayout.findViewById<TextInputEditText>(
                    R.id.addPersonName
                )




                databaseReferenceUsers.child(UID).child("username").get().addOnSuccessListener {
                    databaseReferenceFriends.child(it.value.toString()).get()
                        .addOnSuccessListener {
                            for (names in it.children) {
                                Log.i("check", names.children.toString())
                                listUsernames = listUsernames + names.key.toString()
                                arrayUsernames = arrayUsernames + names.key.toString()
                                var count = names.key!!.toList().size

                                Log.i("adapter", names.toString())

                                if (count >= listUsernames.size) {

                                    setUsers(
                                        dialoglayout,
                                        arrayUsernames,
                                        inputUsername,
                                        nameList,
                                        listItem,
                                        listUsernames
                                    )

                                }

                            }
                        }
                }
            } else {
                addPartner.visibility = GONE
            }

        }
    }


    private fun setUsers(
        dialog: View?,
        users: Array<String>,
        inputUsername: TextInputEditText,
        nameList: TextInputEditText,
        listItem: ListView,
        listUsernames: List<String>
    ) {
        listItem.visibility = View.VISIBLE

        val adapter = ArrayAdapter(
            applicationContext, android.R.layout.simple_spinner_dropdown_item, users
        )


        inputUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence, i: Int, i1: Int, i2: Int
            ) {

            }

            var view: View? = null

            var position = 1
            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {

                adapter.getFilter().filter(charSequence)
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        listItem.adapter = adapter

        listItem.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id -> // when item selected from list
            databaseReferenceUsers.child(UID).child("username").get().addOnSuccessListener {
                val myUsername = it.value.toString()
                if (adapter.getItem(position).toString() != myUsername) {
                    inputUsername.setText(adapter.getItem(position))
                } else {
                    showError(inputUsername, "I can't do it")
                }
            }
        })

        val sendRequest = dialog!!.findViewById<Button>(R.id.send_request).setOnClickListener {
            if (inputUsername.text.toString() != "") {
                if (nameList.text.toString() != "") {
                    databaseReferenceIds.child(inputUsername.text.toString()).child("id").get()
                        .addOnSuccessListener {
                            if (it.value.toString() != "") {
                                databaseReferenceRequests.child(it.value.toString())
                                    .child(nameList.text.toString())
                                    .child(inputUsername.text.toString())
                                    .setValue(nameList.text.toString())
                                databaseReferenceRequests.child(it.value.toString())
                                    .child(nameList.text.toString()).child("Users")
                                    .setValue(UID)

                                Toast.makeText(
                                    this,
                                    "Request is send",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                showError(inputUsername, "Couldn't find this user")

                            }
                        }
                } else {
                    Toast.makeText(this, "Name your list first", Toast.LENGTH_LONG).show()

                }

            } else {
                showError(inputUsername, "Enter partner's username")
            }

        }


    }


    fun showError(input: TextInputEditText, error: String) {
        input.error = error
        input.requestFocus()
    }
}
