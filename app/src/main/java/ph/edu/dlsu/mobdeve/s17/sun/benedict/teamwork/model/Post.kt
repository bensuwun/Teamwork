package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model

import com.google.firebase.Timestamp
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R

class Post {
    var author: User
    var title: String
    var description: String
    var likes: Number
    var comments: Number
    var date_posted: Timestamp

    constructor(
        author: User,
        title: String,
        description: String,
        likes: Number,
        comments: Number,
        date_posted: Timestamp
    ) {
        this.author = author
        this.title = title
        this.description = description
        this.likes = likes
        this.comments = comments
        this.date_posted = date_posted
    }

    companion object {
        fun initSampleData() : ArrayList<Post>{
            var posts : ArrayList<Post> = ArrayList()
            posts.add(Post(User(
                "redstoneadi",
                "password"),
                "Difficulty with Pushups",
                "I want to somehow be as strong as Saitama, he’s my biggest idol! But I can’t seem to do push-ups properly, any tips?",
                0,
                0,
                Timestamp(1629903088, 0)))
            posts.add(Post(User(
                "redstoneadi",
                "password"),
                "Difficulty with Pushups",
                "I want to somehow be as strong as Saitama, he’s my biggest idol! But I can’t seem to do push-ups properly, any tips?",
                0,
                0,
                Timestamp(1629903088, 0)))
            return posts
        }
    }
}