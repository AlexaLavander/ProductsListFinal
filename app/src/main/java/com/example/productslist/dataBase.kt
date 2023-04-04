package com.example.productslist

import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

var AUTH = FirebaseAuth.getInstance()
var UID = AUTH.currentUser?.uid.toString()
val firebaseDatabase = FirebaseDatabase.getInstance()
var databaseReferenceUsers: DatabaseReference = firebaseDatabase.getReference("Users")
var databaseReferenceLists: DatabaseReference = firebaseDatabase.getReference("Lists")
var databaseReferenceUsernames: DatabaseReference = firebaseDatabase.getReference("Usernames")
var databaseReferenceRequests: DatabaseReference = firebaseDatabase.getReference("Requests")
var databaseReferenceIds: DatabaseReference = firebaseDatabase.getReference("Ids")
var databaseReferenceListUsers: DatabaseReference = firebaseDatabase.getReference("ListUsers")
var databaseReferenceFriends: DatabaseReference = firebaseDatabase.getReference("Friends")
var databaseReferenceStorage: StorageReference = FirebaseStorage.getInstance().getReference("ProfileImages")
var databaseReferenceRequestsFriend: DatabaseReference = firebaseDatabase.getReference("RequestsFriend")
var databaseReferenceLikes: DatabaseReference = firebaseDatabase.getReference("Likes")
var databaseReferencePosts: DatabaseReference = firebaseDatabase.getReference("Posts")
var databaseReferenceIngredients: DatabaseReference = firebaseDatabase.getReference("Ingredients")
var databaseReferenceTittles: DatabaseReference = firebaseDatabase.getReference("Tittles")
