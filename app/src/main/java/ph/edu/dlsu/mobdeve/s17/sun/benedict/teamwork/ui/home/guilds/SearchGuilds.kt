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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.adapters.AllGuildsAdapter
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.GuildDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.UserDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentSearchGuildsBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Guild

/**
 *  Displays the list of all currently existing guilds in a RecyclerView.
 */
class SearchGuilds : Fragment() {
    private lateinit var binding : FragmentSearchGuildsBinding
    private val TAG : String = "SearchGuildsFragment"

    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firestoreCollection = "guilds"

    private lateinit var guildsAdapter: AllGuildsAdapter
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
            guildsAdapter.setData(guilds)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchGuildsBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize DAO
        guildDAO = GuildDAO(requireContext())

        // Initialize empty guilds
        guilds = ArrayList()
        guildsAdapter = context?.let { AllGuildsAdapter(guilds, it) }!!

        // Inflate recycler view
        binding.rvSearchGuilds.layoutManager = LinearLayoutManager(activity)
        binding.rvSearchGuilds.adapter = guildsAdapter

        // Register broadcast receiver
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(broadcastReceiver, IntentFilter("ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.guilds"))

        guildDAO.getAllGuilds("KZpVJ7lrdSO2DMSpm9nK")

        return view
    }

    // Unregister broadcast receiver
    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
    }
}