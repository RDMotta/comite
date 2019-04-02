package app.rdm.evento.app

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage

class FirebaseUtils private constructor() {
    init {

    }
    private object getReference {
        val instance = FirebaseUtils()
    }

    private object getFirebaseAuth {
        val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    }

    private object getFirebaseFirestore {
        val mFirebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    }

    private object getFirebaseStorage{
        val mFirebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    }

    companion object reference{
        val instance: FirebaseUtils by lazy { getReference.instance }
        val firebaseAuth :FirebaseAuth by lazy { getFirebaseAuth.mFirebaseAuth }
        val firebaseFirestore: FirebaseFirestore  by lazy { getFirebaseFirestore.mFirebaseFirestore}
        val firebaseStorage: FirebaseStorage by lazy { getFirebaseStorage.mFirebaseStorage }
    }

    val firebaseAuth :FirebaseAuth = reference.firebaseAuth
    val firebaseFirestore: FirebaseFirestore = reference.firebaseFirestore
    val firebaseStorage: FirebaseStorage = reference.firebaseStorage
}