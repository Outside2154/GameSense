package edu.outside2154.gamesense.database

import com.google.firebase.database.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

// TODO: comprehensive documentation

private fun defaultOnCancelled(databaseError: DatabaseError): Nothing =
        throw databaseError.toException()

// Listens for a Firebase path and executes the callback with a refSnap on change.
fun firebaseListen(path: String, callback: (FirebaseRefSnap) -> Unit) {
    val dbRef = FirebaseDatabase.getInstance().reference.child(path)

    dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) =
                callback(FirebaseRefSnap(dbRef, snapshot))
        override fun onCancelled(databaseError: DatabaseError) =
                defaultOnCancelled(databaseError)
    })
}

// Pair of reference and snapshot.
data class FirebaseRefSnap(val ref: DatabaseReference, val snap: DataSnapshot) {
    fun child(path: String) = FirebaseRefSnap(ref.child(path), snap.child(path))
}

// Used for FirebaseProperty. Useful when the destination is not 1:1 with the snapshot.
interface FirebaseTransform<T> {
    fun fromFirebase(s: DataSnapshot): T?
    fun toFirebase(value: T): Any
}

// The default FirebaseTransform.
@Suppress("UNCHECKED_CAST")
open class FirebaseIdentity<T> : FirebaseTransform<T> {
    override fun fromFirebase(s: DataSnapshot): T? = s.value as T?
    override fun toFirebase(value: T): Any = value as Any
}

// Delegates a property to Firebase represented by `root`.
class FirebaseProperty<in R, T>(private val root: FirebaseRefSnap,
                                private val default: T,
                                private val transform: FirebaseTransform<T> = FirebaseIdentity())
    : ReadWriteProperty<R, T> {
    var field: T? = transform.fromFirebase(root.snap)

    override operator fun getValue(thisRef: R, property: KProperty<*>): T = field ?: default
    override operator fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        field = value
        root.ref.setValue(transform.toFirebase(value))
    }
}

// Delegates a property to Firebase represented by `root.child(property)`.
class BoundFirebaseProperty<in R, T>(
        private val root: FirebaseRefSnap,
        private val default: T,
        private val transform: FirebaseTransform<T> = FirebaseIdentity()) {
    operator fun provideDelegate(
            thisRef: R,
            prop: KProperty<*>
    ): ReadWriteProperty<R, T> = FirebaseProperty(root.child(prop.name), default, transform)
}