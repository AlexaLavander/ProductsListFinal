package com.example.productslist

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.productslist.Holders.CategoriesHolder
import com.example.productslist.Holders.ProductHolder
import com.example.productslist.Models.CategoryModel
import com.example.productslist.Models.ProductModel
import com.example.productslist.R.id.recyclerViewProducts
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class Read : AppCompatActivity() {
    val linearLayoutManager = LinearLayoutManager(this)
    val linearLayoutManagerCat = LinearLayoutManager(this)

    lateinit var extra: String
    lateinit var recyclerView: RecyclerView
    lateinit var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<ProductModel, ProductHolder>
    lateinit var inflater: LayoutInflater
    lateinit var dialoglayout: View

    lateinit var firebaseRecyclerAdapterCat: FirebaseRecyclerAdapter<CategoryModel, CategoriesHolder>
    lateinit var inflaterCat: LayoutInflater
    lateinit var recyclerViewCat: RecyclerView

    lateinit var list: List<String>
    lateinit var inputNameProduct: TextInputEditText

    @SuppressLint("MissingInflatedId", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read)

        val menuToolbar = findViewById<MaterialToolbar>(R.id.materialToolbar)
        setSupportActionBar(menuToolbar)

        extra = intent.getStringExtra("title").toString()
        inflater = (this as Activity).getLayoutInflater()
        dialoglayout = inflater.inflate(R.layout.alert_dialog_product, null)
        inputNameProduct = dialoglayout.findViewById<TextInputEditText>(R.id.input_name_product)

        linearLayoutManagerCat.reverseLayout = true
        linearLayoutManagerCat.stackFromEnd = true
        recyclerViewCat = findViewById(recyclerViewProducts)


        showCategories()
        firebaseRecyclerAdapterCat.startListening()


        val text = findViewById<TextView>(R.id.titleText)
        text.text = intent.getStringExtra("title").toString()
        val add = findViewById<FloatingActionButton>(R.id.add_product)
        add.setOnClickListener {
            alertDialog()
        }

        firebaseRecyclerAdapterCat.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menuInflater.inflate(R.menu.menu_back, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.back) {
            finish()
            return true
        }
        if (item.itemId == R.id.add_partner) {
            addAlertDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    fun showError(input: TextInputEditText, error: String) {
        input.error = error
        input.requestFocus()
    }

    private fun alertDialog() {
        inflater = (this as Activity).getLayoutInflater()
        val alertDialog = AlertDialog.Builder(this)
        dialoglayout = inflater.inflate(R.layout.alert_dialog_product, null)

        var list = dialoglayout.findViewById<ListView>(R.id.categories)
        var array = arrayOf(
            "Овощи и фрукты",
            "Хлеб и выпечка",
            "Мясо",
            "Колбаса",
            "Крупы, соль и сахар",
            "Молочные продукты",
            "Яйца",
            "Масло растительное",
            "Соусы и приправы",
            "Алкоголь",
            "Cоки и вода",
            "Рыба",
            "ХозТовары",
            "Сладости",
            "Прочее"
        )

        alertDialog.setView(dialoglayout)
        alertDialog.setCancelable(false)

        val show = alertDialog.show()
        val arrayAdapter = ArrayAdapter<String>(
            applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            array
        )

        val inputCategory =
            dialoglayout.findViewById<TextInputEditText>(R.id.inputCategory)
        inputCategory.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence, i: Int, i1: Int, i2: Int
            ) {

            }


            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {

                arrayAdapter.getFilter().filter(charSequence)
            }

            override fun afterTextChanged(editable: Editable) {}
        })


        list.adapter = arrayAdapter
        inputNameProduct =
            dialoglayout.findViewById<TextInputEditText>(R.id.input_name_product)
        extra = intent.getStringExtra("title").toString()

        list.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id -> // when item selected from list
            if (inputNameProduct.text.toString() != "") {
                inputCategory.setText(arrayAdapter.getItem(position))
            } else {
                showError(inputNameProduct, "Введите название продукта")
            }


        })
        val add: Button = dialoglayout.findViewById(R.id.add_product_alert)
        add.setOnClickListener {

            if (inputCategory.text.toString() != "") {
                databaseReferenceLists.get().addOnSuccessListener {
                    for (ids in it.children) {
                        if (ids.key.toString() != UID) {
                            databaseReferenceLists.child(ids.key.toString()).child(extra)
                                .child(inputCategory.text.toString())
                                .child(inputNameProduct.text.toString()).child("name")
                                .setValue(inputNameProduct.text.toString())
                            databaseReferenceLists.child(ids.key.toString()).child(extra)
                                .child("categories")
                                .child(inputCategory.text.toString())
                                .child("category")
                                .setValue(inputCategory.text.toString())
                        }
                    }
                }
                databaseReferenceLists.child(UID).child(extra).child(inputCategory.text.toString())
                    .child(inputNameProduct.text.toString()).child("name")
                    .setValue(inputNameProduct.text.toString())

                databaseReferenceLists.child(UID).child(extra).child("categories")
                    .child(inputCategory.text.toString())
                    .child("category")
                    .setValue(inputCategory.text.toString())

                show.dismiss()
            } else {
                showError(inputCategory, "Установите категорию")
            }
        }


        val cancel =
            dialoglayout.findViewById<Button>(R.id.cancel).setOnClickListener {
                show.dismiss()
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun showCategories() {

        val options = FirebaseRecyclerOptions.Builder<CategoryModel>()
            .setQuery(
                databaseReferenceLists.child(UID).child(extra).child("categories"),
                CategoryModel::class.java
            )
            .build()

        firebaseRecyclerAdapterCat =
            object :
                FirebaseRecyclerAdapter<CategoryModel, CategoriesHolder>(options) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int,

                    ): CategoriesHolder {
                    val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.category, parent, false)

                    return CategoriesHolder(itemView, CategoryModel())
                }

                override fun onBindViewHolder(
                    holder: CategoriesHolder,
                    position: Int,
                    model: CategoryModel
                ) {
                    holder.setDetails(
                        applicationContext,
                        model.category,
                        intent.getStringExtra("title").toString(),

                        )



                }

            }

        recyclerViewCat.adapter = firebaseRecyclerAdapterCat
        firebaseRecyclerAdapterCat.startListening()
        recyclerViewCat.layoutManager = linearLayoutManagerCat

    }

    fun addAlertDialog() {
        val inflater = (this as Activity).getLayoutInflater()
        dialoglayout = inflater.inflate(R.layout.add_partner, null)
        val alertDialog = AlertDialog.Builder(this)
        lateinit var listUsernames: List<String>
        listUsernames = listOf<String>()
        var listItem = dialoglayout.findViewById<ListView>(R.id.list)
        alertDialog.setView(dialoglayout)
        alertDialog.setCancelable(false)

        val show = alertDialog.show()

        var arrayUsernames: Array<String> = arrayOf<String>()

        val inputUsername = dialoglayout.findViewById<TextInputEditText>(R.id.addPersonName)
        inputUsername.text = null
        val cancel = dialoglayout.findViewById<Button>(R.id.cancel).setOnClickListener {
            show.dismiss()
        }
        databaseReferenceUsers.child(UID).child("username").get()
            .addOnSuccessListener {
                databaseReferenceFriends.child(it.value.toString()).get()
                    .addOnSuccessListener {
                        for (names in it.children) {
                            Log.i("check", names.children.toString())
                            listUsernames = listUsernames + names.key.toString()
                            arrayUsernames =
                                arrayUsernames + names.key.toString()
                            var count = names.key!!.toList().size

                            Log.i("adapter", names.toString())

                            val adapter = ArrayAdapter(
                                applicationContext,
                                android.R.layout.simple_spinner_dropdown_item,
                                arrayUsernames

                            )
                            inputUsername.addTextChangedListener(object : TextWatcher {
                                override fun beforeTextChanged(
                                    charSequence: CharSequence, i: Int, i1: Int, i2: Int
                                ) {

                                }

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
                                databaseReferenceUsers.child(UID).child("username").get()
                                    .addOnSuccessListener {
                                        val myUsername = it.value.toString()
                                        if (adapter.getItem(position)
                                                .toString() != myUsername
                                        ) {
                                            inputUsername.setText(adapter.getItem(position))
                                        } else {
                                            showError(inputUsername, "I can't do it")
                                        }
                                    }
                            })
                            val sendRequest =
                                dialoglayout!!.findViewById<Button>(R.id.send_request)
                                    .setOnClickListener {
                                        if (inputUsername.text.toString() != "") {
                                            databaseReferenceIds.child(inputUsername.text.toString())
                                                .child("id").get()
                                                .addOnSuccessListener {
                                                    if (it.value.toString() != "") {
                                                        databaseReferenceRequests.child(it.value.toString())
                                                            .child(extra)
                                                            .child(inputUsername.text.toString())
                                                            .setValue(extra)
                                                        databaseReferenceRequests.child(it.value.toString())
                                                            .child(extra).child("Users")
                                                            .setValue(UID)

                                                    } else {
                                                        showError(
                                                            inputUsername,
                                                            "Couldn't find this user"
                                                        )

                                                    }
                                                }
                                        } else {
                                            showError(inputUsername, "Введите имя пользователя")
                                        }
                                        show.dismiss()
                                    }
                        }

                    }

            }


    }


}