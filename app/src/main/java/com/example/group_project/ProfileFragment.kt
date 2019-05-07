package com.example.group_project


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ProfileFragment : Fragment() {

    private lateinit var logoutButton: Button
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var userName: TextView
    private lateinit var email: TextView

    var user: User? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_profile, container, false)

        logoutButton = view.findViewById(R.id.logout)
        firebaseAuth = FirebaseAuth.getInstance()

        userName = view.findViewById(R.id.username)
        email = view.findViewById(R.id.email)

        val uid = FirebaseAuth.getInstance().currentUser?.uid

        val ref = FirebaseDatabase.getInstance().getReference("users/" + uid)



        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                //Does nothing
            }

            override fun onDataChange(p0: DataSnapshot?) {

                user = p0?.getValue(User::class.java)


                userName.text = user?.username
                email.text = user?.emailAddress


            }

        })  // This gets the user object of the current User


        logoutButton.setOnClickListener {

            firebaseAuth.signOut()
            activity?.finish()
            startActivity(Intent(activity, LoginActivity::class.java))
        }


        return view
    }



}
