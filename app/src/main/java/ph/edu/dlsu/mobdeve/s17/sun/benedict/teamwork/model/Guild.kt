package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model

class Guild {
    var name : String
    var description : String
    lateinit var guild_dp : String
    var member_count : Int

    constructor(name : String, description : String, guild_dp : String, member_count : Int){
        this.name = name
        this.description = description
        this.guild_dp = guild_dp
        this.member_count = member_count
    }

    constructor(name: String, description: String, member_count: Int) {
        this.name = name
        this.description = description
        this.member_count = member_count
    }

    companion object {
        fun initSampleData() : ArrayList<Guild>{
            var guilds : ArrayList<Guild> = ArrayList()
            guilds.add(Guild("Test Guild 1", "First Guild", 142412))
            guilds.add(Guild("Test Guild 2", "Second Guild", 1231241))
            guilds.add(Guild("Test Guild 3", "Third Guild", 25130313))
            guilds.add(Guild("Test Guild X", "X Guild", 25130313))
            guilds.add(Guild("Test Guild X", "X Guild", 25130313))
            guilds.add(Guild("Test Guild X", "X Guild", 25130313))
            guilds.add(Guild("Test Guild X", "X Guild X Guild X Guild X Guild X Guild X Guild X Guild X Guild X Guild ", 25130313))
            return guilds
        }
    }
}