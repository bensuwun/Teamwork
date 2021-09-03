package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model

class GuildMember {
    var guildID : String = ""
    var userID : String = ""

    constructor(guildID: String, userID: String) {
        this.guildID = guildID
        this.userID = userID
    }

    constructor(){
        this.guildID = ""
        this.userID = ""
    }


}