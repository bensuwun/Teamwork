package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model

class GuildMember {
    var guildId : String = ""
    var userAuthUid : String = ""

    constructor(guildID: String, userAuthUid: String) {
        this.guildId = guildID
        this.userAuthUid = userAuthUid
    }

    constructor(){
        this.guildId = ""
        this.userAuthUid = ""
    }

}