package com.bekk.queuingtrackingsystem

class Host {

    var user : String? = null
    var code : String? = null

    constructor()

    constructor(user: String, code : String){
        this.code = code
        this.user = user
    }

}