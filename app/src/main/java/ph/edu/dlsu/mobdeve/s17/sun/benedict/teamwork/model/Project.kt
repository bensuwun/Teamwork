package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model

import java.io.Serializable

/**
 * This is the project data class, where an instance of Project is stored.
 * @author Adriel Isaiah V. Amoguis
 *
 * @constructor The primary constructor accepts the name of the project as the only parameter.
 * The project name is the only required attribute to create a project. A more specified constructor
 * is declared within the body that will handle instance generation from the data access object from Firestore.
 *
 * @param name The project name.
 */
data class Project(val name: String) : Serializable {

}