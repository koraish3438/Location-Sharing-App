🔷 PROJECT OVERVIEW (Big Picture)

এই app-এর কাজ হলো:

✅ User Sign In / Sign Up করবে
✅ নিজের current location Firebase-এ save হবে
✅ Friend list এ সবাইকে দেখাবে
✅ Friend-দের location থাকলে RecyclerView-এ দেখাবে
✅ Google Map-এ marker আকারে friend-দের location দেখাবে

🔷 ARCHITECTURE: MVVM
UI (Activity / Adapter)
        ↓
ViewModel
        ↓
Firebase (Firestore + Auth)

কেন MVVM?

UI clean থাকে

Business logic আলাদা থাকে

Firebase / Location logic UI থেকে আলাদা

Easy to debug & scalable (job-ready)

🔷 DATA FLOW (Simple Language)
Location Update Flow
FriendListActivity
 → LocationViewModel
 → FusedLocationProviderClient
 → FirestoreViewModel
 → Firebase Firestore

Friend List / Map Data Flow
Firebase Firestore
 → FirestoreViewModel
 → FriendListActivity / GoogleMapActivity
 → RecyclerView / Google Map Marker

🔷 MODEL LAYER
📄 AppUser.kt
data class AppUser(
    val userId: String = "",
    val userEmail: String = "",
    val displayName: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

কাজ কী?

👉 Firebase Firestore এর users document কে Kotlin object বানায়

কেন nullable latitude/longitude?

👉 নতুন user হলে location এখনো set হয়নি

🔷 VIEWMODEL LAYER
📄 FirestoreViewModel.kt
🔹 Role

👉 Firebase Firestore এর সাথে সব communication করে

1️⃣ saveUser()
fun saveUser(userId: String, displayName: String, email: String)


📌 SignUp এর সময়:

নতুন user তৈরি করে

latitude / longitude = null রাখে

2️⃣ getAllUsers()
fun getAllUsers(callback: (List<AppUser>) -> Unit)


📌 Friend list + Google Map এর backbone
📌 Firebase থেকে সব user এনে:

AppUser object বানায়

UI-তে পাঠায়

3️⃣ updateUserLocation()
fun updateUserLocation(userId: String, latitude: Double, longitude: Double)


📌 FloatingActionButton click করলে:

current user এর latitude & longitude update হয়

4️⃣ getUser()
fun getUser(userId: String, callback: (AppUser?) -> Unit)


📌 Profile screen-এ single user load করার জন্য

📄 LocationViewModel.kt
🔹 Role

👉 Device এর GPS location handle করে

1️⃣ initClient()
fun initClient(client: FusedLocationProviderClient)


📌 Activity থেকে location client inject করা হয়
📌 MVVM rule maintain করে

2️⃣ getLastLocation()
fun getLastLocation(callback: (Double?, Double?) -> Unit)


📌 Permission check করে
📌 GPS থেকে:

latitude

longitude
return করে

❌ Location off / unavailable → null দেয়

🔷 VIEW (UI) LAYER
📄 FriendListActivity.kt
🔹 Main Screen of App
1️⃣ onCreate()

Firebase login check

Location client initialize

RecyclerView setup

Drawer setup

Permission request

2️⃣ FloatingActionButton (mapBtn)
binding.mapBtn.setOnClickListener {
    getLocation()
}


📌 Click করলে:

location নেয়

Firebase update করে

friend list refresh হয়

3️⃣ getLocation()
locationViewModel.getLastLocation { lat, lng -> }


📌 LocationViewModel থেকে data নেয়
📌 FirestoreViewModel দিয়ে Firebase update করে

4️⃣ setupRecyclerView()
fireStoreViewModel.getAllUsers { users ->
    userAdapter.updateData(users)
}


📌 Firebase → ViewModel → RecyclerView

5️⃣ Drawer Menu

Profile

Map

Logout

📄 FriendAdapter.kt
🔹 RecyclerView Adapter
onBindViewHolder()
if (latitude != null && longitude != null)
    "Lat: x, Lng: y"
else
    "Location not available"


📌 Firebase data অনুযায়ী UI update হয়
📌 No crash (null safe)

📄 GoogleMapActivity.kt
🔹 Map Screen
onMapReady()
fireStoreViewModel.getAllUsers { userList -> }


📌 Firebase থেকে সব user নেয়
📌 যাদের location আছে:

Marker add করে

Camera move করে

🔷 XML FILES (Short Explanation)
activity_friend_list.xml

DrawerLayout

Toolbar

RecyclerView

FloatingActionButton

item_friend.xml

Name

Email

Location Text

activity_google_map.xml

SupportMapFragment

🔷 WHY PROBLEMS হচ্ছিল আগে?
Problem	Root Cause
Location not available	Location client init করা ছিল না
Marker দেখাচ্ছিল না	Firebase এ latitude/longitude null
FAB কাজ করছিল না	getLocation() Firebase update করছিল না
Map empty	valid user filter করা হয়নি

👉 এখন সব fix করা হয়েছে ✅

🔷 INTERVIEW READY EXPLANATION (1 Line)

“This project follows MVVM architecture where UI communicates with ViewModel, ViewModel handles business logic and Firebase interaction, and real-time location is fetched via Location Services and stored in Firestore, then displayed in RecyclerView and Google Map.”
