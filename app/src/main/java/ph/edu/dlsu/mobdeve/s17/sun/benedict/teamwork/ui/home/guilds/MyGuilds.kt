package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.guilds

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.*
import kotlinx.coroutines.*
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.adapters.AllGuildsAdapter
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.adapters.MyGuildsAdapter
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.GuildDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.GuildMemberDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentMyGuildsBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Guild

/**
 * A simple [Fragment] subclass.
 */
class MyGuilds : Fragment() {
    private lateinit var binding : FragmentMyGuildsBinding
    private var TAG : String = "MyGuildsFragment"

    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firestoreCollection = "guilds"

    private lateinit var adapter: MyGuildsAdapter
    private lateinit var guilds : ArrayList<Guild>
    private lateinit var guildDAO : GuildDAO
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("SearchGuilds", "Broadcast Received")
            var bundle = intent?.extras
            var guilds : ArrayList<Guild> = bundle!!.getParcelableArrayList("guilds")!!
            for(guild in guilds) {
                Log.d("SearchGuilds", guild.name)
            }
            adapter.setData(guilds)
        }
    }

    //private lateinit var adapter : MyGuildsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMyGuildsBinding.inflate(inflater, container, false);
        val view = binding.root

        // Initialize DAO
        guildDAO = GuildDAO(requireContext())

        // Initialize empty array list
        guilds = ArrayList()
        adapter = context?.let { MyGuildsAdapter(guilds, it) }!!

        binding.rvMyGuilds.layoutManager = LinearLayoutManager(context)
        binding.rvMyGuilds.adapter = adapter


        // Register broadcast receiver
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(broadcastReceiver, IntentFilter("ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.my_guilds"))

        // TODO: Get logged in user's userID
        guildDAO.getMyGuilds("KZpVJ7lrdSO2DMSpm9nK")

        return view
    }

}