package edu.outside2154.gamesense.database

import com.google.firebase.database.*
import edu.outside2154.gamesense.util.Updatable
import kotlin.properties.ReadOnlyProperty
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
    fun toFirebase(value: T, ref: DatabaseReference)
}

open class FirebaseIdentity<T> : FirebaseTransform<T> {
    @Suppress("UNCHECKED_CAST")
    override fun fromFirebase(s: DataSnapshot): T? = s.value as? T
    override fun toFirebase(value: T, ref: DatabaseReference) {
        ref.setValue(value)
    }
}

// Delegates a property to Firebase represented by `root`.
class FirebaseProperty<in R, T>(
        private val root: FirebaseRefSnap,
        private val default: T,
        private val transform: FirebaseTransform<T> = FirebaseIdentity())
    : ReadWriteProperty<R, T> {
    private var field: T? = transform.fromFirebase(root.snap)

    override operator fun getValue(thisRef: R, property: KProperty<*>): T = field ?: default
    override operator fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        field = value
        transform.toFirebase(value, root.ref)
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

// Same as FirebaseProperty, but the type provides a transform.
// It's kind of cheating and uses the default class.
class SelfFirebaseProperty<in R, T : FirebaseTransform<T>>(
        private val root: FirebaseRefSnap,
        private val default: T)
    : ReadWriteProperty<R, T> {
    private var field: T? = default.fromFirebase(root.snap)

    override operator fun getValue(thisRef: R, property: KProperty<*>): T = field ?: default
    override operator fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        field = value
        default.toFirebase(value, root.ref)
    }
}

// Same as BoundFirebaseProperty, but the type provides a transform.
// It's kind of cheating and uses the default class.
class SelfBoundFirebaseProperty<in R, T : FirebaseTransform<T>>(
        private val root: FirebaseRefSnap,
        private val default: T) {
    operator fun provideDelegate(
            thisRef: R,
            prop: KProperty<*>
    ): ReadWriteProperty<R, T> = SelfFirebaseProperty(root.child(prop.name), default)
}

/**
 * Retrieves a [FirebaseRefSnap] via [firebaseListen].
 *
 * @param path The Firebase path to retrieve.
 * @param transform Turns a [FirebaseRefSnap] into [T].
 * @param callback The function to call after [transform].
 */
class FromFirebase<in R, T>(
        path: String,
        transform: (FirebaseRefSnap) -> T,
        callback: () -> Unit = {})
    : ReadOnlyProperty<R, T?> {
    private var field: T? = null

    init {
        firebaseListen(path) {
            field = transform(it)
            callback()
        }
    }

    override fun getValue(thisRef: R, property: KProperty<*>): T? = field
}

/**
 * Retrieves a [FirebaseRefSnap] and calls [thisRef.update] on the property-containing
 * instance once it is retrieved via [firebaseListen].
 *
 * @param path The Firebase path to retrieve.
 * @param transform Turns a [FirebaseRefSnap] into [T].
 */
class FromFirebaseAndUpdate<in R : Updatable, T>(
        private val path: String,
        private val transform: (FirebaseRefSnap) -> T) {
    operator fun provideDelegate(
            thisRef: R,
            prop: KProperty<*>
    ): ReadOnlyProperty<R, T?> = FromFirebase(path, transform) { thisRef.update() }
}
